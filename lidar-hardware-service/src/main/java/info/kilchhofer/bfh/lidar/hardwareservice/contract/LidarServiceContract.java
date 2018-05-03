package info.kilchhofer.bfh.lidar.hardwareservice.contract;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import info.kilchhofer.bfh.lidar.hardwareservice.contract.intent.LidarIntent;
import info.kilchhofer.bfh.lidar.hardwareservice.contract.event.LidarMeasurementEvent;
import info.kilchhofer.bfh.lidar.hardwareservice.contract.status.LidarState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LidarServiceContract extends AyamlServiceContract {

    private static final Logger LOGGER = LogManager.getLogger(LidarServiceContract.class);
    public final String STATE;
    public final String MEASUREMENT;

    public final String STATUS_STATE;
    public final String EVENT_MEASUREMENT;


    public LidarServiceContract(String instanceID) {
        super("Robocup", "Lidar", instanceID);

        STATE = "state";
        MEASUREMENT = "measurement";

        STATUS_STATE = STATUS + "/" + STATE;
        EVENT_MEASUREMENT = EVENT + "/" + MEASUREMENT;
    }

    public LidarServiceContract(String topic, boolean isTopic){
        this(topic.split("/")[3]);
        LOGGER.trace("Got topic '{}'. Extracted instance '{}'.", topic, topic.split("/")[3]);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, LidarIntent.class);
        messageTopicMap.put(EVENT_MEASUREMENT, LidarMeasurementEvent.class);
        messageTopicMap.put(STATUS_STATE, LidarState.class);
    }
}
