package info.kilchhofer.bfh.lidar.edgedetection.contract.event;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;

import java.util.List;

public class EdgeDetectionEvent extends AnEvent {

    public List<CartesianPoint> positions;
    public int toleranceMax;

    public EdgeDetectionEvent(List<CartesianPoint> positions, int toleranceMax) {
        this.positions = positions;
        this.toleranceMax = toleranceMax;
    }

    public EdgeDetectionEvent() {

    }

}
