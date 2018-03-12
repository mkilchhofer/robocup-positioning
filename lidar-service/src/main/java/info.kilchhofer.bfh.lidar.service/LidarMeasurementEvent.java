package info.kilchhofer.bfh.lidar.service;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;

import java.util.ArrayList;

public class LidarMeasurementEvent extends AnEvent{

    ArrayList<Integer> distanceValues;

    public LidarMeasurementEvent(ArrayList<Integer> distanceValues) {
        this.distanceValues = distanceValues;
    }

    private LidarMeasurementEvent() {
    }

}
