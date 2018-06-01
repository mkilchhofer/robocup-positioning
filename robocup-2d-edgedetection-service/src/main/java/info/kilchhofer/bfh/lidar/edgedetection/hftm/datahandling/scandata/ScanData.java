
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.scandata;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanData;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanReflectData;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScannerData;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon Bühlmann
 */
public class ScanData implements IScanData
{
    private List<IScanReflectData> scanMeasurementDatas;
    private IScannerData scannerData;
    
    public ScanData(IScannerData scannerData)
    {
        this.scannerData = scannerData;
    }
    
    @Override
    public IScannerData getScannerData()
    {
        return scannerData;
    }

    @Override
    public List<IScanReflectData> getScanMeasurementData()
    {
        if(scanMeasurementDatas == null)
        {
            scanMeasurementDatas = new ArrayList<>();
        }
        
        return scanMeasurementDatas;
    }
    
}
