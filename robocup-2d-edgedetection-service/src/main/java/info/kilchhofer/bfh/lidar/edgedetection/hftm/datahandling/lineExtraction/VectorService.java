package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction;

/**
 *
 * @author sdb
 */
public class VectorService
{
    public static double calculateDistancePointToStraight(Straight straight, Vector positionVectorPoint)
    {
        // Berechnen Lotpunkt (lambda)
        int x  = (straight.getDirectionVector().getX() * positionVectorPoint.getX()
                + straight.getDirectionVector().getY() * positionVectorPoint.getY()
                - straight.getDirectionVector().getX() * straight.getSupportVector().getX()
                - straight.getDirectionVector().getY() * straight.getSupportVector().getY());
        
        int y = (straight.getDirectionVector().getX() * straight.getDirectionVector().getX() + // x^2
                straight.getDirectionVector().getY() * straight.getDirectionVector().getY() // y^2
                );
        
        float lambda
                = (float)(straight.getDirectionVector().getX() * positionVectorPoint.getX()
                + straight.getDirectionVector().getY() * positionVectorPoint.getY()
                - straight.getDirectionVector().getX() * straight.getSupportVector().getX()
                - straight.getDirectionVector().getY() * straight.getSupportVector().getY())
                / (float)(straight.getDirectionVector().getX() * straight.getDirectionVector().getX() + // x^2
                straight.getDirectionVector().getY() * straight.getDirectionVector().getY() // y^2
                );

        // Berechne Ortsvektor Lotpunkt
        Vector perpendicularVectorPoint = calculateLocationVector(straight, lambda);

        Vector connectionVector = calculateConnectionVector(perpendicularVectorPoint, positionVectorPoint);
        
        return calculateVectorLenght(connectionVector);
    }

    public static Vector calculateConnectionVector(Vector positionVectorA, Vector positionVectorB)
    {
        int x = positionVectorB.getX() - positionVectorA.getX();
        int y = positionVectorB.getY() - positionVectorA.getY();
        int z = positionVectorB.getZ() - positionVectorA.getZ();

        return new Vector(x, y, z);
    }

    public static Vector calculateLocationVector(Straight straight, float lambda)
    {
        int x = (int) (straight.getSupportVector().getX() + lambda * straight.getDirectionVector().getX());
        int y = (int) (straight.getSupportVector().getY() + lambda * straight.getDirectionVector().getY());
        int z = (int) (straight.getSupportVector().getZ() + lambda * straight.getDirectionVector().getZ());

        return new Vector(x, y, z);
    }

    public static double calculateVectorLenght(Vector vector)
    {
        return Math.sqrt(
                vector.getX() * vector.getX()
                + vector.getY() * vector.getY()
                + vector.getZ() * vector.getZ()
        );
    }
    
    public static void main(String[] args)
    {
        Vector s = new Vector(3, 6, 0);
        Vector g = new Vector(6, 3, 0);
        Straight straight = new Straight(s, g);
        
        Vector p = new Vector(12, 3, 0);
        Vector locationG = new Vector(9, 9, 0);
        
        System.out.println("Distance P: " + VectorService.calculateDistancePointToStraight(straight, p)); // 6.7
        System.out.println("Distance S: " + VectorService.calculateDistancePointToStraight(straight, s)); // 0
        System.out.println("Distance G: " + VectorService.calculateDistancePointToStraight(straight, locationG)); // 0
    }
}
