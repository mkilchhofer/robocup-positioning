package info.kilchhofer.bfh.lidar.edgedetection.contract;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import info.kilchhofer.bfh.lidar.edgedetection.contract.intent.EdgeDetectionIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static info.kilchhofer.bfh.lidar.Constants.LIDAR_ROOT_CONTEXT;

public class EdgeDetectionServiceContract extends AyamlServiceContract {
    private static final Logger LOGGER = LogManager.getLogger(EdgeDetectionServiceContract.class);

    public EdgeDetectionServiceContract(String instanceID) {
        super(LIDAR_ROOT_CONTEXT, "EdgeDetection", instanceID);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, EdgeDetectionIntent.class);
    }
}
