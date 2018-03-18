package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;

import java.util.Map;

public class LidarServiceContract extends AyamlServiceContract {

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

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, LidarIntent.class);
        messageTopicMap.put(EVENT_MEASUREMENT, LidarMeasurementEvent.class);
        messageTopicMap.put(STATUS_STATE, LidarState.class);
    }
}
