package service.sensor;

import ch.quantasy.mqtt.gateway.client.message.AStatus;

public class LidarStatus extends AStatus {
    public LidarMode mode;

    public LidarStatus(LidarMode mode) {
        this.mode = mode;
    }

    public LidarStatus() {
    }
}
