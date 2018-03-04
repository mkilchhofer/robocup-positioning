package service.sensor;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;

public class LidarIntent extends AnIntent {
    public LidarMode mode;

    public LidarIntent(LidarMode mode){
        this.mode = mode;

    }
    public LidarIntent(){

    }
}
