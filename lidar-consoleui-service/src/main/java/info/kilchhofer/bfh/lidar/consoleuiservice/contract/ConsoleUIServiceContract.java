package info.kilchhofer.bfh.lidar.consoleuiservice.contract;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import info.kilchhofer.bfh.lidar.consoleuiservice.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.intent.ConsoleIntent;

import java.util.Map;

public class ConsoleUIServiceContract extends AyamlServiceContract {

    public final String KEYPRESS;

    public final String EVENT_KEYPRESS;

    public ConsoleUIServiceContract(String instanceID) {
        super("Robocup", "LidarConsoleUI", instanceID);


        KEYPRESS = "keypress";

        EVENT_KEYPRESS = EVENT + "/" + KEYPRESS;
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, ConsoleIntent.class);
        messageTopicMap.put(EVENT_KEYPRESS, ConsoleKeyPressEvent.class);
    }
}
