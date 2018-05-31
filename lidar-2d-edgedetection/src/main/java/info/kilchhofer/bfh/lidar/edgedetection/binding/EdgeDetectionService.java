package info.kilchhofer.bfh.lidar.edgedetection.binding;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import info.kilchhofer.bfh.lidar.edgedetection.binding.EdgeDetectionServiceContract;
import info.kilchhofer.bfh.lidar.edgedetection.binding.EdgeDetectionIntent;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction.ExtractedLine;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction.LineExtracter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URI;
import java.util.List;

public class EdgeDetectionService {
    private final GatewayClient<EdgeDetectionServiceContract> gatewayClient;
    private static final Logger LOGGER = LogManager.getLogger(EdgeDetectionService.class);

    public EdgeDetectionService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        this.gatewayClient = new GatewayClient<EdgeDetectionServiceContract>(mqttURI, mqttClientName, new EdgeDetectionServiceContract(instanceName));

        this.gatewayClient.connect();
        this.gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (topic, payload) -> {

            for(EdgeDetectionIntent intent : gatewayClient.toMessageSet(payload, EdgeDetectionIntent.class)){
                LineExtracter lineExtracter = new LineExtracter(intent.toleranceMax);

                List<ExtractedLine> lines = lineExtracter.extractLines(intent.positions);
                this.gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_EDGE_DETECTED+"/"+intent.id, new EdgeDetectionEvent(intent.id,lines));
                LOGGER.info("# of extracted: {}", lines.size());
            }
        });
    }
}
