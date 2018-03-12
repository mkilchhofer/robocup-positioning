package info.kilchhofer.bfh.lidar.consoleuiservice;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.ConsoleUIServiceContract;
import info.kilchhofer.bfh.lidar.consoleuiservice.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.helper.KeyPressHandler;
import info.kilchhofer.bfh.lidar.consoleuiservice.intent.ConsoleIntent;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleUIService {

    private final GatewayClient<ConsoleUIServiceContract> gatewayClient;
    private static final Logger LOGGER = Logger.getLogger(ConsoleUIService.class.getName());
    private KeyPressHandler keyPressHandler;

    public ConsoleUIService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException, IOException, InterruptedException {
        this.gatewayClient = new GatewayClient<ConsoleUIServiceContract>(mqttURI, mqttClientName, new ConsoleUIServiceContract(instanceName));

        this.gatewayClient.connect();
        this.gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (topic, payload) -> {
            for(ConsoleIntent intent : gatewayClient.toMessageSet(payload, ConsoleIntent.class)){
                System.out.println(intent.consoleMessage);
            }
        });

        keyPressHandler = new KeyPressHandler(character -> {
            System.out.println(character);
            gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_KEYPRESS, new ConsoleKeyPressEvent(character));
        });
        Thread keyPressHandlerThread = new Thread(keyPressHandler);
        keyPressHandlerThread.start();
    }

    private static String computerName;

    static {
        System.setProperty("java.awt.headless", "true");
        System.out.println("Headless: " + java.awt.GraphicsEnvironment.isHeadless());
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

        ConsoleUIService consoleUIService = new ConsoleUIService(mqttURI, "ConsoleUI" + computerName, computerName);
    }
}
