package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import com.google.common.primitives.Ints;
import laser.scanner.*;
import laser.scanner.tim55x.TiM55x;
import laser.scanner.tim55x.com.ComNotRunningException;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.core.util.ArrayUtils;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
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
                gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_MEASUREMENT, new LidarMeasurementEvent(distanceValues));
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

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.error((String)null, ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        System.out.println("Loglevel= " + logger.getLevel());
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");

        Options options = new Options();
        Option broker = new Option("b", "broker", true, "brocker to use, format: tcp://127.0.0.1:1883");
        broker.setRequired(false);
        options.addOption(broker);

        Option sensors = new Option("s", "sensor", true, "sensor address");
        sensors.setRequired(false);
        sensors.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(sensors);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        String brokerArg = cmd.getOptionValue("broker");
        String[] sensorArgs = cmd.getOptionValues("sensor");

        if (brokerArg != null) {
            mqttURI = URI.create(brokerArg);
        } else {
            logger.info("Per default, 'tcp://127.0.0.1:1883' is chosen. You can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883");
        }

        ArrayList<String> sensorArgsList = new ArrayList<>();
        if (sensorArgs != null) {
            sensorArgsList.addAll(Arrays.asList(sensorArgs));
        } else {
            sensorArgsList.add("192.168.91.2");
            logger.info("Per default, we expect our lidar sensor on IP '192.168.91.2.");
        }

        logger.info("Configured {} lidar sensors: {}", sensorArgsList.size(), sensorArgsList);
        logger.info("{} will be used as broker address.", mqttURI);

        int lidarPort = 2112;

        for (String lidarIp : sensorArgsList) {
            String mqttClientName = lidarIp + "@" + computerName;
            String instanceName = mqttClientName;

            LidarService lidarService = new LidarService(mqttURI, mqttClientName, instanceName, lidarIp, lidarPort);
        }
        System.in.read();
    }
}
