
package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord;

import ch.quantasy.mqtt.gateway.client.message.annotations.AValidator;
import ch.quantasy.mqtt.gateway.client.message.annotations.Range;
import java.awt.Point;

/**
 *
 * @author Simon Bühlmann
 */
// refactored from CoordScanData -> CartesianPoint
public class CartesianPoint extends AValidator
{
    @Range(from = Integer.MIN_VALUE,to=Integer.MAX_VALUE)
    private int x;
    @Range(from = Integer.MIN_VALUE,to=Integer.MAX_VALUE)
    private int y;
    
    public CartesianPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    // needed for deserialization
    public CartesianPoint()
    {
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CartesianPoint other = (CartesianPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
    
    
}
