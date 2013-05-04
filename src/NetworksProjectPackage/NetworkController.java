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

    private InetAddress sessionServerAddress = null;
    private InetAddress serverAddress = null;
    private InetAddress myIPAddress = null;

    private UDPClient udpClient = null;
    
    private HashMap<InetAddress,Integer> playersInfo = null;
    private RealTimeData realTimeData= null;
    private PlayerData myData = null;
    
    private boolean thisIsServer;
    private int clientPortNumber;
    
    public NetworkController(String _sessionServerIPAddress, int clientPortNumber, PlayerData initialPlayerData, GUITest _guiTest)
    {
        getMyIPAddress();
        
        playersInfo = new HashMap<InetAddress, Integer>();
        this.realTimeData = new RealTimeData();
        
        if(initialPlayerData == null)
        {
            System.out.println("Used Default Player Data");
            this.myData = PlayerData.DEFAULT_PLAYER_DATA;
        }else{
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
            this.clientPortNumber = ProtocolInfo.DEFAULT_CLIENT_LISTEN_PORT_NUMBER;
        }else
        {
            this.clientPortNumber = _portNumber;
        }
        this.udpClient = new UDPClient(this.clientPortNumber, this);
        this.udpClient.start();
    }
    
    public void addPlayer(InetAddress ip, Integer portNumber, PlayerData newPlayerData)
    {
        this.playersInfo.put(ip, portNumber);
        this.realTimeData.addNewPlayer(ip, newPlayerData);
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

    public InetAddress getSessionServerAddress()
    {
        return this.sessionServerAddress;
    }
    
    public InetAddress getMyIPAddress()
    {
        BufferedReader in = null;

        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
        
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            
            this.myIPAddress = InetAddress.getByName(ip);
        }catch(Exception e)
        {
            System.out.println(e);
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return this.myIPAddress;
    }
    
    
    
    public InetAddress getServerAddress()
    {
        return this.udpClient.getServerAddress();
    }
    
    public int getServerPortNumber()
    {
        return this.udpClient.getServerPortNumber();
    }
    
    public void setRealTimeData(RealTimeData newRealTimeData)
    {
        this.realTimeData = newRealTimeData;
    }
    
    public RealTimeData getRealTimeData()
    {
        return this.realTimeData;
    }
    
    public void updatePlayerData(InetAddress playerAddress, PlayerData playerData)
    {
        this.realTimeData.setPlayerData(playerAddress, playerData);
    }
    
    public boolean isThisServer()
    {
        return this.thisIsServer;
    }
    
    public void setThisToBeServer()
    {
        this.thisIsServer = true;
    }
    
    
    
    public void run()
    {
        while(true)
        {
            try{
                Thread.sleep(10);
                if(!this.thisIsServer)
                {
                    this.udpClient.sendPacket(this.getServerAddress(), this.getServerPortNumber(), this.realTimeData.getBytesForClient(this.myIPAddress), ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_INFO);
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
