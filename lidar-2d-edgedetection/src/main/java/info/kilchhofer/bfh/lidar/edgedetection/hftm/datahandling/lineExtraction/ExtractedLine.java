
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;

import java.util.List;

/**
 *
 * @author sdb
 */
public class ExtractedLine
{
    private List<CartesianPoint> relatedReflections;
    
    public ExtractedLine(List<CartesianPoint> relatedReflections)
    {
        this.relatedReflections = relatedReflections;
    }
    
    public CartesianPoint getStartReflection()
    {
        return relatedReflections.get(0);
    }
    
    public CartesianPoint getEndReflection()
    {
        return relatedReflections.get(relatedReflections.size() - 1);
    }
    
    public List<CartesianPoint> getAllRelatedReflections()
    {
        return relatedReflections;
    }
}
