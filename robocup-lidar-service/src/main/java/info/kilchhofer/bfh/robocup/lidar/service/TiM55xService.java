package info.kilchhofer.bfh.robocup.lidar.service;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageReceiver;

import info.kilchhofer.bfh.robocup.lidar.service.binding.*;
import laser.datahandling.IScanData;
import laser.datahandling.IScanReflectData;
import laser.datahandling.scandata.ScanDataFact;
import laser.scanner.*;
import laser.scanner.tim55x.TiM55x;
import laser.scanner.tim55x.com.ComNotRunningException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TiM55xService {
    private IScan lidarSensor;
    private final GatewayClient<LidarServiceContract> gatewayClient;
    private MessageReceiver intentReceiver;
    private static final Logger LOGGER = LogManager.getLogger(TiM55xService.class);

    public TiM55xService(URI mqttURI, String mqttClientName, String instanceName, String lidarIp, int lidarPort) throws MqttException, IOException {
        this.gatewayClient = new GatewayClient<LidarServiceContract>(mqttURI, mqttClientName, new LidarServiceContract(instanceName));
        IScanListener iScanListener = new IScanListener() {
            @Override
            public void newMeasData(IScanMeasData iScanMeasData) {
                IScanData scanData = ScanDataFact.create(iScanMeasData);

                // Don't use this, as is not fully implemented !
                // This is becasue of 'throw new UnsupportedOperationException("Not supported yet.")' in every Getter
                // Method other than FirmwareVersion and FirmwareVersion is always '1'.
                // -> But if we read the Firmware with SOPAS Engineering Tool it is "V2.54". -> useless
                //IScannerData scannerData = scanData.getScannerData();


                List<IScanReflectData> scanMeasurementData = scanData.getScanMeasurementData();

                ArrayList<Measurement> measurements = new ArrayList<>();
                for(IScanReflectData iScanReflectData : scanMeasurementData){
                    measurements.add(new Measurement(
                            iScanReflectData.getAngle(),
                            iScanReflectData.getDistance(),
                            iScanReflectData.getRSSIValue()
                    ));
                }

                gatewayClient.readyToPublish(
                        gatewayClient.getContract().EVENT_MEASUREMENT,
                        new LidarMeasurementEvent(measurements)
                );
            }
        };

        IScanOperator iScanOperator = new IScanOperator() {
            @Override
            public void newStateActice(State state) {
                LOGGER.info("{}: New laser state active: {}", instanceName, state);
                gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_STATE, new LidarState(state));
            }

            @Override
            public void errorOccured() {
                LOGGER.error("{}: Laser ERROR", instanceName);
            }
        };

        this.intentReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                try {
                    // take only the last Intent from MessageSet
                    LidarIntent intent = gatewayClient.toMessageSet(payload, LidarIntent.class).last();
                    LOGGER.log(Level.INFO, "{}: Received intent: {} ", instanceName, intent);

                    switch (intent.command){
                        case START_CONTINUOUS_MEASUREMENT:
                            lidarSensor.startContMeas();
                            break;
                        case STOP_CONTINUOUS_MEASUREMENT:
                            lidarSensor.stopContMeas();
                            break;
                        case SINGLE_MEASUREMENT:
                            lidarSensor.runSingleMeas();
                            break;
                    }

                } catch (IOException e) {
                    LOGGER.error((String)null, e);
                } catch (ComNotRunningException e) {
                    LOGGER.error((String)null, e);
                } catch (LaserScanStateException e) {
                    LOGGER.error((String)null, e);
                }
            }
        };

        this.lidarSensor = new TiM55x(iScanListener, iScanOperator, lidarIp, lidarPort);
        this.gatewayClient.connect();

        this.gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", this.intentReceiver);
    }
}
