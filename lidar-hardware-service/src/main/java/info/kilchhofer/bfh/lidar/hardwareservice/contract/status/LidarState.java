package info.kilchhofer.bfh.lidar.hardwareservice.contract.status;

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
