package info.kilchhofer.bfh.lidar.edgedetection.binding;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import info.kilchhofer.bfh.lidar.edgedetection.binding.EdgeDetectionIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static info.kilchhofer.bfh.lidar.Constants.LIDAR_ROOT_CONTEXT;
import info.kilchhofer.bfh.lidar.edgedetection.binding.EdgeDetectionEvent;

public class EdgeDetectionServiceContract extends AyamlServiceContract {
    private static final Logger LOGGER = LogManager.getLogger(EdgeDetectionServiceContract.class);
    public final String EVENT_EDGE_DETECTED;

    public EdgeDetectionServiceContract(String instanceID) {
        super(LIDAR_ROOT_CONTEXT, "EdgeDetection", instanceID);
        EVENT_EDGE_DETECTED=EVENT+"/"+"detected";
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, EdgeDetectionIntent.class);
        messageTopicMap.put(EVENT_EDGE_DETECTED+"/<id>", EdgeDetectionEvent.class);
    }
}
