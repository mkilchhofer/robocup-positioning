package info.kilchhofer.bfh.robocup.positioning.servant;

import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageReceiver;
import info.kilchhofer.bfh.lidar.edgedetection.binding.EdgeDetectionIntent;
import info.kilchhofer.bfh.lidar.edgedetection.binding.EdgeDetectionServiceContract;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanReflectData;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.PolarToCartesian;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.scandata.ScanMeasData;
import info.kilchhofer.bfh.robocup.lidar.service.binding.LidarMeasurementEvent;
import info.kilchhofer.bfh.robocup.lidar.service.binding.LidarServiceContract;
import info.kilchhofer.bfh.robocup.lidar.service.binding.Measurement;
import info.kilchhofer.bfh.robocup.positioning.servant.binding.LidarEdgeDetectionServantContract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URI;
import java.util.*;

public class LidarEdgeDetectionServant {

    private static final Logger LOGGER = LogManager.getLogger(LidarEdgeDetectionServant.class);
    private LidarServiceContract lidarServiceContract;
    private Set<EdgeDetectionServiceContract> edgeDetectionServiceInstances;

    private final GatewayClient<LidarEdgeDetectionServantContract> gatewayClient;
    private MessageReceiver edgeDetectionConnectionStatusReceiver,
            measurementReceiver, lidarConnectionStatusReceiver;

    public LidarEdgeDetectionServant(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        this.gatewayClient = new GatewayClient<>(mqttURI, mqttClientName, new LidarEdgeDetectionServantContract(instanceName));
        this.gatewayClient.connect();

        this.edgeDetectionServiceInstances = new HashSet<>();

        setupEdgeDetectionMessageReceivers();
        setupLidarMessageReceiver();

        this.gatewayClient.subscribe(new EdgeDetectionServiceContract("+").STATUS_CONNECTION, this.edgeDetectionConnectionStatusReceiver);
        this.gatewayClient.subscribe(new LidarServiceContract("+").STATUS_CONNECTION, this.lidarConnectionStatusReceiver);
    }

    /**
     * Configure the MessageReceivers for handling events from ConsoleUI-Service
     */
    private void setupEdgeDetectionMessageReceivers() {
        this.edgeDetectionConnectionStatusReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                LOGGER.trace("Payload EdgeDetection ConnStatus: " + new String(payload));
                ConnectionStatus status = gatewayClient.toMessageSet(payload, ConnectionStatus.class).last();
                EdgeDetectionServiceContract edgeDetectionServiceContract = new EdgeDetectionServiceContract(topic, true);

                LOGGER.info("EdgeDetection Instance '{}' is now {}", edgeDetectionServiceContract.INSTANCE, status.value);

                if (status.value.equals("online")) {
                    edgeDetectionServiceInstances.add(edgeDetectionServiceContract);
                } else {
                    edgeDetectionServiceInstances.remove(edgeDetectionServiceContract);
                }
            }
        };
    }

    /**
     * Configure the MessageReceivers for handling events from TiM55x-Service
     */
    private void setupLidarMessageReceiver() {

        this.measurementReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                Set<LidarMeasurementEvent> lidarMeasurementEvents = gatewayClient.toMessageSet(payload, LidarMeasurementEvent.class);
                for (LidarMeasurementEvent lidarMeasurementEvent : lidarMeasurementEvents) {

                    List<CartesianPoint> cartesianPoints = new ArrayList<>();
                    for (Measurement measurement : lidarMeasurementEvent.getMeasurements()) {

                        IScanReflectData tempIScanReflectData = new ScanMeasData(measurement.distance, measurement.angle, measurement.rssi);
                        cartesianPoints.add(PolarToCartesian.calcCoords(tempIScanReflectData));
                    }
                    for (EdgeDetectionServiceContract instance : edgeDetectionServiceInstances) {
                        gatewayClient.readyToPublish(instance.INTENT, new EdgeDetectionIntent("myID", cartesianPoints, 10, 5));
                    }
                }
            }
        };

        this.lidarConnectionStatusReceiver = new MessageReceiver() {
            @Override
            public void messageReceived(String topic, byte[] payload) throws Exception {
                LOGGER.trace("Payload TiM55x ConnStatus: " + new String(payload));
                ConnectionStatus status = new TreeSet<ConnectionStatus>(gatewayClient.toMessageSet(payload, ConnectionStatus.class)).last();
                lidarServiceContract = new LidarServiceContract(topic, true);
                LOGGER.info("TiM55x Instance '{}' is now {}", lidarServiceContract.INSTANCE, status.value);

                if (status.value.equals("online")) {
                    gatewayClient.subscribe(lidarServiceContract.EVENT_MEASUREMENT, measurementReceiver);
                } else {
                    gatewayClient.unsubscribe(lidarServiceContract.EVENT_MEASUREMENT);
                }
            }
        };
    }
}
