package info.kilchhofer.bfh.robocup.lidar.service.binding;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;
import ch.quantasy.mqtt.gateway.client.message.annotations.ArraySize;

import java.util.ArrayList;

public class LidarMeasurementEvent extends AnEvent{
    @ArraySize(min = 1, max = 270)
    private ArrayList<Measurement> measurements;

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public LidarMeasurementEvent(ArrayList<Measurement> measurements) {
        this.measurements = measurements;
    }

    /**
     * needed for deserialization
      */
    private LidarMeasurementEvent() {
    }
}
