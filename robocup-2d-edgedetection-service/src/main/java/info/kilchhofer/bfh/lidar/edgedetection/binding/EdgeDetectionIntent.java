package info.kilchhofer.bfh.lidar.edgedetection.binding;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;
import ch.quantasy.mqtt.gateway.client.message.annotations.NonNull;
import ch.quantasy.mqtt.gateway.client.message.annotations.Range;
import ch.quantasy.mqtt.gateway.client.message.annotations.StringForm;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;

import java.util.List;

public class EdgeDetectionIntent extends AnIntent {
    @NonNull
    @StringForm
    public String id;
    @NonNull
    public List<CartesianPoint> positions;
    @NonNull
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int toleranceMax;
    @NonNull
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int minimalRelatedPoints;

    public EdgeDetectionIntent(String id, List<CartesianPoint> positions, int toleranceMax, int minimalRelatedPoints) {
        this.id = id;
        this.positions = positions;
        this.toleranceMax = toleranceMax;
        this.minimalRelatedPoints = minimalRelatedPoints;
    }

    public EdgeDetectionIntent() {

    }
}
