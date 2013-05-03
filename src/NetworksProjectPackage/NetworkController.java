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
    private String myIPAddress = null;

    private UDPClient udpClient = null;
    
    private HashMap<InetAddress,Integer> playersInfo = null;
    private HashMap<InetAddress,PlayerData> playersData = null;
    private PlayerData myData = null;
    
    private boolean thisIsServer;
    private int clientPortNumber;
    
    public NetworkController(String _sessionServerIPAddress, int clientPortNumber, PlayerData initialPlayerData, GUITest _guiTest)
    {
        getMyIPAddress();
        
        playersInfo = new HashMap<InetAddress, Integer>();
        playersData = new HashMap<InetAddress, PlayerData>();
        
        if(initialPlayerData == null)
        {
            this.myData = new PlayerData(100,100,150,150);
        }else{
            this.myData = initialPlayerData;
        }
        
        try{
            if(_sessionServerIPAddress != null)
            {
                this.sessionServerAddress = InetAddress.getByName(_sessionServerIPAddress);
            }else
            {
                //if I am not specified with a session server address, I will run the session server
                this.sessionServerAddress = InetAddress.getByName(this.myIPAddress);
            }
        }catch(UnknownHostException e)
        {
            System.out.println("Cannot find Session Server");
            System.exit(1);
        }
        
        // I start off assuming I am not the server
        this.thisIsServer = false;
        
        // I will create a client on the specified port
        this.createClient(clientPortNumber);
        
        // I will request the Server to be a server/request server address
        this.udpClient.sendRequestPacket();
    }
    
    public void setRealTimeData()
    {
        //this.playersData = realTimeData.getAllPlayerData();
    }
    
    public RealTimeData getRealTimeData(RealTimeData realTimeData)
    {
        RealTimeData newRealTimedata = new RealTimeData(this.playersData);
        return newRealTimedata;
    }
    
    public byte[] getBytesFromAllPlayerData()
    {
        int sizeOfSinglePlayerData = 12;
        byte[] playerDataInBytes = new byte[this.playersData.size()*sizeOfSinglePlayerData];
        int index = 0;
        PlayerData playerData = null;
        byte[] ipAddress = new byte[4];
        
        for (InetAddress playerIPAddress : playersData.keySet())
        {
            ipAddress = playerIPAddress.getAddress();
            playerDataInBytes[0 + index*sizeOfSinglePlayerData] = ipAddress[0];
            playerDataInBytes[1 + index*sizeOfSinglePlayerData] = ipAddress[1];
            playerDataInBytes[2 + index*sizeOfSinglePlayerData] = ipAddress[2];
            playerDataInBytes[3 + index*sizeOfSinglePlayerData] = ipAddress[3];

            playerData = playersData.get(playerIPAddress);
            playerDataInBytes[4 + index*sizeOfSinglePlayerData] = (byte)(playerData.getPlayerX() >> 8);
            playerDataInBytes[5 + index*sizeOfSinglePlayerData] = (byte)(playerData.getPlayerX());
            playerDataInBytes[6 + index*sizeOfSinglePlayerData] = (byte)(playerData.getPlayerY() >> 8);
            playerDataInBytes[7 + index*sizeOfSinglePlayerData] = (byte)(playerData.getPlayerY());
            playerDataInBytes[8 + index*sizeOfSinglePlayerData] = (byte)(playerData.getBallX() >> 8);
            playerDataInBytes[9 + index*sizeOfSinglePlayerData] = (byte)(playerData.getBallX());
            playerDataInBytes[10 + index*sizeOfSinglePlayerData] = (byte)(playerData.getBallY() >> 8);
            playerDataInBytes[11 + index*sizeOfSinglePlayerData] = (byte)(playerData.getBallY());
            index ++;
        }
        return playerDataInBytes;
    }
    
    public byte[] getBytesFromPlayerData()
    {
        byte[] playerDataInBytes = new byte[5];
        
        playerDataInBytes[0] = (byte)(this.myData.getPlayerX()>>8);
        playerDataInBytes[1] = (byte)(this.myData.getPlayerX());
        playerDataInBytes[2] = (byte)(this.myData.getPlayerY()>>8);
        playerDataInBytes[3] = (byte)(this.myData.getPlayerY());
        playerDataInBytes[4] = (byte)(this.myData.getMousePressed() & 0x000000FF);

        return playerDataInBytes;
    }
    
    public PlayerData getPlayerDataFromBytes(byte[] dataInBytes)
    {
        PlayerData playerDataToReturn = null;
        if(dataInBytes.length == 8)
        {
            int playerX = (int)(dataInBytes[0]);
            playerX <<= 8;
            playerX &= 0x0000FF00;
            playerX |= (0x000000FF & ((int)(dataInBytes[1])));
            int playerY = (int)(dataInBytes[2]);
            playerY <<= 8;
            playerY &= 0x0000FF00;
            playerY |= (0x000000FF & ((int)(dataInBytes[3])));
            int ballX = (int)(dataInBytes[4]);
            ballX <<= 8;
            ballX &= 0x0000FF00;
            ballX |= (0x000000FF & ((int)(dataInBytes[5])));
            int ballY = (int)(dataInBytes[6]);
            ballY <<= 8;
            ballY &= 0x0000FF00;
            ballY |= (0x000000FF & ((int)(dataInBytes[7])));
            playerDataToReturn = new PlayerData(playerX, playerY, ballX, ballY);
        }
        return playerDataToReturn;
    }
    
    public void updatePlayerData(InetAddress playerAddress, PlayerData playerData)
    {
        this.playersData.put(playerAddress, playerData);
    }
    
    public void updateHashMap(HashMap<InetAddress, PlayerData> processedPlayerData){
        this.playersData = processedPlayerData;
    }


    public void createClient(int _portNumber)
    {
        if(_portNumber < 1)
        {
            this.clientPortNumber = ProtocolInfo.DEFAULT_CLIENT_LISTEN_PORT_NUMBER;
        }else
        {
            this.clientPortNumber = _portNumber;
        }
        this.udpClient = new UDPClient(this.clientPortNumber, this);
        this.udpClient.start();
    }
    
    public void addPlayer(InetAddress ip, Integer portNumber)
    {
        this.playersInfo.put(ip, portNumber);
    }
    
    public void broadcastMessage(byte[] message)
    {
        Iterator it = this.playersInfo.entrySet().iterator();
        while (it.hasNext()) {
             Map.Entry playerInfo = (Map.Entry)it.next();
             this.udpClient.sendPacket((InetAddress)playerInfo.getKey(), ((Integer)playerInfo.getValue()).intValue(), message, ProtocolInfo.TYPE_MULTICAST);
             //System.out.println("Send a packet!");
             //it.remove();
        }
        //System.out.println("Finished broadcasting");
    }
    
    public void broadcastMessage()
    {
        Iterator it = this.playersInfo.entrySet().iterator();
        while (it.hasNext()) {
             Map.Entry playerInfo = (Map.Entry)it.next();
             this.udpClient.sendPacket((InetAddress)playerInfo.getKey(), ((Integer)playerInfo.getValue()).intValue(), this.getBytesFromAllPlayerData(), ProtocolInfo.TYPE_MULTICAST);
             //System.out.println("Send a packet!");
             //it.remove();
        }
        //System.out.println("Finished broadcasting");
    }
    
    
    public void processClientMessage()
    {
        
    }

    public boolean isThisServer()
    {
        return this.thisIsServer;
    }
    
    public void setThisToBeServer()
    {
        //System.out.println("This station is now the server");
        this.thisIsServer = true;
    }
    
    public InetAddress getSessionServerAddress()
    {
        return this.sessionServerAddress;
    }
    public InetAddress getClientIPAddress()
    {
        return this.udpClient.getClientAddress();
    }
    
    public void getMyIPAddress()
    {
        BufferedReader in = null;

        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
        
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            this.myIPAddress = ip;
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
    
    public InetAddress getServerAddress()
    {
        return this.udpClient.getServerAddress();
    }
    
    public int getServerPortNumber()
    {
        return this.udpClient.getServerPortNumber();
    }
    
    public void run()
    {
        while(true)
        {
            try{
                Thread.sleep(10);
                if(this.thisIsServer)
                {
                    //this.broadcastMessage((new String("hahahahaha")).getBytes());
                }else{
                    //System.out.println("About to send packet to server at "+ this.getServerAddress() + " at port: "+this.getServerPortNumber());
                    //this.udpClient.sendPacket(InetAddress.getByName("127.0.0.1"), this.getServerPortNumber(), (new String("hahahahaha")).getBytes(), ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_INFO);
                     this.udpClient.sendPacket(this.getServerAddress(), this.getServerPortNumber(), this.getBytesFromPlayerData(), ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_INFO);
                     
                }
            }catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
    
}
