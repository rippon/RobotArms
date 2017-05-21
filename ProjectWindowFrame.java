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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ProjectWindowFrame extends JFrame
{
    private int frameWidth;
    private int frameHeight;
    private final int NUMBERof_DEBUGINFO_BOXES = 20; //number of debug info boxes
    private JButton exitButton = new JButton("Exit");
    protected ActionPanel actionPanel = new ActionPanel();
    private int graphingCounter = 0;
    private JPanel leftPanel = new JPanel(new GridLayout(2,1));
    private JPanel rightPanel = new JPanel(new GridLayout());
    private JPanel controlPanel_1 = new JPanel(new GridLayout(5,1));
    private JPanel controlPanel_2 = new JPanel();
    private JPanel topBit = new JPanel(new GridLayout(4,1));
    private JPanel middleBit = new JPanel();
    private JPanel bottomBit = new JPanel();
    private JPanel debugPanel = new JPanel(new GridLayout(NUMBERof_DEBUGINFO_BOXES + 1, 1));
    private JButton debugButton = new JButton("Debug");
    
    private JButton goButton = new JButton("Go");
    private JButton resetButton = new JButton("Reset");
    private JButton optimiseButton = new JButton("Optimise");    
    
    private JTextField [] debugInfo = new JTextField[20];
    private JTextField [] actionPanelInfo = new JTextField[20];
    private JTextField middleDebugInfo = new JTextField("middle debug info ", 20);
    private int timerInterval = 10;
    Border redlineBorder = BorderFactory.createLineBorder( Color.red );
    Border bluelineBorder = BorderFactory.createLineBorder( Color.blue );
    Border blacklineBorder = BorderFactory.createLineBorder( Color.black );
    Border cyanlineBorder = BorderFactory.createLineBorder( Color.cyan );
    Border darkGraylineBorder = BorderFactory.createLineBorder( Color.darkGray );
    Border greenlineBorder = BorderFactory.createLineBorder( Color.green );
    Border whitelineBorder = BorderFactory.createLineBorder( Color.white );
    private double R1 = 0, R2 = 0;
    enum R1Type{R1ACWR1LEADSPHI, R1ACWR1LAGSPHI,
    R1CWR1LEADSPHI, R1CWR1LAGSPHI};
    enum R2Type{R2ACWR1LEADSPHI, R2ACWR1LAGSPHI,
    R2CWR1LEADSPHI, R2CWR1LAGSPHI};    
    
    private void initialInitialisation(){
        Toolkit theKit = this.getToolkit(); // Get the window toolkit
        Dimension wndSize = theKit.getScreenSize(); // Get screen size
        frameWidth = wndSize.width;
        frameHeight = wndSize.height;
        this.setSize(frameWidth / 2, frameHeight / 2);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
    }
      
    public ProjectWindowFrame()
    {
        initialInitialisation();
        
        exitButton.    addActionListener(new ExitButtonWatcher());

        goButton.    addActionListener(new PlayButtonWatcher());
        resetButton.   addActionListener(new ResetButtonWatcher());
        optimiseButton.addActionListener(new OptimiseButtonWatcher());
        
        controlPanel_2.setLayout(new GridLayout(3,1));
        controlPanel_2.add(topBit);
        controlPanel_2.add(middleBit);
        controlPanel_2.add(bottomBit);
        for (int i=0; i<4; i++){
            actionPanelInfo[i] = new JTextField("info box " + i, 20);
            if (i<2) topBit.add(actionPanelInfo[i]);
            if (i==2) middleBit.add(actionPanelInfo[i]);
            if (i==3) bottomBit.add(actionPanelInfo[i]);
        }

        controlPanel_1.add(exitButton);
        controlPanel_1.add(goButton);
        controlPanel_1.add(resetButton);
        controlPanel_1.add(optimiseButton);

        rightPanel.setBackground(Color.red);
        leftPanel.add(controlPanel_1);
        leftPanel.add(controlPanel_2);
        
//        JOptionPane.showMessageDialog(null,"actionPanel.getWidth() = " + actionPanel.getWidth() + "\n" +
//                                           "actionPanel.getHeight() = " + actionPanel.getHeight() + "\n");

        debugPanel.setBorder( bluelineBorder );
        debugPanel.add(debugButton);
        for (int i=0; i < NUMBERof_DEBUGINFO_BOXES; i++){
            debugInfo[i] = new JTextField("debugInfo[" + i + "]");
            debugPanel.add(debugInfo[i]);
        }
        
        finalInitialisation();

    } // end of constructor: public ProjectWindowFrame()
    
    private void finalInitialisation(){
//        JOptionPane.showMessageDialog(null,"hallo from 'finalInitialisation()'");
               
        GridBagLayout gridbag = new GridBagLayout(); // Create a layout manager
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(gridbag); // Set container layout mgr
                
        c.weightx = 10;
        c.weighty = 10;
        
        c.fill = GridBagConstraints.BOTH; // Fill the space
        
        c.gridx = 0;
        c.gridy = 0;
        add(leftPanel, c);
        
        c.weightx = 20;
        c.gridx = 1;
        add(rightPanel, c);
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        rightPanel.add(actionPanel, c);
    } // end of: private void finalInitialisation()        
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
    }

    public ActionListener callTheRotation = 
     new ActionListener(){
        int w, h; // width & height
        Graphics g;
         
        @Override
        public void actionPerformed(ActionEvent e){
          graphingCounter++;
          actionPanel.rotationTimer.start();
          if (actionPanel.getMovementHappening()){
            actionPanelInfo[0].setText("D = " + String.format("%5.2f", actionPanel.getD()) + "; Phi = " + 
            String.format("%5.2f", actionPanel.getPhi()/Math.PI*180) + " deg"
                                      );
            actionPanelInfo[1].setText(R1String() + "    " + R2String());
            actionPanelInfo[2].setText("theta_1 = " + 
              String.format("%5.2f", actionPanel.get_theta_1()/Math.PI*180) 
                 + " deg"             );
            actionPanelInfo[3].setText("theta_2 = " + 
              String.format("%5.2f", actionPanel.get_theta_2()/Math.PI*180) 
                 + " deg"             );
          }
          else {
              actionPanel.rotationTimer.start();
              callTheRotationTimer.stop();
          }
          g = middleBit.getGraphics();
          w = middleBit.getWidth(); h = middleBit.getHeight();
          if (R1 >= 0){ // graphing the angle evolution
             middleDebugInfo.setText("R1 >= 0");
             middleDebugInfo.setText("graphingCounter = " + graphingCounter);
             g.fillOval((int)(graphingCounter/(R1/actionPanel.getAngleIncrement())*w/4), 
               h - (int) (h/2*actionPanel.get_theta_1()/R1), 2,2
             );
          }
          if (R1 < 0){ // graphing the angle evolution
             middleDebugInfo.setText("R1 < 0");
             middleDebugInfo.setText("graphingCounter = " + graphingCounter);
             g.fillOval((int)(graphingCounter/(-R1/actionPanel.getAngleIncrement())*w/4), 
               (int) (h/2*actionPanel.get_theta_1()/R1), 2,2
             );
          }
          g = bottomBit.getGraphics();
          w = bottomBit.getWidth(); h = bottomBit.getHeight();
          if (R2 >= 0) 
           g.fillOval(// graphing the angle evolution
             (int)(graphingCounter/(R2/actionPanel.getAngleIncrement())*w/4),
             h - (int) (h/2*actionPanel.get_theta_2()/R2), 2,2
           );
          if (R2 < 0)  
           g.fillOval(// graphing the angle evolution
               (int)(graphingCounter/(-R2/actionPanel.getAngleIncrement())*w/4),
               (int) (h/2*actionPanel.get_theta_2()/R2), 2,2
           );
        }
    };

    private javax.swing.Timer callTheRotationTimer = 
       new javax.swing.Timer(timerInterval, callTheRotation);
    
    private class PlayButtonWatcher implements ActionListener {
        Graphics g;
        int w, h;
        
        @Override
        public void actionPerformed(ActionEvent a){
            actionPanel.setPlayHasBeenClicked(true);
            actionPanel.setTimerInterval(timerInterval);
            R1 = determineR1();
            R2 = determineR2();
            graphingCounter = 0;
            callTheRotationTimer.start();
            actionPanel.setMovementHappening(true);
        }
    } // end of: private class PlayButtonWatcher implements ActionListener
    private class ExitButtonWatcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a){
          System.exit(0);
        }
    }
    
    private class OptimiseButtonWatcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a){
            actionPanel.setOptimumChoices();
            JOptionPane.showMessageDialog(null,"solution optimised :-) ");
        }
    }
        
    private class ResetButtonWatcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent a){
          actionPanel.setAnimateCounter(0);
          actionPanel.setPhi(0);
          actionPanel.setA1GoACW(true);
          actionPanel.setA2GoACW(true);
          actionPanel.setR1_leadsPhi(true);
          actionPanel.resetTheta_1();
          actionPanel.resetTheta_2();
          actionPanel.setR1ACWR1_leadsPhi(0);
          actionPanel.setR1CWR1_leadsPhi(0);
          actionPanel.setR1ACWR1_lagsPhi(0);
          actionPanel.setR1CWR1_lagsPhi(0);
          actionPanel.setR2ACWforR1_leadsPhi(0);
          actionPanel.setR2CWforR1_leadsPhi(0);
          actionPanel.setR2ACWforR1_lagsPhi(0);
          actionPanel.setR2CWforR1_lagsPhi(0);
          /*****************/
          actionPanel.setR1ACWandR2ACW_R1_leads(0);
          actionPanel.setR1CWandR2ACW_R1_leads(0);
          actionPanel.setR1ACWandR2ACW_R1_lags(0);
          actionPanel.setR1CWandR2ACW_R1_lags(0);
          actionPanel.setR1ACWandR2CW_R1_leads(0);
          actionPanel.setR1CWandR2CW_R1_leads(0);
          actionPanel.setR1ACWandR2CW_R1_lags(0);
          actionPanel.setR1CWandR2CW_R1_lags(0);
          /*******************/
          actionPanel.setPlayHasBeenClicked(false);
          actionPanel.setMovementHappening(false);
          actionPanel.rotationTimer.stop();
          actionPanel.repaint();
          middleBit.repaint();
          bottomBit.repaint();
          actionPanel.setAllowedToClickAPoint(true);
          actionPanel.setPointToPointMode(false);
          actionPanel.setFirstPtoPset(false);
          actionPanel.setSecondPtoPset(false);
          actionPanel.getTrailPoints().clear();
        }
    }
    
    private R1Type whichR1(){
        if (actionPanel.getR1_leadsPhi()) {
            if (actionPanel.getA1GoACW())
              return R1Type.R1ACWR1LEADSPHI;
        }
        if (actionPanel.getR1_leadsPhi()) {
            if (!actionPanel.getA1GoACW())
              return R1Type.R1CWR1LEADSPHI;
        }
        if (!actionPanel.getR1_leadsPhi()) {
            if (actionPanel.getA1GoACW())
              return R1Type.R1ACWR1LAGSPHI;
        }
        if (!actionPanel.getR1_leadsPhi()) {
            if (!actionPanel.getA1GoACW())
              return R1Type.R1CWR1LAGSPHI;
        }
        return null;
    } // end of: private R1Type whichR1()
    
    private R2Type whichR2(){
        if (actionPanel.getR1_leadsPhi()) {
            if (actionPanel.getA2GoACW())
              return R2Type.R2ACWR1LEADSPHI;
        }
        if (actionPanel.getR1_leadsPhi()) {
            if (!actionPanel.getA2GoACW())
              return R2Type.R2CWR1LEADSPHI;
        }
        if (!actionPanel.getR1_leadsPhi()) {
            if (actionPanel.getA2GoACW())
              return R2Type.R2ACWR1LAGSPHI;
        }
        if (!actionPanel.getR1_leadsPhi()) {
            if (!actionPanel.getA2GoACW())
              return R2Type.R2CWR1LAGSPHI;
        }
        return null;
    } // end of: private R1Type whichR1()
    
    private double determineR1(){
      switch (whichR1()) {
        case R1ACWR1LEADSPHI:  return actionPanel.getR1ACWR1_leadsPhi();
        case R1CWR1LEADSPHI:  return actionPanel.getR1CWR1_leadsPhi();
        case R1ACWR1LAGSPHI:  return actionPanel.getR1ACWR1_lagsPhi();
        case R1CWR1LAGSPHI:  return actionPanel.getR1CWR1_lagsPhi();
        default:  return 0;
      }
    }
    private double determineR2(){
      switch (whichR2()) {
        case R2ACWR1LEADSPHI:  return actionPanel.getR2ACWforR1_leadsPhi();
        case R2CWR1LEADSPHI:  return actionPanel.getR2CWforR1_leadsPhi();
        case R2ACWR1LAGSPHI:  return actionPanel.getR2ACWforR1_lagsPhi();
        case R2CWR1LAGSPHI:  return actionPanel.getR2CWforR1_lagsPhi();
        default:  return 0;
      }
    }
        
    private String R1String(){
        if (whichR1() == R1Type.R1ACWR1LEADSPHI) {
              return "R1 = " + String.format("%5.2f", actionPanel.getR1ACWR1_leadsPhi()/Math.PI*180
                                            ) + " deg";
        }
        if (whichR1() == R1Type.R1CWR1LEADSPHI) {
              return "R1 = " + String.format("%5.2f", actionPanel.getR1CWR1_leadsPhi()/Math.PI*180
                                            ) + " deg";
        }
        if (whichR1() == R1Type.R1ACWR1LAGSPHI) {
              return "R1 = " + String.format("%5.2f", actionPanel.getR1ACWR1_lagsPhi()/Math.PI*180
                                            ) + " deg";
        }
        if (whichR1() == R1Type.R1CWR1LAGSPHI) {
              return "R1 = " + String.format("%5.2f", actionPanel.getR1CWR1_lagsPhi()/Math.PI*180
                                            ) + " deg";
        }
        return "";
    } // end of: private String R1String()
    
    public String R2String(){
        if (whichR2() == R2Type.R2ACWR1LEADSPHI) {
              return "R2 = " + String.format("%5.2f", actionPanel.getR2ACWforR1_leadsPhi()/Math.PI*180
                                            ) + " deg";
        }
        if (whichR2() == R2Type.R2CWR1LEADSPHI) {
              return "R2 = " + String.format("%5.2f", actionPanel.getR2CWforR1_leadsPhi()/Math.PI*180
                                            ) + " deg";
        }
        if (whichR2() == R2Type.R2ACWR1LAGSPHI) {
              return "R2 = " + String.format("%5.2f", actionPanel.getR2ACWforR1_lagsPhi()/Math.PI*180
                                            ) + " deg";
        }
        if (whichR2() == R2Type.R2CWR1LAGSPHI) {
              return "R2 = " + String.format("%5.2f", actionPanel.getR2CWforR1_lagsPhi()/Math.PI*180
                                            ) + " deg";
        }
        return "";
    } // end of: public String R2String()
    
    
} // end of public class ProjectWindowFrame extends JFrame
