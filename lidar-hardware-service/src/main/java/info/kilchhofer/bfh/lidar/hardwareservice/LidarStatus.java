package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.message.AStatus;

public class LidarStatus extends AStatus {
    public LidarCommand mode;

    public LidarStatus(LidarCommand mode) {
        this.mode = mode;
    }

    public LidarStatus() {
    }
}
