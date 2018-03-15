package info.kilchhofer.bfh.lidar.consoleuiservant;

import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageReceiver;
import info.kilchhofer.bfh.lidar.consoleuiservant.contract.ConsoleUIServantContract;
import info.kilchhofer.bfh.lidar.consoleuiservice.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.intent.ConsoleIntent;
import info.kilchhofer.bfh.lidar.service.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.ConsoleUIServiceContract;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUIServant {

    LidarServiceContract lidarServiceContract;
    Set<ConsoleUIServiceContract> consoleUIServiceInstances;
    GatewayClient gatewayClient;
    private static final Logger LOGGER = Logger.getLogger(ConsoleUIServant.class.getName());

    public ConsoleUIServant(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        this.gatewayClient = new GatewayClient<ConsoleUIServantContract>(mqttURI, mqttClientName, new ConsoleUIServantContract(instanceName));
        this.consoleUIServiceInstances = new HashSet<>();
        this.gatewayClient.connect();
        this.lidarServiceContract = new LidarServiceContract(instanceName);


        this.gatewayClient.subscribe("Robocup/LidarConsoleUI/U/+/S/connection", (topic, payload) -> {
            System.out.println("Payload: " + new String(payload));
            ConnectionStatus status = new TreeSet<ConnectionStatus>(gatewayClient.toMessageSet(payload, ConnectionStatus.class)).last();
            String consoleUIServiceInstance = topic.replaceFirst("Robocup/LidarConsoleUI/U/", "").replaceFirst("/S/connection", "");
            ConsoleUIServiceContract consoleUIServiceContract = new ConsoleUIServiceContract(consoleUIServiceInstance);

            if (status.value.equals("online")) {
                ConsoleIntent consoleIntent = new ConsoleIntent();
                consoleIntent.consoleMessage = "Servant online";
                LidarIntent lidarIntent = new LidarIntent();

                this.gatewayClient.readyToPublish(consoleUIServiceContract.INTENT, consoleIntent);
                this.consoleUIServiceInstances.add(consoleUIServiceContract);

                this.gatewayClient.subscribe(consoleUIServiceContract.EVENT_KEYPRESS, (eventTopic, eventPayload) -> {

                    Set<ConsoleKeyPressEvent> consoleKeyPressEvents = gatewayClient.toMessageSet(eventPayload, ConsoleKeyPressEvent.class);
                    for (ConsoleKeyPressEvent consoleKeyPressEvent : consoleKeyPressEvents) {
                        System.out.println("Event Payload: " + consoleKeyPressEvent.character);
                        switch (consoleKeyPressEvent.character) {
                            case 's':
                                System.out.println("single");
                                lidarIntent.command = LidarCommand.SINGLE_MEAS;
                                break;
                            case 'e':
                                System.out.println("enable");
                                lidarIntent.command = LidarCommand.CONT_MEAS_START;
                                break;
                            case 'd':
                                System.out.println("disable");
                                lidarIntent.command = LidarCommand.CONT_MEAS_STOP;
                                break;
                        }

                    }

                    this.gatewayClient.readyToPublish(lidarServiceContract.INTENT, lidarIntent);
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

        ConsoleUIServant consoleUIServant = new ConsoleUIServant(mqttURI, "LidarServant" + computerName, computerName);

        System.in.read();
    }
}
