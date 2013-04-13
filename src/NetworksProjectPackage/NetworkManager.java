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
public class NetworkManager {
    
    private UDPServer udpServer = null;
    private Thread udpServerThread = null;
    private UDPClient udpClient = null;
    private Thread udpClientThread = null;
    private HashMap<InetAddress,Integer> playersInfo = null;
    
    public NetworkManager()
    {
        
    }
    
    public void createServer()
    {
        this.udpServer = new UDPServer(4444, this);
        udpServerThread = new Thread(this.udpServer);
        udpServerThread.start();
    }
    
    public void createClient()
    {
        this.udpClient = new UDPClient(4444, this);
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
