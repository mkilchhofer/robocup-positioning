package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.scandata;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScannerData;

/**
 *
 * @author Simon Bühlmann
 */
public class ScannerData implements IScannerData
{
    private int firmwareVersion;
    public int deviceNr;
    public long sickSerialNr;
    public int telegrammCounter;
    public long devicePowerONDuration;
    public long deviceTransmissionDuration;
    public long scanFrequency;

    public ScannerData(
            int firmwareVersion,
            int deviceNr,
            long sickSerialNr,
            int telegrammCounter,
            long devicePowerONDuration,
            long deviceTransmissionDuration,
            long scanFrequency
    )
    {
        this.firmwareVersion = firmwareVersion;
        this.deviceNr = deviceNr;
        this.sickSerialNr = sickSerialNr;
        this.telegrammCounter = telegrammCounter;
        this.devicePowerONDuration = devicePowerONDuration;
        this.deviceTransmissionDuration = deviceTransmissionDuration;
        this.scanFrequency = scanFrequency;
    }

    @Override
    public int getFirmwareVersion()
    {
        return this.firmwareVersion;
    }

    @Override
    public int getDeviceNr()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getSickSerialNr()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTelegrammCounter()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getDevicePowerONDuration()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getDeviceTransmissionDuration()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getScanFrequency()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
