/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotArms;

/**
 *
 * @author Rippon
 */
public class SystemProperties {
    public static double armLength = 1;
    public static int VERY_FAST = 400;
    public static int FAST = 200;
    public static int MEDIUM = 400;
    private static int mainFrameXPos = 200;
    private static int mainFrameYPos = 50;
    private static int reportFrameXPos = 10;
    private static int reportFrameYPos = 50;

    public SystemProperties()
    {
    }
    
    public static int getMainFrameXPos(){ return mainFrameXPos; }
    public static int getMainFrameYPos(){ return mainFrameYPos; }
    public static int getReportFrameXPos(){ return reportFrameXPos; }
    public static int getReportFrameYPos(){ return reportFrameYPos; }

    
}
