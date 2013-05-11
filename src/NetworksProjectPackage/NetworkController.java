/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * The network controller is in charge of network aspect of the game
 * @author Siyuan Wang
 */
public class NetworkController extends Thread{

    //all the IP address
    public static InetAddress sessionServerAddress = null;
    public static InetAddress serverAddress = null;
    public static InetAddress myIPAddress = null;
    
    //all the listening port number
    public static int sessionServerListenPortNumber;
    public static int serverListenPortNumber;
    public static int clientListenPortNumber;

    //UDP Client is NetworkController's minion in sending and receiving packet
    private UDPClient udpClient = null;
    
    //The following data are the only copy shared across all the controllers
    public static HashMap<InetAddress,Integer> playersInfo = null;
    public static RealTimeData realTimeData= null;
    public static PlayerData myData = null;
    
    //This variable is deprecated
    public static boolean thisIsServer;
    
    public MainController mainController = null;

    /**
     * Constructor
     * @param _sessionServerIPAddress
     * @param clientPortNumber
     * @param initialPlayerData
     * @param _mainController 
     */
    public NetworkController(String _sessionServerIPAddress, int clientPortNumber, PlayerData initialPlayerData, MainController _mainController)
    {
        this.mainController = _mainController;
        if(!getMyIPAddress())
        {
            System.out.println("Cannot get my external IP.");
            System.exit(1);
        }
        
        NetworkController.sessionServerListenPortNumber = ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER;
        NetworkController.serverListenPortNumber = ProtocolInfo.DEFAULT_SERVER_LISTEN_PORT_NUMBER;
        NetworkController.clientListenPortNumber = ProtocolInfo.DEFAULT_CLIENT_LISTEN_PORT_NUMBER;
        
        NetworkController.myData = new PlayerData(300,300,450,450);
        NetworkController.playersInfo = new HashMap<InetAddress, Integer>();
        NetworkController.realTimeData = new RealTimeData();
        NetworkController.realTimeData.addNewPlayer(myIPAddress, myData);
        NetworkController.serverAddress = NetworkController.myIPAddress;
        NetworkController.serverListenPortNumber = NetworkController.clientListenPortNumber;
        
        if(initialPlayerData != null)
        {
            this.myData = initialPlayerData;
        }
        
        try{
            
            if(_sessionServerIPAddress != null)
            {
                NetworkController.sessionServerAddress = InetAddress.getByName(_sessionServerIPAddress);
            }else
            {
                System.out.println("used my own IP as session server addresss");
                NetworkController.sessionServerAddress = NetworkController.myIPAddress;
            }
        }catch(UnknownHostException e)
        {
            System.out.println("Session Server Not Found");
            System.exit(1);
        }
        
        // I start off assuming I am not the server
        this.thisIsServer = true;
        
        // I will create a client on the specified port
        this.createClient(clientPortNumber);
        
    }
    
    
    /**
     * Instantiate my UDPClient
     * @param _portNumber listen port for this client machine
     */
    public void createClient(int _portNumber)
    {
        if(_portNumber < 1)
        {
            System.out.println("Used Default Client Listening Port at: "+ ProtocolInfo.DEFAULT_CLIENT_LISTEN_PORT_NUMBER);
        }else
        {
            NetworkController.clientListenPortNumber = _portNumber;
        }
        
        this.udpClient = new UDPClient(this);
        this.udpClient.start();
    }
    
    /**
     * add a player to RealTimeData
     * @param ip
     * @param portNumber
     * @param newPlayerData 
     */
    public void addPlayer(InetAddress ip, Integer portNumber, PlayerData newPlayerData)
    {

        this.playersInfo.put(ip, portNumber);
        if(newPlayerData == null)
        {
            newPlayerData = new PlayerData(300,300,450,450);
        }else
        {
            this.realTimeData.addNewPlayer(ip, newPlayerData);
        }
    }
    
    /**
     * This function is deprecated
     * @param newPlayerIP
     * @param newPlayerPortNum 
     */
    public void broadcastNewPlayerInfo(InetAddress newPlayerIP, int newPlayerPortNum)
    {
        byte[] bytesToBroadcast = new byte[6];
        byte[] ipBuffer = new byte[4];
        byte[] portNumBuffer = new byte[2];
        
        ipBuffer = newPlayerIP.getAddress();
        portNumBuffer[1] = (byte)(newPlayerPortNum << 8);
        portNumBuffer[0] = (byte)(newPlayerPortNum);
        
        for(int i = 0; i < 4; i++)
        {
            bytesToBroadcast[i] = ipBuffer[i];
        }
        
        for(int j = 0; j < 2; j++)
        {
            bytesToBroadcast[4 + j] = portNumBuffer[j];
        }
        
        for (InetAddress ipAddress : this.playersInfo.keySet()) {
             this.udpClient.sendPacket(ipAddress, this.playersInfo.get(ipAddress), bytesToBroadcast, ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO);
        }
    }

    /**
     * Request to retrieve a list of game servers
     * @return the list of game servers in ArrayList format
     */
    public ArrayList<String> requestGameServers(){
        byte[] toSend = new byte[2];
        toSend[0] = (byte)((NetworkController.clientListenPortNumber) >> 8);
        toSend[1] = (byte)(NetworkController.clientListenPortNumber);

        this.udpClient.sendPacket(NetworkController.sessionServerAddress, NetworkController.sessionServerListenPortNumber, toSend , ProtocolInfo.TYPE_UNICAST_JOINGAME);

        return null;
    }

    /**
     * broadcast the current RealTimeData to all the clients in the game
     */
    public void broadcastMessage()
    {
        for (InetAddress ipAddress : this.playersInfo.keySet()) {
             this.udpClient.sendPacket(ipAddress, this.playersInfo.get(ipAddress), this.realTimeData.getBytesForServer(), ProtocolInfo.TYPE_MULTICAST);
        }
        
        for (InetAddress ipAddress : this.playersInfo.keySet()) {
    
            if(NetworkController.realTimeData.getPlayerData(ipAddress) != null && NetworkController.playersInfo.get(ipAddress) != null && NetworkController.realTimeData.getPlayerData(ipAddress).getExiting() == Constants.EXITING)
            {
                NetworkController.realTimeData.removePlayer(ipAddress);
                NetworkController.playersInfo.remove(ipAddress);
                break;
            }
            
        }
        
        
    }
    
    /**
     * When the server receives a packet from any client, it will call this function to process the packet
     * @param bytesFromClient only the data section of the packet
     */
    public void processClientMessage(byte[] bytesFromClient)
    {
        this.realTimeData.updateBasedOnBytesFromClient(bytesFromClient);
    }
    
    /**
     * This function uses the amazon service to retrieve my external IP
     * @return 
     */
    public boolean getMyIPAddress()
    {
        BufferedReader in = null;

        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
        
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            
            this.myIPAddress = InetAddress.getByName(ip);
        }catch(Exception e)
        {
            return false;
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return true;
    }
    
    /**
     * This function will inform the MainController that the multicast has been received
     */
    public void multicastReceived()
    {
        this.mainController.multicastReceived();
    }

    
    /**
     * Retrieved an UDP Client
     * @return 
     */
    public UDPClient getUDPClient()
    {
        return this.udpClient;
    }

    /**
     * When the thread starts, send to the server about the new player information
     * Set the default team to be team2
     * 
     * Then unless the user decides to exit, you will be sending the server an player 
     * data update packet every 20 millisec
     */
    public void run()
    {        
        this.udpClient.sendPacket(NetworkController.serverAddress, NetworkController.serverListenPortNumber, this.udpClient.getBytesForNewPlayerInfo(), ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO);
        NetworkController.realTimeData.getPlayerData(myIPAddress).setTeam(Constants.TEAM2);
        while(NetworkController.myData != null && NetworkController.myData.getExiting() == Constants.NOT_EXITING)
        {
            try{
                Thread.sleep(20);
                this.udpClient.sendPacket(NetworkController.serverAddress, NetworkController.serverListenPortNumber, NetworkController.realTimeData.getBytesForClient(this.myIPAddress), ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_DATA);
            }catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
    
    /**
     * This function is only used in testing
     * @param args 
     */
    public static void main(String[] args)
    {
        try{
        
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        }catch(Exception e)
        {
            System.out.println(e);
        }
        
        SessionServer ss = null;
        
        Scanner sc = new Scanner(System.in);
        String session_ip = sc.nextLine();
        if(!session_ip.equals(""))
        {
            System.out.println("Started session server at " + session_ip);
            ss = new SessionServer(ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER, session_ip);
            ss.start();
        }
        
        String portNum = sc.nextLine();
        int portNumber = Integer.parseInt(portNum);
        NetworkController networkController = new NetworkController("139.147.73.212", portNumber, null, null);
        networkController.run();
    }
    
}
