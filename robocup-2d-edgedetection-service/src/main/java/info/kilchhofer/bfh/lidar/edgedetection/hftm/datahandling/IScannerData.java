
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling;

/**
 *
 * @author Simon Bühlmann
 */
public interface IScannerData
{
    public int getFirmwareVersion();
    public int getDeviceNr();
    public long getSickSerialNr();
    public int getTelegrammCounter();
    public long getDevicePowerONDuration();
    public long getDeviceTransmissionDuration();
    public long getScanFrequency();
}
