package info.kilchhofer.bfh.lidar.consoleuiservice;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.ConsoleUIServiceContract;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.intent.ConsoleIntent;
import info.kilchhofer.bfh.lidar.consoleuiservice.helper.KeyPressHandler;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;

public class ConsoleUIService {

    private final GatewayClient<ConsoleUIServiceContract> gatewayClient;
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
