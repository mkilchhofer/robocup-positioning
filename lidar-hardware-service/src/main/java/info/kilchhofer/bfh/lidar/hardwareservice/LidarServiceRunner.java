package info.kilchhofer.bfh.lidar.hardwareservice;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class LidarServiceRunner {
    private static final Logger LOGGER = LogManager.getLogger(LidarServiceRunner.class);
    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.error((String)null, ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        System.out.println("Loglevel= " + LOGGER.getLevel());
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
            LOGGER.info("Per default, 'tcp://127.0.0.1:1883' is chosen. You can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883");
        }

        ArrayList<String> sensorArgsList = new ArrayList<>();
        if (sensorArgs != null) {
            sensorArgsList.addAll(Arrays.asList(sensorArgs));
        } else {
            sensorArgsList.add("192.168.91.2");
            LOGGER.info("Per default, we expect our lidar sensor on IP '192.168.91.2.");
        }

        LOGGER.info("Configured {} lidar sensors: {}", sensorArgsList.size(), sensorArgsList);
        LOGGER.info("{} will be used as broker address.", mqttURI);

        int lidarPort = 2112;

        for (String lidarIp : sensorArgsList) {
            String mqttClientName = lidarIp + "@" + computerName;
            String instanceName = mqttClientName;
            try {
                LidarService lidarService = new LidarService(mqttURI, mqttClientName, instanceName, lidarIp, lidarPort);

            } catch (Exception ex){
                LOGGER.error("{}: Connection error", mqttClientName, ex);
            }
        }
        System.in.read();
    }

}
