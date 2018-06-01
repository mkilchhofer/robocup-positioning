package info.kilchhofer.bfh.lidar.hardwareservice.contract.intent;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;

public class LidarIntent extends AnIntent {
    public LidarCommand command;

    public LidarIntent(LidarCommand command){
        this.command = command;

    }
    public LidarIntent(){

    }
}
