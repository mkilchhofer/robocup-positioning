
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.filters;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanReflectData;

import java.util.List;

/**
 *
 * @author sdb
 */
public interface IScanReflectionsFilter
{
    public  List<IScanReflectData> filter(List<IScanReflectData> data) throws Exception;
}
