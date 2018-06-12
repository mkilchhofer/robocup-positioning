package info.kilchhofer.bfh.robocup.positioning.servant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import static info.kilchhofer.bfh.robocup.common.Constants.DEFAULT_BROKER_ADDRESS;

public class LidarEdgeDetectionServantRunner {
    private static final Logger LOGGER = LogManager.getLogger(LidarEdgeDetectionServantRunner.class);
    private static String computerName;

    static {
        LOGGER.info("Headless: " + java.awt.GraphicsEnvironment.isHeadless());
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.error("undefined hostname", ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, IOException {
        System.out.println("Loglevel= " + LOGGER.getLevel());
        URI mqttURI = URI.create(DEFAULT_BROKER_ADDRESS);
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            LOGGER.info("Per default, '{}' is chosen. You can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883", DEFAULT_BROKER_ADDRESS);
        }
        LOGGER.info("{} will be used as broker address.", mqttURI);

        String mqttClientName = "LidarEdgeDetectionServant@" + computerName;
        String instanceName = mqttClientName;

        new LidarEdgeDetectionServant(mqttURI, mqttClientName, instanceName);

        System.in.read();
    }
}
