package info.kilchhofer.bfh.lidar.consoleuiservice.contract;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.event.ConsoleKeyPressEvent;
import info.kilchhofer.bfh.lidar.consoleuiservice.contract.intent.ConsoleIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ConsoleUIServiceContract extends AyamlServiceContract {

    private static final Logger LOGGER = LogManager.getLogger(ConsoleUIServiceContract.class);
    public final String KEYPRESS;

    public final String EVENT_KEYPRESS;

    public ConsoleUIServiceContract(String instanceID) {
        super("Robocup", "LidarConsoleUI", instanceID);


        KEYPRESS = "keypress";

        EVENT_KEYPRESS = EVENT + "/" + KEYPRESS;
    }

    public ConsoleUIServiceContract(String topic, boolean isTopic){
        this(topic.split("/")[3]);
        LOGGER.trace("Got topic '{}'. Extracted instance '{}'.", topic, topic.split("/")[3]);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, ConsoleIntent.class);
        messageTopicMap.put(EVENT_KEYPRESS, ConsoleKeyPressEvent.class);
    }
}
