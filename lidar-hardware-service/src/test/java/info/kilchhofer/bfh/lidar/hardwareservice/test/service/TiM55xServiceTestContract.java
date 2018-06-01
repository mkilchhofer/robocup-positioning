package info.kilchhofer.bfh.lidar.hardwareservice.test.service;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;

import java.util.Map;

public class TiM55xServiceTestContract extends AyamlServiceContract {

    public TiM55xServiceTestContract(String instanceID) {
        super("Robocup", "TiM55xServiceTestContract", instanceID);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> map) {

    }
}
