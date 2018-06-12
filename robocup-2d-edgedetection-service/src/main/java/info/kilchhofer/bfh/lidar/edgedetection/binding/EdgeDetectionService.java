package info.kilchhofer.bfh.lidar.edgedetection.binding;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.filters.MinNrReflectFilter;
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
                LOGGER.info("id: {} ; tolerance: {} ; minimalRelatedPoints: {} ; number of positions: {} " , intent.id, intent.toleranceMax, intent.minimalRelatedPoints, intent.positions.size());
                LineExtracter lineExtracter = new LineExtracter(intent.toleranceMax);

                List<ExtractedLine> lines = lineExtracter.extractLines(intent.positions);

                MinNrReflectFilter minNrReflectFilter = new MinNrReflectFilter(intent.minimalRelatedPoints);
                // filtert extrahierte Linien auf Grund der Anzahl der Reflektionen, welche die Linie definieren
                List<ExtractedLine> filteredLines = minNrReflectFilter.filter(lines);

                LOGGER.info("# of extracted unfiltered: {}", lines.size());
                LOGGER.info("# of extracted filtered:   {}", filteredLines.size());

                this.gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_EDGE_DETECTED+"/"+intent.id, new EdgeDetectionEvent(intent.id, filteredLines));
            }
        });
    }
}
