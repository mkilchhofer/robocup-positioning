package info.kilchhofer.bfh.lidar.hardwareservice;

public class Measurement {
    public int id, distance, rssi;

    public Measurement(int id, int distance, int rssi){
        this.id = id;
        this.distance = distance;
        this.rssi = rssi;
    }

    public Measurement(){
    }
}
