/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;


public class MainController extends Thread{// extends javax.swing.JFrame{
    RealTimeData realTimeData = null;
    NetworkController networkController = null;
    GUITest guiController = null;

    //variables for the ball physics
    final int RADIUS = 50;
    final int DELAY = 50;
    int centerX = 0;
    int centerY = 0;
    int orbitX;
    int orbitY;
    int frame_time = 5;
    Thread animator;
    double Vx = 0;
    double Vy = 0;
    double Ax = 0;
    double Ay = 0;

    //constructor
    public MainController()
    {
        realTimeData = new RealTimeData();
        networkController = new NetworkController(null, -1, null);
        this.guiController = new GUITest();
    }
    
    public RealTimeData getRealTimedata()
    {
        return this.realTimeData;
    }

    public static void main(String[] args)
    {
        MainController mainController = new MainController();
        
        mainController.start();
    }
    
    public void run()
    {
        //acquire new data and call the guicontroller's repaint function

        this.guiController.repaintAll(realTimeData);
        

        //then pause for a while
        try {
                Thread.sleep(1000);
        } catch (InterruptedException e) {
                System.out.println("MainController thread interrupted!");
        }


    }


}
