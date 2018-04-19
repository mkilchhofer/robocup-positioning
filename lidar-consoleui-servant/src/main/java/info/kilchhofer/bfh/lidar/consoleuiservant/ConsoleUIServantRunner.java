package info.kilchhofer.bfh.lidar.consoleuiservant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

public class ConsoleUIServantRunner {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleUIServantRunner.class);
    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.error("undefined hostname", ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        System.out.println("Loglevel= " + LOGGER.getLevel());
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            LOGGER.info("Per default, 'tcp://127.0.0.1:1883' is chosen. You can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883");
        }
        LOGGER.info("{} will be used as broker address.", mqttURI);

        String mqttClientName = "LidarServant@" + computerName;
        String instanceName = mqttClientName;

        ConsoleUIServant consoleUIServant = new ConsoleUIServant(mqttURI, mqttClientName, instanceName);

        System.in.read();
    }
}
