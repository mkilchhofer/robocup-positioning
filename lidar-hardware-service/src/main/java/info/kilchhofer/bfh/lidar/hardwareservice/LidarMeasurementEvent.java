package info.kilchhofer.bfh.lidar.hardwareservice;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;

import java.util.ArrayList;

public class LidarMeasurementEvent extends AnEvent{

    ArrayList<Integer> distanceValues;
    ArrayList<Integer> rssiValues;

    public LidarMeasurementEvent(ArrayList<Integer> distanceValues, ArrayList<Integer> rssiValues) {
        this.distanceValues = distanceValues;
        this.rssiValues = rssiValues;
    }

    private LidarMeasurementEvent() {
    }

}
