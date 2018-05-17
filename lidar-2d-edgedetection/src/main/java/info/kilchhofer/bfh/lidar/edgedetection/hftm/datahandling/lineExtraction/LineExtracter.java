package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.lineExtraction;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord.CartesianPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sdb
 */
public class LineExtracter
{
    private int toleranceMax;
    
    public LineExtracter(int toleranceMax)
    {
        this.toleranceMax = toleranceMax;
    }
    
    public synchronized List<ExtractedLine> extractLines(List<CartesianPoint> scandatas)
    {
        List<ExtractedLine> tempResults = new ArrayList<>();

        // Douglas Peucker Algorithmus
        // finde Punkt mit grösstem Abstand
        double distanceMax = 0;
        int indexOfDistanceMax = 0;
        //System.out.println("Extracting Lines");

        Straight straight = new Straight(
                new Vector(
                        scandatas.get(0).getPoint().x,
                        scandatas.get(0).getPoint().y,
                        0),
                VectorService.calculateConnectionVector(
                        new Vector(
                                scandatas.get(0).getPoint().x,
                                scandatas.get(0).getPoint().y,
                                0),
                        new Vector(
                                scandatas.get(scandatas.size() - 1).getPoint().x,
                                scandatas.get(scandatas.size() - 1).getPoint().y,
                                0)
                ));

        //System.out.println("1");
        for (int cnt = 1; cnt < scandatas.size(); cnt++)
        {
            double dCurrent = VectorService.calculateDistancePointToStraight(
                    straight,
                    new Vector(
                            scandatas.get(cnt).getPoint().x,
                            scandatas.get(cnt).getPoint().y,
                            0));

            if (dCurrent > distanceMax)
            {
                distanceMax = dCurrent;
                indexOfDistanceMax = cnt;
            }
        }

        //System.out.println("2");
        // max Distanz mit max Toleranz vergleichen
        if (distanceMax > toleranceMax)
        {
            // Toleranz überschritten. Scandatas splitten und per Rekursion erneut probieren.
            tempResults.addAll(extractLines(
                            splittScandataArray(
                                    0, // start
                                    indexOfDistanceMax,
                                    scandatas))
            );

            tempResults.addAll(extractLines(
                            splittScandataArray(
                                    indexOfDistanceMax,
                                    scandatas.size() - 1, // end
                                    scandatas))
            );
        }
        else
        {
            //System.out.println("3");
            /*System.out.println("Straight found! Support vector"
                    + " X: " + straight.getSupportVector().getX() 
                    + " Y: " + straight.getSupportVector().getY() 
                    + " Z: " + straight.getSupportVector().getZ() 
                    + " Direction vector"
                    + " X: " + straight.getDirectionVector().getX()
                    + " Y: " + straight.getDirectionVector().getY()
                    + " Z: " + straight.getDirectionVector().getZ());*/
            tempResults.add(new ExtractedLine(scandatas));
        }

        //System.out.println("Lines extracted!");
        return tempResults;
    }

    private List<CartesianPoint> splittScandataArray(int startIndex, int lastIndex, List<CartesianPoint> datas)
    {

        List<CartesianPoint> temp = new ArrayList<>(lastIndex - startIndex + 1);
        for (int cnt = startIndex; cnt <= lastIndex; cnt++)
        {
            temp.add(datas.get(cnt));
        }

        return temp;
    }
    
    // getter & setter
    public int getToleranceMax()
    {
        return toleranceMax;
    }

    public synchronized void setToleranceMax(int toleranceMax)
    {
        this.toleranceMax = toleranceMax;
    }

    public static void main(String[] args) throws IOException {
        List<CartesianPoint> datas = new ArrayList<>();
        datas.add(new CartesianPoint(-200, 200, 0));
        datas.add(new CartesianPoint(-200, 600, 0));
        datas.add(new CartesianPoint(400, 1200, 0));
        datas.add(new CartesianPoint(400, 800, 0));
        datas.add(new CartesianPoint(400, 200, 0));
        
        List<ExtractedLine> lines = new LineExtracter(10).extractLines(datas);
        System.out.println(lines.size());
    }
}
