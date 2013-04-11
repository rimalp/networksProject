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
    
    private UDPServer udpServer;
    private Thread udpServerThread;
    private UDPClient udpClient;
    private HashMap<InetAddress,Integer> playersInfo = null;
    
    public NetworkManager()
    {
        
    }
    
    public void createServer()
    {
        this.udpServer = new UDPServer(4444);
        udpServerThread = new Thread(this.udpServer);
        udpServerThread.start();
    }
    
    public void addPlayer(InetAddress ip, Integer portNumber)
    {
        this.playersInfo.put(ip, portNumber);
    }
    
    public void sendMessage(InetAddress ip, byte[] message)
    {
        this.udpServer.sendPacket(message, ip, this.playersInfo.get(ip));
    }
    
    public void broadcastMessage(byte[] message)
    {
        Iterator it = this.playersInfo.entrySet().iterator();
        while (it.hasNext()) {
             Map.Entry playerInfo = (Map.Entry)it.next();
             this.sendMessage((InetAddress)playerInfo.getKey(), message);
             it.remove();
        } 
    }
}
