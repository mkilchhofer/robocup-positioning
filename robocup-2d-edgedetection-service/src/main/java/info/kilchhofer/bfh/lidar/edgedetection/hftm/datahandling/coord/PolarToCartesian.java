package info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.coord;

import info.kilchhofer.bfh.lidar.edgedetection.hftm.datahandling.IScanReflectData;

/**
 *
 * @author simon.buehlmann
 */
public class PolarToCartesian
{
    public static CartesianPoint calcCoords(IScanReflectData scanMeasData)
    {
        return new CartesianPoint(calcX(scanMeasData), calcY(scanMeasData));
    }

    /**
     * Calc the X Coordinate from the handed parameters
     *
     * @param data
     * @param angleDeg
     * @param refX
     * @return
     */
    private static int calcX(IScanReflectData scanMeasData)
    {
        double cos = Math.cos(Math.toRadians(scanMeasData.getAngle()));
        
        double tempReturn = (cos * scanMeasData.getDistance());
        return (int) tempReturn;
    }

    /**
     * Calc the Y Cordinate from the handed parameters
     *
     * @param data
     * @param angleDeg
     * @param refY
     * @return
     */
    private static int calcY(IScanReflectData scanMeasData)
    {
        double sin = Math.sin(Math.toRadians(scanMeasData.getAngle()));
        
        double tempReturn = (sin * scanMeasData.getDistance());
        return (int) tempReturn;
    }
}
