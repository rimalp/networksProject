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

    //some test methods for simulation
    public void createTestPlayer(){
        PlayerData pd = new PlayerData(400, 400);
        try{
        players.put(InetAddress.getLocalHost(), pd);
        }catch(Exception e){
            System.out.println("Exception getting localhost");
        }
    }

    public void changePlayerData(){

        try{
            PlayerData pd = this.players.get(InetAddress.getLocalHost());
            pd.setBallX(pd.getBallX()-1);
            pd.setBallY(pd.getBallY()-1);
            pd.setPlayerX(pd.getPlayerX()+1);
            pd.setPlayerY(pd.getPlayerY()+1);
            this.players.put(InetAddress.getLocalHost(), pd);
        }catch(Exception e){
            System.out.println("Exception getting localhost");
        }

    }



    //getters and setters
    public void setPlayerData(InetAddress address, PlayerData newPlayerData)
    {
        this.players.put(address, newPlayerData);
    }
    
    public void setPlayerData(InetAddress address, int playerX, int playerY, int ballX, int ballY)
    {
        PlayerData newPlayerData = new PlayerData(playerX, playerY, ballX, ballY);
        this.players.put(address, newPlayerData);
    }
    
    public PlayerData getPlayerData(InetAddress address)
    {
        return this.players.get(address);
    }
    
    public HashMap<InetAddress, PlayerData> getAllPlayerData()
    {
        return this.players; 
    }
}
