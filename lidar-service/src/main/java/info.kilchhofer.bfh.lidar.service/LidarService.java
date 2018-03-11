package info.kilchhofer.bfh.lidar.service;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import com.google.common.primitives.Ints;
import laser.scanner.*;
import laser.scanner.tim55x.TiM55x;
import laser.scanner.tim55x.com.ComNotRunningException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LidarService {
    private IScan scanner;
    private final GatewayClient<LidarServiceContract> gatewayClient;
    private static final Logger LOGGER = Logger.getLogger(LidarService.class.getName());

    public LidarService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException, IOException {
        this.gatewayClient = new GatewayClient<LidarServiceContract>(mqttURI, mqttClientName, new LidarServiceContract(instanceName));
        IScanListener iScanListener = new IScanListener() {
            @Override
            public void newMeasData(IScanMeasData iScanMeasData) {
                //System.out.println(Arrays.toString(iScanMeasData.getAllDistanceValues()));
                ArrayList<Integer> distanceValues = new ArrayList<Integer>(Ints.asList(iScanMeasData.getAllDistanceValues()));
                gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_MEASUREMENT, new LidarMeasurementEvent(distanceValues));
            }
        };

        IScanOperator iScanOperator = new IScanOperator() {
            @Override
            public void newStateActice(State state) {
                LOGGER.log(Level.INFO, "New laser state active: " + state);
                gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_STATE, new LidarState(state));
            }

            @Override
            public void errorOccured() {
                LOGGER.log(Level.SEVERE, "Laser ERROR");
            }
        };

        this.scanner = new TiM55x(iScanListener, iScanOperator, "192.168.91.2", 2112);
        this.gatewayClient.connect();
        this.gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (topic, payload) -> {

            try {

                for(LidarIntent intent : gatewayClient.toMessageSet(payload, LidarIntent.class)){
                    LOGGER.log(Level.INFO, "Received intent: " + intent);
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
                LOGGER.log(Level.SEVERE, null, e);
            } catch (ComNotRunningException e) {
                LOGGER.log(Level.SEVERE, null, e);
            } catch (LaserScanStateException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }

        });
    }

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        LidarService lidarService = new LidarService(mqttURI, "Lidar" + computerName, computerName);

        System.in.read();
    }
}
