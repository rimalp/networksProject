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
    SessionServer ss = null;
     private HashMap<String, Integer> activeGameServers;

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
//        ss = new SessionServer(ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER, "139.147.30.243");
//        ss.start();
        
        realTimeData = new RealTimeData();
        realTimeData.createTestPlayer();
        networkController = new NetworkController("139.147.30.243", 4444, PlayerData.DEFAULT_PLAYER_DATA, this, null);
        guiController = new ClientGUIController(this);
        guiController.drawMainMenu();
        activeGameServers = new HashMap<String, Integer>();
    }
    
    public void startNetworkController()
    {
        this.networkController.start();
        this.guiController.repaintAll(NetworkController.realTimeData);
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
        if(NetworkController.thisIsServer){
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

//        this.networkController.setRealTimeData(MainController.realTimeData);
    }

    public void addNewPlayer(InetAddress ip, int team){
        MainController.realTimeData.addNewPlayer(ip, new PlayerData(team));
    }

    public static void main(String[] args)
    {
        System.out.println("about to created main controller");
        MainController mainController = new MainController();
        System.out.println("main controller created");
        mainController.start();
        System.out.println("main controller started");
    }
    
    public void setPlayerData(InetAddress playerAddress, PlayerData playerData){
        MainController.realTimeData.setPlayerData(playerAddress, playerData);
        //update the GUI when the player data changes
        //guiController.repaintAll(realTimeData);
        
    }


    public void requestGameServers(){
        this.networkController.requestGameServers();
    }



    public void addHostServerInMainMenu(String ip, int port){
        if(this.activeGameServers == null)
            this.activeGameServers = new HashMap<String, Integer>();
        this.activeGameServers.put(ip, port);

        if(this.guiController == null)
            System.out.println("GuiController null");

        if(this.guiController.main_menu == null)
            System.out.println("mainmenu null");
        this.guiController.main_menu.updateServerListComboBox(ip);

    }
    
    public HashMap<String, Integer> getActiveGameServers()
    {
        return this.activeGameServers;
    }
    
    public void multicastReceived()
    {
        this.guiController.repaintAll(NetworkController.realTimeData);
    }
    
    public void run()
    {
        
        this.guiController.drawArena();

//        while(true)
//        {
            //acquire new data and call the guicontroller's repaint function
            
        //this.guiController.repaintAll(MainController.realTimeData);


            //then pause for a while
//            try {
//                    Thread.sleep(1000/60);
//            } catch (InterruptedException e) {
//                    System.out.println("MainController thread interrupted!");
//            }
//        }
    }


}
