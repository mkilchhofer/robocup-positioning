package info.kilchhofer.bfh.lidar.service;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;

public class LidarIntent extends AnIntent {
    public LidarCommand command;

    public LidarIntent(LidarCommand command){
        this.command = command;

    }
    public LidarIntent(){

    }
}
