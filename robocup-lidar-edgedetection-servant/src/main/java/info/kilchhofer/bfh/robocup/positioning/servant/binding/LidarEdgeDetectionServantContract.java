package info.kilchhofer.bfh.robocup.positioning.servant.binding;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;

import java.util.Map;

import static info.kilchhofer.bfh.robocup.common.Constants.ROBOCUP_ROOT_CONTEXT;

public class LidarEdgeDetectionServantContract extends AyamlServiceContract {

    public LidarEdgeDetectionServantContract(String instanceID) {
        super(ROBOCUP_ROOT_CONTEXT, "Lidar-EdgeDetection-Servant", instanceID);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> map) {
        // none
    }
}
