package info.kilchhofer.bfh.lidar.consoleuiservant.contract;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;

import java.util.Map;

public class ConsoleUIServantContract extends AyamlServiceContract {

    public ConsoleUIServantContract(String instanceID) {
        super("Robocup", "LidarConsoleUIServant", instanceID);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> map) {

    }
}
