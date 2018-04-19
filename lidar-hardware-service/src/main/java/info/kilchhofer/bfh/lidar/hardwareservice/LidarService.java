package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import com.google.common.primitives.Ints;
import laser.scanner.*;
import laser.scanner.tim55x.TiM55x;
import laser.scanner.tim55x.com.ComNotRunningException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LidarService {
    private IScan scanner;
    private final GatewayClient<LidarServiceContract> gatewayClient;
    private static final Logger logger = LogManager.getLogger(LidarService.class);

    public LidarService(URI mqttURI, String mqttClientName, String instanceName, String lidarIp, int lidarPort) throws MqttException, IOException {
        this.gatewayClient = new GatewayClient<LidarServiceContract>(mqttURI, mqttClientName, new LidarServiceContract(instanceName));
        IScanListener iScanListener = new IScanListener() {
            @Override
            public void newMeasData(IScanMeasData iScanMeasData) {
                ArrayList<Integer> distanceValues = new ArrayList<Integer>(Ints.asList(iScanMeasData.getAllDistanceValues()));
                ArrayList<Integer> rssiValues = new ArrayList<Integer>(Ints.asList(iScanMeasData.getAllRSSIValues()));
                gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_MEASUREMENT, new LidarMeasurementEvent(distanceValues, rssiValues));
            }
        };

        IScanOperator iScanOperator = new IScanOperator() {
            @Override
            public void newStateActice(State state) {
                logger.info("{}: New laser state active: {}", instanceName, state);
                gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_STATE, new LidarState(state));
            }

            @Override
            public void errorOccured() {
                logger.error("{}: Laser ERROR", instanceName);
            }
        };

        this.scanner = new TiM55x(iScanListener, iScanOperator, lidarIp, lidarPort);
        this.gatewayClient.connect();
        this.gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (topic, payload) -> {

            try {

                for(LidarIntent intent : gatewayClient.toMessageSet(payload, LidarIntent.class)){
                    logger.log(Level.INFO, "{}: Received intent: {} ", instanceName, intent);
                    switch (intent.command){
                        case CONT_MEAS_START:
                            this.scanner.startContMeas();
                            break;
                        case CONT_MEAS_STOP:
                            this.scanner.stopContMeas();
                            break;
                        case SINGLE_MEAS:
                            this.scanner.runSingleMeas();
                            break;
                    }
                }

            } catch (IOException e) {
                logger.error((String)null, e);
            } catch (ComNotRunningException e) {
                logger.error((String)null, e);
            } catch (LaserScanStateException e) {
                logger.error((String)null, e);
            }

        });
    }
}
