
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.scandata;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanReflectData;


/**
 *
 * @author Simon Bühlmann
 */
public class ScanMeasData implements IScanReflectData
{
    private final int distance;
    private final int angle;
    private final int rssi;
    
    public ScanMeasData(int distance, int angle, int rssi)
    {
        this.distance = distance;
        this.angle = angle;
        this.rssi = rssi;
    }
    
    // getter
    @Override
    public int getDistance()
    {
        return distance;
    }

    @Override
    public int getAngle()
    {
        return angle;
    }

    @Override
    public int getRSSIValue()
    {
        return rssi;
    }
}
