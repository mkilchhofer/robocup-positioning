
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.filters;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanReflectData;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sdb
 */
public class ThresholdValueFilter implements IScanReflectionsFilter
{
    
    private int thresholdValue;
    
    private List<IScanReflectData> previousData;
    
    public ThresholdValueFilter(int thresholdValue)
    {
        this.thresholdValue = thresholdValue;
    }

    @Override
    public List<IScanReflectData> filter(List<IScanReflectData> data) throws Exception
    {
        // check: first call?
        if(this.previousData == null)
        {
            this.previousData = data;
            return this.previousData;
        }
        
        // check: same count of distance datas?
        if(this.previousData.size() != data.size())
        {
            throw new Exception("Not the same count of distance datas");
        }
        
        List<IScanReflectData> filteredDatas = new ArrayList<>();
        
        for (int cntFor = 0; cntFor < data.size(); cntFor++)
        {
            // check: same angle?
            if(data.get(cntFor).getAngle() != this.previousData.get(cntFor).getAngle())
            {
                throw new Exception("Not the same angle sequence");
            }
            
            int difference = Math.abs(data.get(cntFor).getDistance() - previousData.get(cntFor).getDistance());
            
            if(difference > thresholdValue)
            {
                filteredDatas.add(data.get(cntFor));
            }
            else
            {
                filteredDatas.add(previousData.get(cntFor));
            }
        }
        this.previousData = filteredDatas;
        return this.previousData;
    }
    
    // getter & setter
    public int getThresholdValue()
    {
        return thresholdValue;
    }
}
