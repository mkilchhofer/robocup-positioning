package service.sensor;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;

import java.util.Map;

public class LidarServiceContract extends AyamlServiceContract {

    public final String MODE;

    public final String STATUS_MODE;
    public final String EVENT_MODE;


    public LidarServiceContract(String instanceID) {
        super("Robocup", "Lidar", instanceID);

        MODE = "mode";

        STATUS_MODE = STATUS + "/" + MODE;
        EVENT_MODE = EVENT + "/" + MODE;
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        //messageTopicMap.put(EVENT_PLAY, PlayEvent.class);
        messageTopicMap.put(STATUS_MODE, LidarStatus.class);
        messageTopicMap.put(INTENT, LidarIntent.class);
    }
}
