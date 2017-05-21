/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotArms;

/**
 *
 * @author Rippon
 */
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

public class Configuration {
   int apW, apH;      // apW, apH are actionPanel width and height
   private double theta_1 = 0, theta_2 = 0;
   private final double armLength = SystemProperties.armLength;
   private final ArrayList<Point2D.Double> mathPoints = 
                                    new ArrayList<Point2D.Double>(3);
   private final ArrayList<Point> physicalPoints = new ArrayList<Point>(3);
   ActionPanel myAP;

    public Configuration(ActionPanel AP) {
        myAP = AP;
    }
    
    public void draw(Graphics g, int apW, int apH){
      double xCoord, yCoord;
      
      mathPoints.clear();
      physicalPoints.clear();
      mathPoints.add(new Point2D.Double(0,0));
      xCoord = armLength*Math.cos(theta_1);
      yCoord = armLength*Math.sin(theta_1);
      mathPoints.add(new Point2D.Double(xCoord, yCoord));
      xCoord = armLength*Math.cos(theta_1) - 
               armLength*Math.cos(theta_1 + theta_2);
      yCoord = armLength*Math.sin(theta_1) - 
               armLength*Math.sin(theta_1 + theta_2);
      mathPoints.add(new Point2D.Double(xCoord, yCoord));
      for (int i=0; i<3; i++){
       physicalPoints.add(new Point(CoordTransformation.
               calcPhysPoint(mathPoints.get(i),apW, apH)));
      } // end of: for for (int i=0; i<3; i++)
      g.setColor(Color.blue);
      //draw arm1
      setLineThickness(g, 3);
      g.drawLine(physicalPoints.get(0).x, physicalPoints.get(0).y,
                 physicalPoints.get(1).x, physicalPoints.get(1).y);
      g.setColor(Color.green);
      // draw arm2
      g.drawLine(physicalPoints.get(1).x, physicalPoints.get(1).y,
                 physicalPoints.get(2).x, physicalPoints.get(2).y);
      myAP.getTrailPoints().add(physicalPoints.get(2));
    } // end of: public void draw(Graphics g)
    
    private void setLineThickness(Graphics g, float thickness){
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(thickness));
    }
    
    public void setTheta_1(double toThis){theta_1 = toThis;}
    public double get_theta_1(){return theta_1;}
    public void setTheta_2(double toThis){theta_2 = toThis;}
    public double get_theta_2(){return theta_2;}
    public ArrayList<Point> getPhysicalPoints(){ return physicalPoints; }    

} // end of: public class Configuration
