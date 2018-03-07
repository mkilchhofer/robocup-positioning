package service.sensor;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;

import java.util.Map;

public class LidarServiceContract extends AyamlServiceContract {

    public final String MODE;
    public final String STATE;
    public final String MEASUREMENT;

    public final String STATUS_MODE;
    public final String STATUS_STATE;
    public final String EVENT_MODE;
    public final String EVENT_MEASUREMENT;


    public LidarServiceContract(String instanceID) {
        super("Robocup", "Lidar", instanceID);

        MODE = "mode";
        STATE = "state";

        MEASUREMENT = "measurement";

        STATUS_MODE = STATUS + "/" + MODE;
        STATUS_STATE = STATUS + "/" + STATE;
        EVENT_MODE = EVENT + "/" + MODE;
        EVENT_MEASUREMENT = EVENT + "/" + MEASUREMENT;
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        //messageTopicMap.put(EVENT_PLAY, PlayEvent.class);
        messageTopicMap.put(STATUS_MODE, LidarStatus.class);
        messageTopicMap.put(INTENT, LidarIntent.class);
    }
}
