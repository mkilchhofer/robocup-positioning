
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling;

import java.util.List;

/**
 *
 * @author Simon Bühlmann
 */
public interface IScanData
{
    public IScannerData getScannerData();
    public List<IScanReflectData> getScanMeasurementData();
}
