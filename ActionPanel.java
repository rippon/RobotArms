package robotArms;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

public class ActionPanel extends JPanel {
   public static final double originXdisplacement = 0.5, 
                              originYdisplacement = 0.5;
   final private double armLength = SystemProperties.armLength;
   private static final double SOME_MASSIVE_NUMBER = 20000000;
   final private Point targetPointPhys = new Point(0,0);
   private double D = 0, Phi = 0; // polar coords of target point
   private boolean A1GoACW = true, A2GoACW = true;
   // A1GoACW means 'Arm 1 Go anti-clockwise'
   private boolean R1_leadsPhi = true;
   private double R1ACWR1_leadsPhi = 0, R2ACWforR1_leadsPhi = 0;
   private double R1CWR1_leadsPhi = 0, R2CWforR1_leadsPhi = 0;
   private double R1ACWR1_lagsPhi = 0, R2ACWforR1_lagsPhi = 0;
   private double R1CWR1_lagsPhi = 0, R2CWforR1_lagsPhi = 0;
   private double angleIncrement = Math.PI/200;
   private double R1ACWandR2ACW_R1_leads = 0, R1ACWandR2CW_R1_leads = 0;
   private double R1CWandR2ACW_R1_leads = 0, R1CWandR2CW_R1_leads = 0;
   private double R1ACWandR2ACW_R1_lags = 0, R1ACWandR2CW_R1_lags = 0;
   private double R1CWandR2ACW_R1_lags = 0, R1CWandR2CW_R1_lags = 0;
   private JTextField [] messageBoxes = new JTextField[8];
   private JButton [] apButtons = new JButton[8];
   private Configuration configuration;
   public JTextField publicTextField = new JTextField("public field", 20);
   private int timerInterval = 10; //milliseconds
   private int animateCounter = 0;
   private boolean playHasBeenClicked = false;
   private boolean movementHappening = false;
   private boolean pointToPointMode = false;
   private boolean firstPtoPset = false;
   private boolean secondPtoPset = false;
   private boolean allowedToClickAPoint = true;
   private ArrayList<Point> trailPoints = new ArrayList<Point>();
   
    public ActionPanel() { // constructor
      configuration = new Configuration(this);
      for (int i=0; i<8; i++){
          messageBoxes[i] = new JTextField("messageBox" + i, 35);
          apButtons[i] = new JButton("Button" + i);
          if (i==0) apButtons[i].setText("Play");
          if (i==1) apButtons[i].setText("Pause");
          if (i==2) apButtons[i].setText("Reset");
          if (i==3) apButtons[i].setText("myFunc points");
          if (i==4) apButtons[i].setText("Optimise");
      }
      for (int i=0; i<5; i++){
//          if ( (i != 0) && (i != 1) && (i != 2) && (i != 3) ) add(apButtons[i]);
      }
      addListeners();
    } // end of constructor: ActionPanel()
    
    private void addListeners(){
      apButtons[0].addActionListener(new Button0Watcher()); // "Play"
      apButtons[1].addActionListener(new Button1Watcher()); // "Pause"
      apButtons[3].addActionListener(new Button3Watcher()); // "myFunc points"
      apButtons[4].addActionListener(new Button4Watcher()); // "Optimise"
      addMouseListener(new MouseWatcher());
    }
        
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawEverything(g);
    }
    
    private void drawEverything(Graphics g){
        int w = getWidth(), h = getHeight(); // width, height;
        Point origin = new Point(0,0);
        Point twoZeroToPhysPoint = new Point();
        int limitRadius;
        int targetBlobWidth = 7, targetBlobHeight = 7;
        int trailBlobWidth = 2, trailBlobHeight = 2;
        
        origin.x = (int) (originXdisplacement * w);
        origin.y = (int) (originYdisplacement * h);
        g.drawLine(0, origin.y, w, origin.y); // x-axis
        g.drawLine(origin.x, 0, origin.x, h); // y-axis
        twoZeroToPhysPoint.setLocation( CoordTransformation.calcPhysPoint(new Point2D.Double(2,0), w, h) );
        limitRadius =  (int) twoZeroToPhysPoint.getX() - origin.x;
        g.drawOval(origin.x - limitRadius, origin.y - limitRadius, 2*limitRadius, 2*limitRadius);
        if (playHasBeenClicked){
          configuration.draw(g,w,h);
          g.setColor(Color.black);
          blob(g, new Point(targetPointPhys),
                  targetBlobWidth,targetBlobHeight);
          g.setColor(Color.red);
          for (int i=0; i<trailPoints.size(); i++){
              blob(g, new Point(trailPoints.get(i)),
                      trailBlobWidth,trailBlobHeight);
          }
        }
    } // end of: drawEverything(Graphics g)
        
    public ActionListener animateRotation = 
     new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
          animateCounter++;
          if (R1_leadsPhi)
          {
            if (A1GoACW && A2GoACW)
            {
              rotateA1ACWaBit(R1ACWR1_leadsPhi);
              rotateA2ACWaBit(R2ACWforR1_leadsPhi);
              repaint();
              if ((configuration.get_theta_1() > R1ACWR1_leadsPhi) &&
                  (configuration.get_theta_2() > R2ACWforR1_leadsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            } // end of: if (A1GoACW && A2GoACW)
            if (A1GoACW && !A2GoACW)
            {
              rotateA1ACWaBit(R1ACWR1_leadsPhi);
              rotateA2CWaBit(R2CWforR1_leadsPhi);
              repaint();
              if ((configuration.get_theta_1() > R1ACWR1_leadsPhi) &&
                  (configuration.get_theta_2() < R2CWforR1_leadsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            } // end of: if (A1GoACW && !A2GoACW)
            if (!A1GoACW && A2GoACW)
            {
              rotateA1CWaBit(R1CWR1_leadsPhi);
              rotateA2ACWaBit(R2ACWforR1_leadsPhi);
              repaint();
              if ((configuration.get_theta_1() < R1CWR1_leadsPhi) &&
                  (configuration.get_theta_2() > R2ACWforR1_leadsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            }
            if (!A1GoACW && !A2GoACW) // i.e. both arms go clockwise
            {
              rotateA1CWaBit(R1CWR1_leadsPhi);
              rotateA2CWaBit(R2CWforR1_leadsPhi);
              repaint();
              if ((configuration.get_theta_1() < R1CWR1_leadsPhi) &&
                  (configuration.get_theta_2() < R2CWforR1_leadsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            } // end of: if (!A1GoACW && !A2GoACW)
          } // end of: if (R1_leadsPhi)
          if (!R1_leadsPhi) // i.e. R1 lags behind Phi
          {
            if (A1GoACW && A2GoACW)
            {
              rotateA1ACWaBit(R1ACWR1_lagsPhi);
              rotateA2ACWaBit(R2ACWforR1_lagsPhi);
              repaint();
              if ((configuration.get_theta_1() > R1ACWR1_lagsPhi) &&
                  (configuration.get_theta_2() > R2ACWforR1_lagsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            }
            if (A1GoACW && !A2GoACW)
            {
              rotateA1ACWaBit(R1ACWR1_lagsPhi);
              rotateA2CWaBit(R2CWforR1_lagsPhi);
              repaint();
              if ((configuration.get_theta_1() > R1ACWR1_lagsPhi) &&
                  (configuration.get_theta_2() < R2CWforR1_lagsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            } // end of: if (A1GoACW && !A2GoACW)
            if (!A1GoACW && A2GoACW)
            {
              rotateA1CWaBit(R1CWR1_lagsPhi);
              rotateA2ACWaBit(R2ACWforR1_lagsPhi);
              repaint();
              if ((configuration.get_theta_1() < R1CWR1_lagsPhi) &&
                  (configuration.get_theta_2() > R2ACWforR1_lagsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            } // end of: if (A1GoACW && !A2GoACW)
            if (!A1GoACW && !A2GoACW) // i.e. both arms go clockwise
            {
              rotateA1CWaBit(R1CWR1_lagsPhi);
              rotateA2CWaBit(R2CWforR1_lagsPhi);
              repaint();
              if ((configuration.get_theta_1() < R1CWR1_lagsPhi) &&
                  (configuration.get_theta_2() < R2CWforR1_lagsPhi)){
                rotationTimer.stop(); movementHappening = false;
              }
            } // end of: if (!A1GoACW && !A2GoACW)
          } // end of: if (!R1_leadsPhi)
       } // end of: public void actionPerformed(ActionEvent e)
    }; // end of: private ActionListener animateRotation = new ActionListener()
    
    private void printMessages(){
       messageBoxes[0].setText("animateCounter = " + animateCounter);
       messageBoxes[1].setText("theta_1 = " + 
           String.format("%5.2f", configuration.get_theta_1()));
       messageBoxes[2].setText("theta_2 = " + 
           String.format("%5.2f", configuration.get_theta_2()));
    }
    
    public javax.swing.Timer rotationTimer = 
       new javax.swing.Timer(timerInterval, animateRotation);
       

    private class Button0Watcher/*"Play"*/implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a) {
            playHasBeenClicked = true;
            publicTextField.setText
            ("D = " + String.format("%5.1f", D) + ";  Phi = " + 
              String.format("%5.1f", Phi/Math.PI*180) + "; R1ACWR1_leadsPhi = " +
              String.format("%5.1f", R1ACWR1_leadsPhi/Math.PI*180) + "; R2ACWforR1_leadsPhi = " +
              String.format("%5.1f", R2ACWforR1_leadsPhi/Math.PI*180)
            );
            rotationTimer.start(); movementHappening = true;
        } // end of: public void actionPerformed(ActionEvent a)
    } // end of: private class Button0Watcher implements ActionListener

    private class Button1Watcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a) {
            rotationTimer.stop();
        }
    }
    
    private class Button3Watcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a) {
            double x = 0.822, y = 0, root = 0;
            int numberOfPoints = 100/2;
            double xCoords [] = new double[numberOfPoints];
            double yCoords [] = new double[numberOfPoints];
            double previousValue = 100, newValue=0;
            double increment = 2.0/10000;
            int thisOne = 0;
            
            // initialisation
            for (int i=0; i<numberOfPoints; i++) {
                xCoords[i] = 0; yCoords[i] = 0;
            }
            // find where myFunc has its minimum magnitude - looking for
            // its zero root
            for (int i=0; i<numberOfPoints; i++) {
              xCoords[i] = i * 2.0/numberOfPoints;
              y = 0; previousValue = 100;
              do {
                   y = y + increment; newValue = myFunc(xCoords[i],y);
                   if (Math.abs(newValue) < Math.abs(previousValue)) {
                      previousValue = newValue;
                      yCoords[i] = y;
                   }
              } while (y <= 2);
            }

            // now plot all the root points                       
            for (int i=0; i<numberOfPoints; i++){
               Point2D.Double mathPoint = 
                 new Point2D.Double(xCoords[i], yCoords[i]);
               Point physPoint = CoordTransformation.
                    calcPhysPoint(mathPoint, getWidth(), getHeight());
               blob(getGraphics(), physPoint, 5,5);
            }
          }; // end of: public void actionPerformed(..)
    } // end of: private class Button3Watcher implements ActionListener
    
    private class Button4Watcher implements ActionListener { // "Optimise"
        @Override
        public void actionPerformed(ActionEvent a) {
            setOptimumChoices();
            messageBoxes[3].setText("optimum choice is: " + findOptimumChoice());
            getGraphics().drawLine(0,0,getWidth()-1,getHeight()-1);
        }
    }
    
    private class MouseWatcher implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e){
          double x, y;
          Point physPoint = new Point(0,0); // physical point
          Point physPoint1 = new Point(0,0); // physical point
          Point physPoint2 = new Point(0,0); // physical point
          Point2D.Double mathPoint = 
                     new Point2D.Double(0,0); // mathematical point
          
          physPoint.x = e.getX(); physPoint.y = e.getY();
          x = CoordTransformation.calcMathPointX(physPoint, getWidth(), getHeight());
          y = CoordTransformation.calcMathPointY(physPoint, getWidth(), getHeight());
          if (allowedToClickAPoint){
            if (pointToPointMode == false) {
              allowedToClickAPoint = false;
              targetPointPhys.setLocation(physPoint.getLocation());
              mathPoint.setLocation(x, y);
              blob(getGraphics(), physPoint, 10, 10);
              D = Math.sqrt(square(x) + square(y));
              Phi = determinePhi(x,y);
              setTheRotationAngles();
              allowedToClickAPoint = false;
            } // end of: if (pointToPointMode == false)
            if (pointToPointMode == true) {
              if (firstPtoPset == false) {
                physPoint1.x = physPoint.x; physPoint1.y = physPoint.y;
                firstPtoPset = true;
                blob(getGraphics(), physPoint1, 10, 10);
              }
              else {
                allowedToClickAPoint = false;
                pointToPointMode = false;
                physPoint2.x = physPoint.y; physPoint2.y = physPoint.y;
                secondPtoPset = true;
                blob(getGraphics(), physPoint2, 10, 10);
              } 
            } // end of: if (pointToPointMode == true)
          } // end of: if (allowedToClickAPoint)
      } // end of: public void mouseClicked(MouseEvent e)
        @Override
      public void mouseEntered(MouseEvent e){}
        @Override
      public void mouseReleased(MouseEvent e){}
        @Override
      public void mousePressed(MouseEvent e){}
        @Override
      public void mouseExited(MouseEvent e){}
    } // end of private class MouseWatcher implements ... 
    
    public double determinePhi(double x, double y){
      if ((x > 0) && (y >= 0)){ // first quadrant
       Phi = Math.atan(y/x);
      }
      if ((x < 0) && (y >= 0)){  // second quadrant
       Phi = Math.PI + Math.atan(y/x);
      }
      if ((x < 0) && (y <= 0)){  // third quadrant
       Phi = Math.PI + Math.atan(y/x);
      }
      if ((x > 0) && (y <= 0)){  // fourth quadrant
       Phi = 2*Math.PI + Math.atan(y/x);
      }
      if ((x == 0) && (y >= 0)) {  // +ve y-axis
          Phi = Math.PI/2;
      }
      if ((x == 0) && (y <= 0)) { // -ve y-axis
          Phi = 3*Math.PI/2;
      }
      return Phi;
    } // end of public double determinePhi(...)
    
    public void rotateA1ACWaBit(double R1ACWlimit){
            if (configuration.get_theta_1() < R1ACWlimit){
              configuration.setTheta_1(animateCounter * angleIncrement);
            }
    }
    public void rotateA2ACWaBit(double R2ACWlimit){
        if (configuration.get_theta_2() < R2ACWlimit){
          configuration.setTheta_2(animateCounter * angleIncrement);
        }
    }
    public void rotateA1CWaBit(double R1CWlimit){
            if (configuration.get_theta_1() > R1CWlimit){
              configuration.setTheta_1(animateCounter * -angleIncrement);
            }
    }
    public void rotateA2CWaBit(double R2CWlimit){
        if (configuration.get_theta_2() > R2CWlimit){
          configuration.setTheta_2(animateCounter * -angleIncrement);
        }
    }
    
    
    public String findOptimumChoice(){
     String optimumChoice = "R1ACWR1_leadsPhiAndR2ACWforR1leadsPhi";
     double optimumMaximum = SOME_MASSIVE_NUMBER;
        if (max(abs(R1ACWR1_leadsPhi), abs(R2ACWforR1_leadsPhi)) < optimumMaximum) {
          optimumMaximum = max(abs(R1ACWR1_leadsPhi), abs(R2ACWforR1_leadsPhi));
          optimumChoice = "R1ACWR1_leadsPhiAndR2ACWforR1_leadsPhi";  //1
        }
        if (max(abs(R1ACWR1_leadsPhi), abs(R2CWforR1_leadsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1ACWR1_leadsPhi), abs(R2CWforR1_leadsPhi));
         optimumChoice = "R1ACWR1_leadsPhiAndR2CWforR1_leadsPhi";  //2
        }
        if (max(abs(R1CWR1_leadsPhi), abs(R2ACWforR1_leadsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1CWR1_leadsPhi), abs(R2ACWforR1_leadsPhi));
         optimumChoice = "R1CWR1_leadsPhiAndR2ACWforR1_leadsPhi";  //3
        }
        if (max(abs(R1CWR1_leadsPhi), abs(R2CWforR1_leadsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1CWR1_leadsPhi), abs(R2CWforR1_leadsPhi));
         optimumChoice = "R1CWR1_leadsPhiAndR2CWforR1_leadsPhi";  //4
        }
        if (max(abs(R1ACWR1_lagsPhi), abs(R2ACWforR1_lagsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1ACWR1_lagsPhi), abs(R2ACWforR1_lagsPhi));
         optimumChoice = "R1ACWR1_lagsPhiAndR2ACWforR1_lagsPhi";  //5
        }
        if (max(abs(R1ACWR1_lagsPhi), abs(R2CWforR1_lagsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1ACWR1_lagsPhi), abs(R2CWforR1_lagsPhi));
         optimumChoice = "R1ACWR1_lagsPhiAndR2CWforR1_lagsPhi";  //6
        }
        if (max(abs(R1CWR1_lagsPhi), abs(R2ACWforR1_lagsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1CWR1_lagsPhi), abs(R2ACWforR1_lagsPhi));
         optimumChoice = "R1CWR1_lagsPhiAndR2ACWforR1_lagsPhi";  //7
        }
        if (max(abs(R1CWR1_lagsPhi), abs(R2CWforR1_lagsPhi)) < optimumMaximum) {
         optimumMaximum = max(abs(R1CWR1_lagsPhi), abs(R2CWforR1_lagsPhi));
         optimumChoice = "R1CWR1_lagsPhiAndR2CWforR1_lagsPhi";  //8
        }
        return optimumChoice;
    }
    
    public void setOptimumChoices(){
        if (findOptimumChoice().equals("R1ACWR1_leadsPhiAndR2ACWforR1_leadsPhi")){     //1
            R1_leadsPhi = true; A1GoACW = true; A2GoACW = true;
        }
        if (findOptimumChoice().equals("R1ACWR1_leadsPhiAndR2CWforR1_leadsPhi")){      //2
            R1_leadsPhi = true; A1GoACW = true; A2GoACW = false;
        }
        if (findOptimumChoice().equals("R1CWR1_leadsPhiAndR2ACWforR1_leadsPhi")){      //3
            R1_leadsPhi = true; A1GoACW = false; A2GoACW = true;
        }
        if (findOptimumChoice().equals("R1CWR1_leadsPhiAndR2CWforR1_leadsPhi")){       //4
            R1_leadsPhi = true; A1GoACW = false; A2GoACW = false;
        }
        if (findOptimumChoice().equals("R1ACWR1_lagsPhiAndR2ACWforR1_lagsPhi")){       //5
            R1_leadsPhi = false; A1GoACW = true; A2GoACW = true;
        }
        if (findOptimumChoice().equals("R1ACWR1_lagsPhiAndR2CWforR1_lagsPhi")){        //6
            R1_leadsPhi = false; A1GoACW = true; A2GoACW = false;
        }
        if (findOptimumChoice().equals("R1CWR1_lagsPhiAndR2ACWforR1_lagsPhi")){        //7
            R1_leadsPhi = false; A1GoACW = false; A2GoACW = true;
        }
        if (findOptimumChoice().equals("R1CWR1_lagsPhiAndR2CWforR1_lagsPhi")){         //8
            R1_leadsPhi = false; A1GoACW = false; A2GoACW = false;
        }
    }
    
    public void setTheRotationAngles() {
        setR1ACWR1_leadsPhi(D, Phi); setR2ACWforR1_leadsPhi(D, Phi);
        setR1ACWR1_lagsPhi(D, Phi); setR2ACWforR1_lagsPhi(D, Phi);
        R1CWR1_leadsPhi = -(2*Math.PI - R1ACWR1_leadsPhi);
        R2CWforR1_leadsPhi = -(2*Math.PI - R2ACWforR1_leadsPhi);
        R1CWR1_lagsPhi = -(2*Math.PI - R1ACWR1_lagsPhi);
        R2CWforR1_lagsPhi = -(2*Math.PI - R2ACWforR1_lagsPhi);
/*********************************************************/
        R1ACWandR2ACW_R1_leads = 
             max(abs(R1ACWR1_leadsPhi), abs(R2ACWforR1_leadsPhi));
        R1ACWandR2CW_R1_leads =
             max(abs(R1ACWR1_leadsPhi), abs(R2CWforR1_leadsPhi));
        R1CWandR2ACW_R1_leads = 
             max(abs(R1CWR1_leadsPhi), abs(R2ACWforR1_leadsPhi));
        R1CWandR2CW_R1_leads = 
             max(abs(R1CWR1_leadsPhi), abs(R2CWforR1_leadsPhi));
        R1ACWandR2ACW_R1_lags = 
             max(abs(R1ACWR1_lagsPhi), abs(R2ACWforR1_lagsPhi));
        R1ACWandR2CW_R1_lags = 
             max(abs(R1ACWR1_lagsPhi), abs(R2CWforR1_lagsPhi));
        R1CWandR2ACW_R1_lags = 
             max(abs(R1CWR1_lagsPhi), abs(R2ACWforR1_lagsPhi));
        R1CWandR2CW_R1_lags = 
             max(abs(R1CWR1_lagsPhi), abs(R2CWforR1_lagsPhi));
    }
    
    public void resetTheta_1(){ configuration.setTheta_1(0); }
    public void resetTheta_2(){ configuration.setTheta_2(0); }
    
    public void setD(double toThis){D = toThis;}
    public void setPhi(double usingThis){Phi = usingThis;}
    public void setR1ACWR1_leadsPhi(double D, double Phi){
        if ((Phi >= 0) && (Phi <= 2*Math.PI)){
           R1ACWR1_leadsPhi = 
             (Phi + Math.acos((D/2)/armLength)) % (2 * Math.PI);
        }
    } // end of setR1ACWR1_leadsPhi(double D, double Phi)
    public void setR1ACWR1_leadsPhi(double toThis){
        R1ACWR1_leadsPhi = toThis;
    }
    public void setR1CWR1_leadsPhi(double toThis){
        R1CWR1_leadsPhi = toThis;
    }
    public void setR1CWR1_lagsPhi(double toThis){
        R1CWR1_lagsPhi = toThis;
    }
    public void setR2ACWforR1_leadsPhi(double D, double Phi){
        if ((Phi >= 0) && (Phi <= 2*Math.PI)){
           R2ACWforR1_leadsPhi = 
                     (2*Math.asin((D/2)/armLength)) % (2 * Math.PI);
        }
    } // end of: setR2ACWforR1_leadsPhi(double D, double Phi)
    public void setR2ACWforR1_leadsPhi(double toThis){
            R2ACWforR1_leadsPhi = toThis;
    }
    public void setR1ACWR1_lagsPhi(double D, double Phi){
        if ((Phi >= 0) && (Phi <= 2*Math.PI)){
            double temp = Phi - Math.acos((D/2)/armLength);
            if (temp >= 0) R1ACWR1_lagsPhi = temp % (2 * Math.PI);
            else R1ACWR1_lagsPhi = (temp + 2*Math.PI) % (2 * Math.PI);
        }
    } // end of setR1ACWR1_lagsPhi(double D, double Phi)
    public void setR1ACWR1_lagsPhi(double toThis){
          R1ACWR1_lagsPhi = toThis;
    }
    public void setR2ACWforR1_lagsPhi(double D, double Phi){
        if ((Phi >= 0) && (Phi <= 2*Math.PI)){
           R2ACWforR1_lagsPhi = 
             (2*(Math.PI - Math.asin((D/2)/armLength))) % (2 * Math.PI);
        }
    } // end of setR2ACWforR1_lagsPhi(double D, double Phi)
    public void setR2ACWforR1_lagsPhi(double toThis){
          R2ACWforR1_lagsPhi = toThis;
    }
    public void setR2CWforR1_leadsPhi(double toThis){
          R2CWforR1_leadsPhi = toThis;
    }
    public void setR2CWforR1_lagsPhi(double toThis){
          R2CWforR1_lagsPhi = toThis;
    }
    public void setA1GoACW(boolean toThis){ A1GoACW = toThis; }
    public void setA2GoACW(boolean toThis){ A2GoACW = toThis; }
    public void setR1_leadsPhi(boolean toThis){ R1_leadsPhi = toThis; }
    public void setAnimateCounter(int toThis){ animateCounter = toThis; }
    public double getAngleIncrement(){ return angleIncrement; }
    public void setAngleIncrement(double toThis){angleIncrement = toThis;}
    public int getTimerInterval(){ return timerInterval; }
    public void setTimerInterval(int toThis){timerInterval = toThis;}
    public void setPlayHasBeenClicked(boolean toThis){playHasBeenClicked = toThis;}
    public double getD(){ return D; }
    public double getPhi(){ return Phi; }
    public void setTargetPointPhys(Point toThis){
        targetPointPhys.setLocation(toThis);
    }
    public Point getTargetPointPhys(){
        return targetPointPhys;
    }
    /*1*/public double getR1ACWR1_leadsPhi(){ return R1ACWR1_leadsPhi; }
    /*2*/public double getR1CWR1_leadsPhi(){ return R1CWR1_leadsPhi; }
    /*3*/public double getR1ACWR1_lagsPhi(){ return R1ACWR1_lagsPhi; }
    /*4*/public double getR1CWR1_lagsPhi(){ return R1CWR1_lagsPhi; }
    /*5*/public double getR2ACWforR1_leadsPhi(){ return R2ACWforR1_leadsPhi; }
    /*6*/public double getR2CWforR1_leadsPhi(){ return R2CWforR1_leadsPhi; }
    /*7*/public double getR2ACWforR1_lagsPhi(){ return R2ACWforR1_lagsPhi; }
    /*8*/public double getR2CWforR1_lagsPhi(){ return R2CWforR1_lagsPhi; }
    public boolean getA1GoACW(){ return A1GoACW; }
    public boolean getA2GoACW(){ return A2GoACW; }
    public boolean getR1_leadsPhi(){ return R1_leadsPhi; }
    public double get_theta_1(){ return configuration.get_theta_1(); }
    public double get_theta_2(){ return configuration.get_theta_2(); }
    public boolean getMovementHappening(){return movementHappening;}
    public void setMovementHappening(boolean toThis){movementHappening = toThis;}
    /********************/
    public double getR1ACWandR2ACW_R1_leads(){ return R1ACWandR2ACW_R1_leads; }
    public double getR1CWandR2ACW_R1_leads(){ return R1CWandR2ACW_R1_leads; }
    public double getR1ACWandR2ACW_R1_lags(){ return R1ACWandR2ACW_R1_lags; }
    public double getR1CWandR2ACW_R1_lags(){ return R1CWandR2ACW_R1_lags; }
    public double getR1ACWandR2CW_R1_leads(){ return R1ACWandR2CW_R1_leads; }
    public double getR1CWandR2CW_R1_leads(){ return R1CWandR2CW_R1_leads; }
    public double getR1ACWandR2CW_R1_lags(){ return R1ACWandR2CW_R1_lags; }
    public double getR1CWandR2CW_R1_lags(){ return R1CWandR2CW_R1_lags; }
    /**************************/
    public void setR1ACWandR2ACW_R1_leads(double toThis){
        R1ACWandR2ACW_R1_leads = toThis; 
    }
    public void setR1CWandR2ACW_R1_leads(double toThis){
        R1CWandR2ACW_R1_leads = toThis; 
    }
    public void setR1ACWandR2ACW_R1_lags(double toThis){
        R1ACWandR2ACW_R1_lags = toThis; 
    }
    public void setR1CWandR2ACW_R1_lags(double toThis){
        R1CWandR2ACW_R1_lags = toThis; 
    }
    public void setR1ACWandR2CW_R1_leads(double toThis){
        R1ACWandR2CW_R1_leads = toThis; 
    }
    public void setR1CWandR2CW_R1_leads(double toThis){
        R1CWandR2CW_R1_leads = toThis; 
    }
    public void setR1ACWandR2CW_R1_lags(double toThis){
        R1ACWandR2CW_R1_lags = toThis; 
    }
    public void setR1CWandR2CW_R1_lags(double toThis){
        R1CWandR2CW_R1_lags = toThis; 
    }
    /*****************************/
    public void setPointToPointMode(boolean toThis){
        pointToPointMode = toThis;
    }
    public boolean getPointToPointMode(){ return pointToPointMode; }
    public void setAllowedToClickAPoint(boolean toThis){
        allowedToClickAPoint = toThis;
    }
    public boolean getAllowedToClickAPoint(){ return allowedToClickAPoint; }
    public void setFirstPtoPset(boolean toThis){firstPtoPset=toThis;}
    public void setSecondPtoPset(boolean toThis){secondPtoPset=toThis;}
    public ArrayList<Point> getTrailPoints(){ return trailPoints; }
    
    private double myFunc(double x, double y){
        return Math.atan(y/x) + 
                     Math.acos(
                               squareRoot( (square(x)+square(y)) )/2
                              )
                     - 2*Math.asin(
                                   squareRoot( (square(x)+square(y)) )/2
                                  );
    } // end of mFunc(..)
    
    private double square(double in){
        return in*in;
    }
    private double squareRoot(double in){
        return Math.sqrt(in);
    }
    public void blob(Graphics g, Point thePoint, int width, int height){
       g.fillOval((int)(thePoint.x - width/2.0), 
                  (int)(thePoint.y - height/2.0),
                  width, height);
    }
    private double abs(double input){
        return Math.abs(input);
    }
    private double max(double a, double b){ return Math.max(a,b); }
    
    
} // end of: public class ActionPanel extends JPanel
