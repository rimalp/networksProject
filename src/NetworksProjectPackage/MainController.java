/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

import java.net.InetAddress;
import java.util.*;


public class MainController extends Thread{// extends javax.swing.JFrame{
    public static RealTimeData realTimeData = null;
    NetworkController networkController = null;
    ClientGUIController guiController = null;

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
        realTimeData.createTestPlayer();
        networkController = new NetworkController(null, -1, null, null);
        guiController = new ClientGUIController();
        guiController.drawMainMenu();
    }
    
    public RealTimeData getRealTimedata()
    {
        return MainController.realTimeData;
    }

    public void setRealTimeData(RealTimeData rtd){
        MainController.realTimeData = rtd;

        /* every time a new dataset is obtained, you process the data if you are
         * a server.
         */
        this.processRealTimeData();


    }

    /*
     * This method processes the playerdata to see
     *
     */
    public void processRealTimeData(){
        //add any new player that might have entered the game (IS THIS NEEDED??)
        if(this.networkController.isThisServer()){
            HashMap<InetAddress, PlayerData> players = MainController.realTimeData.getAllPlayerData();
            //see if any of the balls touch any of the players
            ArrayList<InetAddress> toRemove = new ArrayList<InetAddress>();
            for(InetAddress ip1: players.keySet()){
                for(InetAddress ip2: players.keySet()){

                    if(ip1.equals(ip2)) continue; //for itself
                    //if anyone else's ball hits me, I'm dead
                    if(players.get(ip2).getTeam() != players.get(ip1).getTeam()
                       &&  (players.get(ip2).hits(players.get(ip1)))){
                       //ip1 got hit. remove it from the list
                       toRemove.add(ip2);
                        //                        players.get(ip1).setAlive(Constants.DEAD);
                        break;
                    }
                }
            }
            //remove the dead from the list of players
            for(InetAddress deadIP: toRemove){
                players.remove(deadIP);
            }
       }

        //need to change the realtimedta of the networkmanager class so that it broadcasts the updates.
        this.networkController.updateHashMap(MainController.realTimeData.getAllPlayerData());
    }

    public void addNewPlayer(InetAddress ip, int team){
        MainController.realTimeData.addNewPlayer(ip, new PlayerData(team));
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
        this.guiController.drawArena();
        

        //then pause for a while
        try {
                Thread.sleep(1000);
        } catch (InterruptedException e) {
                System.out.println("MainController thread interrupted!");
        }


    }


}
