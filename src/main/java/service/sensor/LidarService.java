package service.sensor;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import laser.scanner.*;
import laser.scanner.tim55x.TiM55x;
import laser.scanner.tim55x.com.ComNotRunningException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LidarService implements IScanListener, IScanOperator {
    private IScan scanner;
    private final GatewayClient<LidarServiceContract> gatewayClient;

    public LidarService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException, IOException {
        this.gatewayClient = new GatewayClient<LidarServiceContract>(mqttURI, mqttClientName, new LidarServiceContract(instanceName));
        this.scanner = new TiM55x( this, (IScanOperator) this, "192.168.137.2", 2112);
        this.gatewayClient.connect();
        this.gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (topic, payload) -> {
            Set<LidarIntent> intents = gatewayClient.toMessageSet(payload, LidarIntent.class);
            intents.stream().filter((intent) -> (intent.mode == LidarMode.singleMeas)).map((_item) -> {
                try {
                    this.scanner.runSingleMeas();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ComNotRunningException e) {
                    e.printStackTrace();
                } catch (LaserScanStateException e) {
                    e.printStackTrace();
                }
                return _item;
            }).forEachOrdered((_item) -> {

            });
        });
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_MODE, new LidarStatus(LidarMode.singleMeas));

    }

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(LidarService.class.getName()).log(Level.SEVERE, null, ex);
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

        LidarService lidarService = new LidarService(mqttURI, "Lidar" + computerName, computerName);

        System.in.read();
    }

    @Override
    public void newMeasData(IScanMeasData iScanMeasData) {
        System.out.println(Arrays.toString(iScanMeasData.getAllDistanceValues()));

    }

    @Override
    public void newStateActice(State state) {
        System.out.println("New laser state active: " + state);
    }

    @Override
    public void errorOccured() {
        System.out.println("Laser ERROR");
    }
}
