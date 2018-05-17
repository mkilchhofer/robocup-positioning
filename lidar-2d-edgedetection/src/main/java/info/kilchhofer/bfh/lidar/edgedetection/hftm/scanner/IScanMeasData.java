
package info.kilchhofer.bfh.lidar.edgedetection.hftm.scanner;

/**
 *
 * @author Simon Bühlmann
 */
public interface IScanMeasData
{
    public int getFirmwareVersion();
    public int getDeviceNr();
    public long getSickSerialNr();
    public int getTelegrammCounter();
    public long getDevicePowerONDuration();
    public long getDeviceTransmissionDuration();
    public long getScanFrequency();
    public float getScalingFactorDistanceDatas();
    public long getScanStartAngle();
    public int getScanAngleIncrement();
    public int getNrDistanceDatas();
    
    // distance
    public int getDistanceValue(int index);
    
    public int[] getDistanceValues(int startIndex, int endIndex);
    
    public int[] getAllDistanceValues();
    
    // rssi
    public int getNrRSSIValues();
    
    public int getRSSIValue(int index);
    
    public int[] getRSSIValues(int startIndex, int endIndex);
    
    public int[] getAllRSSIValues();
}
