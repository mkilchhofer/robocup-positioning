package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class LidarMeasurementEvent extends AnEvent{
    private static final Logger LOGGER = LogManager.getLogger(LidarMeasurementEvent.class);

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    private ArrayList<Measurement> measurements;

    public LidarMeasurementEvent(int[] distanceValues, int[] rssiValues) {
        if(distanceValues.length != rssiValues.length){
            LOGGER.error("Measurement incorrect");
            return;
        }

        this.measurements = new ArrayList<>();
        for(int i=0; i < distanceValues.length; i++){
            this.measurements.add(new Measurement(i, distanceValues[i], rssiValues[i]));
        }
    }

    // needed for deserialization
    private LidarMeasurementEvent() {
    }

}
