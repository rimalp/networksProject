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
public class RealTimeData {
    private HashMap<InetAddress, PlayerData> players = null;
    
    public RealTimeData()
    {
        players = new HashMap<InetAddress, PlayerData>();
    }
    
    public void setPlayerData(InetAddress address, PlayerData newPlayerData)
    {
        this.players.put(address, newPlayerData);
    }
    
    public void setPlayerData(InetAddress address, int playerX, int playerY, int ballX, int ballY)
    {
        PlayerData newPlayerData = new PlayerData(address, playerX, playerY, ballX, ballY);
        this.players.put(address, newPlayerData);
    }
    
    public PlayerData getPlayerData(InetAddress address)
    {
        return this.players.get(address);
    }
}
