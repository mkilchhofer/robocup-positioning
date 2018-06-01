package info.kilchhofer.bfh.lidar.hardwareservice.contract.event;

import ch.quantasy.mqtt.gateway.client.message.annotations.Range;

public class Measurement {
    @Range(from = -45, to = 225)
    public int angle;

    @Range(from = 0, to = 10000)
    public int distance;

    @Range(from = 0, to = 10000)
    public int rssi;

    public Measurement(int angle, int distance, int rssi){
        this.angle = angle;
        this.distance = distance;
        this.rssi = rssi;
    }

    public Measurement(){
    }
}
