package info.kilchhofer.bfh.robocup.lidar.service.binding;

import ch.quantasy.mqtt.gateway.client.message.AStatus;
import laser.scanner.IScanOperator;

public class LidarState extends AStatus{
    public IScanOperator.State state;

    public LidarState(IScanOperator.State state) {
        this.state = state;
    }

    public LidarState() {
    }
}
