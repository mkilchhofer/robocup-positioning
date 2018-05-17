package info.kilchhofer.bfh.lidar.edgedetection.contract.intent;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;
import info.kilchhofer.bfh.lidar.edgedetection.contract.CartesianPosition;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;

import java.util.List;

public class EdgeDetectionIntent extends AnIntent {
    public List<CartesianPoint> positions;
    public int toleranceMax;

    public EdgeDetectionIntent(List<CartesianPoint> positions, int toleranceMax){
        this.positions = positions;
        this.toleranceMax = toleranceMax;
    }

    public EdgeDetectionIntent(){

    }
}
