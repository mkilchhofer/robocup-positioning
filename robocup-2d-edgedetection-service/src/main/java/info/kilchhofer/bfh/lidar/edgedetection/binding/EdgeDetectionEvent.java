package info.kilchhofer.bfh.lidar.edgedetection.binding;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;
import ch.quantasy.mqtt.gateway.client.message.annotations.NonNull;
import ch.quantasy.mqtt.gateway.client.message.annotations.StringForm;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction.ExtractedLine;
import java.util.ArrayList;

import java.util.List;

public class EdgeDetectionEvent extends AnEvent {
    @NonNull
    @StringForm
    public String id;
    @NonNull
    public List<ExtractedLine> lines;
    

    public EdgeDetectionEvent(String id, List<ExtractedLine> positions) {
        this.id=id;
        this.lines = new ArrayList<>(lines);
    }

    public EdgeDetectionEvent() {

    }

}
