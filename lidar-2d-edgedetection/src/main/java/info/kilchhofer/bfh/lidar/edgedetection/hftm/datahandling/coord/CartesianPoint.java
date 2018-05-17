
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord;

import java.awt.*;

/**
 *
 * @author Simon Bühlmann
 */
// refactored from CoordScanData -> CartesianPoint
public class CartesianPoint
{
    private int x, y, reflection;
    
    public CartesianPoint(int x, int y, int reflection)
    {
        this.x = x;
        this.y = y;
        this.reflection = reflection;
    }

    // needed for deserialization
    public CartesianPoint()
    {
    }
    
    // getter
    public Point getPoint()
    {
        return new Point(this.x, this.y);
    }

    public int getReflection()
    {
        return reflection;
    }
}
