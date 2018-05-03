package info.kilchhofer.bfh.lidar.hardwareservice.contract.event;

public class Measurement {
    public int id, angle, distance, rssi;

    public Measurement(int id, int angle, int distance, int rssi){
        this.id = id;
        this.angle = angle;
        this.distance = distance;
        this.rssi = rssi;
    }

    public Measurement(){
    }
}
