/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotArms;

/**
 *
 * @author Rippon
 */
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Write a description of class CoordTransformation here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CoordTransformation
{
    private static double xMin = -2.5, xMax = 2.5;
    private static double yMin = -2.5, yMax = 2.5;

    /**
     * Constructor for objects of class CoordTransformation
     */
    public CoordTransformation()
    {
    }
    
    public static Point calcPhysPoint(Point2D.Double mathPoint, int apW, int apH){
     // a physical point is what is actually plotted, and has to be caluculated
     // from a mathematical point
        Point answer = new Point(0,0);
        
        answer.x = (int)((mathPoint.x - xMin)/(xMax - xMin) * apW);
        answer.y = (int)(apH - (mathPoint.y - yMin)/(yMax - yMin) * apH);
        return answer;
    } // end of: private Point calcPhyPoints
    
    public static Point2D.Double calcMathPoint(Point physPoint, int apW, int apH){
        Point2D.Double result = new Point2D.Double();
        
        /*
        result.x = (physPoint.x - apW/5.0) * 2.5*5.0/(4*apW);
        result.y = (-physPoint.y + (4/5.0)*apH) * 2.5*5.0/(4*apH);
        */
        result.x = ((double)physPoint.x/apW) * (xMax - xMin) + xMin;
        result.y = ((apH - physPoint.y)*(yMax - yMin)/apH) + yMin;
        return result;
    }
    public static double calcMathPointX(Point physPoint, int apW, int apH){
        return ((double)physPoint.x/apW) * (xMax - xMin) + xMin;
    }
    public static double calcMathPointY(Point physPoint, int apW, int apH){
        return ((apH - physPoint.y)*(yMax - yMin)/apH) + yMin;
    }

}

