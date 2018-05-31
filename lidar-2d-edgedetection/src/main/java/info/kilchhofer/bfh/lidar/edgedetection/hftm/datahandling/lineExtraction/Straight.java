
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction;

/**
 *
 * @author sdb
 */
public class Straight
{
    private Vector supportVector;
    private Vector directionVector;
    
    public Straight(Vector supportVector, Vector directionVector)
    {
        this.supportVector = supportVector;
        this.directionVector = directionVector;
    }
    
    public Straight()
    {
        
    }
    
    // getter and setter
    public Vector getSupportVector()
    {
        return supportVector;
    }

    public void setSupportVector(Vector supportVector)
    {
        this.supportVector = supportVector;
    }

    public Vector getDirectionVector()
    {
        return directionVector;
    }

    public void setDirectionVector(Vector directionVector)
    {
        this.directionVector = directionVector;
    }
    
}
