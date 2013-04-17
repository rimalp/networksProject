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
    
    private UDPServer udpServer = null;
    private Thread udpServerThread = null;
    private UDPClient udpClient = null;
    private Thread udpClientThread = null;
    private HashMap<InetAddress,Integer> playersInfo = null;
    
    public NetworkController()
    {
        this.createClient();
        this.udpClient.sendRequestPacket();
    }
    
    public void createServer()
    {
        this.udpServer = new UDPServer(4444, this);
        this.udpServer.start();
        //this.udpServerThread = new Thread(this.udpServer);
        //this.udpServerThread.start();
    }
    
    public void createClient()
    {
        this.udpClient = new UDPClient(4444, this);
        this.udpClient.start();
        //this.udpClientThread = new Thread(this.udpClient);
        //this.udpClientThread.start();
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
    
    public void processServerMessage(InetAddress ip, int portNum, String msg)
    {
        
    }
}
