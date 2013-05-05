/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.net.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author This PC
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

    private UDPClient udpClient = null;
    
    public static HashMap<InetAddress,Integer> playersInfo = null;
    public static RealTimeData realTimeData= null;
    public static PlayerData myData = null;
    
    public static boolean thisIsServer;

    
    public NetworkController(String _sessionServerIPAddress, int clientPortNumber, PlayerData initialPlayerData, GUITest _guiTest)
    {
        
        if(!getMyIPAddress())
        {
            System.out.println("Cannot get my external IP.");
            System.exit(1);
        }
        
        NetworkController.sessionServerListenPortNumber = ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER;
        NetworkController.serverListenPortNumber = ProtocolInfo.DEFAULT_SERVER_LISTEN_PORT_NUMBER;
        NetworkController.clientListenPortNumber = ProtocolInfo.DEFAULT_CLIENT_LISTEN_PORT_NUMBER;
        
        NetworkController.myData = PlayerData.DEFAULT_PLAYER_DATA;
        NetworkController.playersInfo = new HashMap<InetAddress, Integer>();
        NetworkController.realTimeData = new RealTimeData();
        
        if(initialPlayerData != null)
        {
            this.myData = initialPlayerData;
        }
        
        try{
            
            if(_sessionServerIPAddress != null)
            {
                this.sessionServerAddress = InetAddress.getByName(_sessionServerIPAddress);
            }else
            {
                System.out.println("Session Server IP is not given.  Program exits");
                System.exit(1);
            }
        }catch(UnknownHostException e)
        {
            System.out.println("Session Server Not Found");
            System.exit(1);
        }
        
        // I start off assuming I am not the server
        this.thisIsServer = false;
        
        // I will create a client on the specified port
        this.createClient(clientPortNumber);
        
        // I will request the Server to be a server/request server address
        this.udpClient.sendRequestPacket();
    }

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
    
    public void addPlayer(InetAddress ip, Integer portNumber, PlayerData newPlayerData)
    {
        this.playersInfo.put(ip, portNumber);
        this.realTimeData.addNewPlayer(ip, newPlayerData);
        this.broadcastNewPlayerInfo(ip,portNumber);
    }
    
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
    
    public void broadcastMessage()
    {
        for (InetAddress ipAddress : this.playersInfo.keySet()) {
             this.udpClient.sendPacket(ipAddress, this.playersInfo.get(ipAddress), this.realTimeData.getBytesForServer(), ProtocolInfo.TYPE_MULTICAST);
        }
    }
    
    public void processClientMessage(byte[] bytesFromClient)
    {
        this.realTimeData.updateBasedOnBytesFromClient(bytesFromClient);
    }
    
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
    
    public void run()
    {
        while(true)
        {
            try{
                Thread.sleep(10);
                if(!this.thisIsServer)
                {
                    this.udpClient.sendPacket(NetworkController.serverAddress, NetworkController.serverListenPortNumber, NetworkController.realTimeData.getBytesForClient(this.myIPAddress), ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_DATA);
                }
            }catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
    
    public static void main(String[] args)
    {
        try{
        
       URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            System.out.println(ip);
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
        if(session_ip != "")
        {
            System.out.println("Started session server at " + session_ip);
            ss = new SessionServer(ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER, session_ip);
            ss.start();
        }else
        {
            System.out.println("no session server running");
        }
        
        
        NetworkController networkController = new NetworkController("139.147.37.17", 4444, null, null);
        networkController.run();
    }
    
}
