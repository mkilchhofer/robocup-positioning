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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConsoleUIService {

    private final GatewayClient<ConsoleUIServiceContract> gatewayClient;
    private static final Logger logger = LogManager.getLogger(ConsoleUIService.class);
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
            gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_KEYPRESS, new ConsoleKeyPressEvent(character));
        });
        Thread keyPressHandlerThread = new Thread(keyPressHandler);
        keyPressHandlerThread.start();
    }
}
