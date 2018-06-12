
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction;

import ch.quantasy.mqtt.gateway.client.message.annotations.AValidator;
import ch.quantasy.mqtt.gateway.client.message.annotations.NonNull;
import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;

import java.util.List;

/**
 *
 * @author sdb
 */
public class ExtractedLine extends AValidator
{
    @NonNull
    private List<CartesianPoint> relatedPoints;
    
    public ExtractedLine(List<CartesianPoint> relatedPoints)
    {
        this.relatedPoints = relatedPoints;
    }

    // needed for deserialization
    public ExtractedLine() {
    }
    
    public CartesianPoint getStartPoint()
    {
        return relatedPoints.get(0);
    }
    
    public CartesianPoint getEndPoint()
    {
        return relatedPoints.get(relatedPoints.size() - 1);
    }
    
    public List<CartesianPoint> getRelatedPoints()
    {
        return relatedPoints;
    }
}
