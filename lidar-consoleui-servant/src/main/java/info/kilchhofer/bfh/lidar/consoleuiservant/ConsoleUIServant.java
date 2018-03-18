package info.kilchhofer.bfh.lidar.consoleuiservant;

import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import info.kilchhofer.bfh.lidar.consoleuiservant.contract.ConsoleUIServantContract;
import info.kilchhofer.bfh.lidar.consoleuiservice.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.intent.ConsoleIntent;
import info.kilchhofer.bfh.lidar.hardwareservice.LidarCommand;
import info.kilchhofer.bfh.lidar.hardwareservice.LidarIntent;
import info.kilchhofer.bfh.lidar.hardwareservice.LidarMeasurementEvent;
import info.kilchhofer.bfh.lidar.hardwareservice.LidarServiceContract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.ConsoleUIServiceContract;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.Character.toLowerCase;

public class ConsoleUIServant {

    LidarServiceContract lidarServiceContract;
    Set<ConsoleUIServiceContract> consoleUIServiceInstances;
    GatewayClient gatewayClient;
    private static final Logger logger = LogManager.getLogger(ConsoleUIServant.class);

    public ConsoleUIServant(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        this.gatewayClient = new GatewayClient<ConsoleUIServantContract>(mqttURI, mqttClientName, new ConsoleUIServantContract(instanceName));
        this.consoleUIServiceInstances = new HashSet<>();
        this.gatewayClient.connect();
        this.lidarServiceContract = new LidarServiceContract(instanceName);


        this.gatewayClient.subscribe("Robocup/LidarConsoleUI/U/+/S/connection", (topic, payload) -> {
            logger.trace("Payload: " + new String(payload));
            ConnectionStatus status = new TreeSet<ConnectionStatus>(gatewayClient.toMessageSet(payload, ConnectionStatus.class)).last();
            String consoleUIServiceInstance = topic.replaceFirst("Robocup/LidarConsoleUI/U/", "").replaceFirst("/S/connection", "");
            ConsoleUIServiceContract consoleUIServiceContract = new ConsoleUIServiceContract(consoleUIServiceInstance);

            if (status.value.equals("online")) {
                ConsoleIntent consoleIntent = new ConsoleIntent();
                consoleIntent.consoleMessage = "Servant online";

                this.gatewayClient.readyToPublish(consoleUIServiceContract.INTENT, consoleIntent);
                this.consoleUIServiceInstances.add(consoleUIServiceContract);

                this.gatewayClient.subscribe(consoleUIServiceContract.EVENT_KEYPRESS, (eventTopic, eventPayload) -> {

                    Set<ConsoleKeyPressEvent> consoleKeyPressEvents = gatewayClient.toMessageSet(eventPayload, ConsoleKeyPressEvent.class);
                    for (ConsoleKeyPressEvent consoleKeyPressEvent : consoleKeyPressEvents) {
                        logger.trace("Event Payload: " + consoleKeyPressEvent.character);
                        LidarIntent lidarIntent = new LidarIntent();

                        switch (toLowerCase(consoleKeyPressEvent.character)) {
                            case 's':
                                logger.info(LidarCommand.SINGLE_MEAS.toString());
                                lidarIntent.command = LidarCommand.SINGLE_MEAS;
                                break;
                            case 'e':
                                logger.info(LidarCommand.CONT_MEAS_START.toString());
                                lidarIntent.command = LidarCommand.CONT_MEAS_START;
                                break;
                            case 'd':
                                logger.info(LidarCommand.CONT_MEAS_STOP.toString());
                                lidarIntent.command = LidarCommand.CONT_MEAS_STOP;
                                break;
                            default:

                        }
                        if(lidarIntent.command != null) {
                            this.gatewayClient.readyToPublish(lidarServiceContract.INTENT, lidarIntent);
                        }

                    }

                });
            } else {
                consoleUIServiceInstances.remove(consoleUIServiceInstance);
            }
        });

        this.gatewayClient.subscribe(lidarServiceContract.EVENT_MEASUREMENT, (topic, payload) ->{
            Set<LidarMeasurementEvent> lidarMeasurementEvents = gatewayClient.toMessageSet(payload, LidarMeasurementEvent.class);
            for(LidarMeasurementEvent lidarMeasurementEvent : lidarMeasurementEvents) {
                consoleUIServiceInstances.forEach((instance) -> {
                    ConsoleIntent consoleIntent = new ConsoleIntent();
                    consoleIntent.consoleMessage = lidarMeasurementEvent.toString();
                    this.gatewayClient.readyToPublish(instance.INTENT, consoleIntent);
                });
            }
        });

    }

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.error((String) null, ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        System.out.println("Loglevel= " + logger.getLevel());
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            logger.info("Per default, 'tcp://127.0.0.1:1883' is chosen. You can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883");
        }
        logger.info(mqttURI+" will be used as broker address.");

        ConsoleUIServant consoleUIServant = new ConsoleUIServant(mqttURI, "LidarServant" + computerName, computerName);

        System.in.read();
    }
}
