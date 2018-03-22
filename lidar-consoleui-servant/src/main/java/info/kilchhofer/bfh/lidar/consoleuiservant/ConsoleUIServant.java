package info.kilchhofer.bfh.lidar.consoleuiservant;

import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import info.kilchhofer.bfh.lidar.consoleuiservant.contract.ConsoleUIServantContract;
import info.kilchhofer.bfh.lidar.consoleuiservice.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.intent.ConsoleIntent;
import info.kilchhofer.bfh.lidar.hardwareservice.*;
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

    private static final Logger logger = LogManager.getLogger(ConsoleUIServant.class);
    private LidarServiceContract lidarServiceContract;
    private Set<ConsoleUIServiceContract> consoleUIServiceInstances;
    private GatewayClient gatewayClient;

    // Do not hardcode topics for autodetect service instances
    private final ConsoleUIServiceContract allConsoleUIContracts = new ConsoleUIServiceContract("+");
    private final LidarServiceContract tempLidarServiceContract = new LidarServiceContract("+");

    public ConsoleUIServant(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        this.gatewayClient = new GatewayClient<ConsoleUIServantContract>(mqttURI, mqttClientName, new ConsoleUIServantContract(instanceName));
        this.consoleUIServiceInstances = new HashSet<>();
        this.gatewayClient.connect();

        handleConsoleUI();
        handleLidarHardware();
    }

    private void handleConsoleUI(){
        // Subscribe to all console UI instances
        this.gatewayClient.subscribe(allConsoleUIContracts.STATUS_CONNECTION, (topic, payload) -> {
            logger.trace("Payload: " + new String(payload));
            ConnectionStatus status = new TreeSet<ConnectionStatus>(gatewayClient.toMessageSet(payload, ConnectionStatus.class)).last();
            ConsoleUIServiceContract consoleUIServiceContract = new ConsoleUIServiceContract(topic, true);

            if (status.value.equals("online")) {
                logger.info("Instance {} online", consoleUIServiceContract.INSTANCE);
                ConsoleIntent consoleIntent = new ConsoleIntent();
                consoleIntent.consoleMessage = "Servant online";

                this.gatewayClient.readyToPublish(consoleUIServiceContract.INTENT, consoleIntent);
                this.consoleUIServiceInstances.add(consoleUIServiceContract);

                this.gatewayClient.subscribe(consoleUIServiceContract.EVENT_KEYPRESS, (eventTopic, eventPayload) -> {

                    Set<ConsoleKeyPressEvent> consoleKeyPressEvents = gatewayClient.toMessageSet(eventPayload, ConsoleKeyPressEvent.class);
                    for (ConsoleKeyPressEvent consoleKeyPressEvent : consoleKeyPressEvents) {
                        logger.trace("Event Payload: " + consoleKeyPressEvent.character);
                        LidarIntent lidarIntent = new LidarIntent();

                        Character receivedChar = toLowerCase(consoleKeyPressEvent.character);
                        switch (receivedChar) {
                            case 's':
                                lidarIntent.command = LidarCommand.SINGLE_MEAS;
                                break;
                            case 'e':
                                lidarIntent.command = LidarCommand.CONT_MEAS_START;
                                break;
                            case 'd':
                                lidarIntent.command = LidarCommand.CONT_MEAS_STOP;
                                break;
                            default:
                                logger.warn("Received unknown command '{}'", receivedChar);

                        }
                        if(lidarIntent.command != null) {
                            logger.info("Received '{}'. Send Intent '{}' to Hardware Service.",
                                    receivedChar,
                                    lidarIntent.command.toString());
                            this.gatewayClient.readyToPublish(lidarServiceContract.INTENT, lidarIntent);
                        }
                    }
                });
            } else {
                logger.info("Instance {} offline", consoleUIServiceContract.INSTANCE);
                consoleUIServiceInstances.remove(consoleUIServiceContract);
            }
        });
    }

    private void handleLidarHardware(){
        // Subscribe to all console UI instances
        this.gatewayClient.subscribe(tempLidarServiceContract.STATUS_CONNECTION, (topic, payload) -> {
            logger.trace("Payload: " + new String(payload));
            ConnectionStatus status = new TreeSet<ConnectionStatus>(gatewayClient.toMessageSet(payload, ConnectionStatus.class)).last();
            this.lidarServiceContract = new LidarServiceContract(topic, true);

            if (status.value.equals("online")) {
                logger.info("Hardware Instance online: {}", lidarServiceContract.INSTANCE);

                // Subscribe to hardware measurement events
                this.gatewayClient.subscribe(lidarServiceContract.EVENT_MEASUREMENT, (eventTopic, eventPayload) ->{
                    Set<LidarMeasurementEvent> lidarMeasurementEvents = gatewayClient.toMessageSet(eventPayload, LidarMeasurementEvent.class);
                    for(LidarMeasurementEvent lidarMeasurementEvent : lidarMeasurementEvents) {
                        consoleUIServiceInstances.forEach((instance) -> {
                            ConsoleIntent consoleIntent = new ConsoleIntent();
                            consoleIntent.consoleMessage = lidarMeasurementEvent.toString();
                            this.gatewayClient.readyToPublish(instance.INTENT, consoleIntent);
                        });
                    }
                });

                // Subscribe to hardware status
                this.gatewayClient.subscribe(lidarServiceContract.STATUS_STATE, (statusTopic, statusPayload) -> {
                    logger.trace("STATUS_STATE Payload: " + statusPayload);
                    Set<LidarState> lidarStates = gatewayClient.toMessageSet(statusPayload, LidarState.class);
                    for(LidarState lidarState : lidarStates) {
                        consoleUIServiceInstances.forEach((instance) -> {
                            ConsoleIntent consoleIntent = new ConsoleIntent();
                            consoleIntent.consoleMessage = "Sensor " + lidarState.state;
                            this.gatewayClient.readyToPublish(instance.INTENT, consoleIntent);
                        });
                    }
                });
            } else {
                logger.info("Hardware Instance offline: {}", lidarServiceContract.INSTANCE);
            }
        });
    }

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            logger.error("undefined hostname", ex);
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
        logger.info("{} will be used as broker address.", mqttURI);

        String mqttClientName = "LidarServant@" + computerName;
        String instanceName = mqttClientName;

        ConsoleUIServant consoleUIServant = new ConsoleUIServant(mqttURI, mqttClientName, instanceName);

        System.in.read();
    }
}
