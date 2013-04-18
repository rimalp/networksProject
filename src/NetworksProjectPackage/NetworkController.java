/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.net.*;
import java.util.*;
/**
 *
 * @author This PC
 */
public class NetworkController extends Thread{
    
    private InetAddress sessionServerAddress = null;
    private InetAddress serverAddress = null;

    private UDPClient udpClient = null;
    private HashMap<InetAddress,Integer> playersInfo = null;
    private HashMap<InetAddress,PlayerData> playersData = null;
    private PlayerData myData = null;
    
    private boolean thisIsServer;
    
    private int clientPortNumber;
    
    public NetworkController(String _sessionServerIPAddress, int clientPortNumber, PlayerData initialPlayerData)
    {
        playersInfo = new HashMap<InetAddress, Integer>();
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
                this.sessionServerAddress = InetAddress.getByName("localhost");
            }
        }catch(UnknownHostException e)
        {
            System.out.println("Cannot find Session Server");
            System.exit(1);
        }
        
        this.thisIsServer = false;
        this.createClient(clientPortNumber);
        this.udpClient.sendRequestPacket();
    }
    
    public byte[] getBytesFromPlayerData()
    {
        byte[] playerDataInBytes = new byte[8];
        playerDataInBytes[0] = (byte)(this.myData.getPlayerX()>>8);
        playerDataInBytes[1] = (byte)(this.myData.getPlayerX());
        playerDataInBytes[2] = (byte)(this.myData.getPlayerY()>>8);
        playerDataInBytes[3] = (byte)(this.myData.getPlayerY());
        playerDataInBytes[4] = (byte)(this.myData.getBallX()>>8);
        playerDataInBytes[5] = (byte)(this.myData.getBallX());
        playerDataInBytes[6] = (byte)(this.myData.getBallY()>>8);
        playerDataInBytes[7] = (byte)(this.myData.getBallY());
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
             System.out.println("Send a packet!");
             //it.remove();
        }
        System.out.println("Finished broadcasting");
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
        System.out.println("This station is now the server");
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
    
    public static void main(String[] args)
    {
        NetworkController networkController = new NetworkController(null,4000, null);
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
                Thread.sleep(1000);
                //this.broadcastMessage((new String("hahahahaha")).getBytes());
                
                //if(!this.getServerAddress().equals(this.getClientIPAddress()))
                //{
                    this.udpClient.sendPacket(this.getServerAddress(), this.getServerPortNumber(), this.getBytesFromPlayerData(), ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_INFO);
                    System.out.println("Data Sent to Server!");
                //}
            }catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
}
