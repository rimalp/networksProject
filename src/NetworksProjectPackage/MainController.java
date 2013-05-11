/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

/**
 * MainController is the highest in the controller hierarchy.  It controls the overall
 * game logic
 * @author Siyuan Wang
 */
public class MainController extends Thread{
    
    //This variable is deprecated
    public static RealTimeData realTimeData = null;
    
    //Its minion controllers
    NetworkController networkController = null;
    ClientGUIController guiController = null;
    
    // session server
    static SessionServer ss = null;
    
    //keeps track of all active game session
    private HashMap<InetAddress, Integer> activeGameServers;

    /**
     * Constructor
     * @param sessionIP string representation of session server ip if there is any. null if this station will serve
     * as session server
     */
    public MainController(String sessionIP)
    {
        
        realTimeData = new RealTimeData();
        realTimeData.createTestPlayer();
        PlayerData defaultPlayerData = new PlayerData(300,300,450,450);
        networkController = new NetworkController(sessionIP, 4444, defaultPlayerData, this);
        guiController = new ClientGUIController(this);
        guiController.drawMainMenu();
        activeGameServers = new HashMap<InetAddress, Integer>();
    }
    
    
    /**
     * Start the NetworkController threads
     */
    public void startNetworkController()
    {
        this.networkController.start();
        this.guiController.repaintAll(NetworkController.realTimeData, false);
    }
    
    /**
     * Deprecated function
     * @return 
     */
    public RealTimeData getRealTimedata()
    {
        return MainController.realTimeData;
    }

    /**
     * Deprecated function
     * @param rtd 
     */
    public void setRealTimeData(RealTimeData rtd){
        MainController.realTimeData = rtd;

        /* every time a new dataset is obtained, you process the data if you are
         * a server.
         */
        this.processRealTimeData();


    }

    /*
     * Deprecated function
     *
     */
    public void processRealTimeData(){
        //add any new player that might have entered the game (IS THIS NEEDED??)
        if(NetworkController.thisIsServer){
            HashMap<InetAddress, PlayerData> players = realTimeData.getAllPlayerData();
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

    /**
     * Add new player to a team
     * @param ip
     * @param team 
     */
    public void addNewPlayer(InetAddress ip, int team){
        realTimeData.addNewPlayer(ip, new PlayerData(team));
    }

    /**
     * This is the function that will execute when the jar file is executed
     * @param args nothing is needed
     */
    public static void main(String[] args)
    {
        boolean sessionServerIsMe = true;
        String sessionIP = "";
        MainController mainController = null;
        
        //try reading a local file for session server ip address
        try{
            BufferedReader br = new BufferedReader(new FileReader("SessionServerAddress.txt"));
            sessionIP = br.readLine();
            if(!sessionIP.equals("Me"))
            {
                NetworkController.sessionServerAddress = InetAddress.getByName(sessionIP);
                sessionServerIsMe = false;
            }
            br.close();
            
        }catch(Exception e)
        {
            System.out.println(e);
        }
                
        //pass in different parameters for MainController depending on whether or not this station is the 
        //session server as well
        if(sessionServerIsMe)
        {
            mainController = new MainController(null);
        }else
        {
            mainController = new MainController(sessionIP);
        }
        
        //start MainController thread
        mainController.start();
        
        //initiate and start running the session server if this is the session server
        if(sessionServerIsMe)
        {
            String myIP = NetworkController.myIPAddress.toString();
            ss = new SessionServer(ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER, myIP.substring(1, myIP.length()));
            ss.start();
        }
    }
    
    /**
     * Deprecated function
     * @param playerAddress
     * @param playerData 
     */
    public void setPlayerData(InetAddress playerAddress, PlayerData playerData){
        realTimeData.setPlayerData(playerAddress, playerData);
    }

    /**
     * Ask the networkController to request the list of all game servers
     */
    public void requestGameServers(){
        this.networkController.requestGameServers();
    }

    /**
     * add a host server into the main menu
     * @param ip
     * @param port 
     */
    public void addHostServerInMainMenu(InetAddress ip, int port){
        if(this.activeGameServers == null)
            this.activeGameServers = new HashMap<InetAddress, Integer>();
        this.activeGameServers.put(ip, port);
        
        this.guiController.main_menu.updateServerListComboBox(ip);

    }
    
    /**
     * accessor for active game servers
     * @return 
     */
    public HashMap<InetAddress, Integer> getActiveGameServers()
    {
        return this.activeGameServers;
    }
    
    //ask the GUIController to repaint everything whenever a broadcast is received
    public void multicastReceived()
    {
        this.guiController.repaintAll(NetworkController.realTimeData, true);
    }
    
    /**
     * This is the only thing that MainController thread does
     */
    public void run()
    {
        this.guiController.drawArena();
    }


}
