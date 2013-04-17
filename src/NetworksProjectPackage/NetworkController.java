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
public class NetworkController {
    
    private InetAddress sessionServerAddress = null;
    private InetAddress serverAddress = null;
    
    private UDPServer udpServer = null;
    private Thread udpServerThread = null;
    private UDPClient udpClient = null;
    private Thread udpClientThread = null;
    private HashMap<InetAddress,Integer> playersInfo = null;
    private boolean thisIsServer;
    
    private int clientPortNumber;
    
    public NetworkController(String _sessionServerIPAddress, int clientPortNumber)
    {
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
    
    /*
    public void createServer()
    {
        this.udpServer = new UDPServer(5555, this);
        this.thisIsServer = true;
        this.udpServer.start();
    }
    */
    
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
    
    public void clientSendMessage(InetAddress ip, byte[] message)
    {
        //this.udpClient.sendPacket(message, ip, this.playersInfo.get(ip));
    }
    
    public void serverSendMessage(InetAddress ip, byte[] message)
    {
        this.udpServer.sendPacket(ip, this.playersInfo.get(ip), new String(message));
    }
    
    public void broadcastMessage(byte[] message)
    {
        Iterator it = this.playersInfo.entrySet().iterator();
        while (it.hasNext()) {
             Map.Entry playerInfo = (Map.Entry)it.next();
             this.serverSendMessage((InetAddress)playerInfo.getKey(), message);
             it.remove();
        } 
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
        NetworkController networkController = new NetworkController(null,4000);
    }
}
