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
    private HashMap<InetAddress, PlayerData> playersData = null;
    
    public RealTimeData()
    {
        playersData = new HashMap<InetAddress, PlayerData>();
    }
    
    public RealTimeData(HashMap<InetAddress, PlayerData> newPlayersData)
    {
        this.playersData = newPlayersData;
    }

    //some test methods for simulation
    public void createTestPlayer(){
        PlayerData pd = new PlayerData(400, 400);
        try{
            this.playersData.put(InetAddress.getLocalHost(), pd);
        }catch(Exception e){
            //System.out.println("Exception getting localhost");
        }
    }

    public void changePlayerData(){

        try{
            PlayerData pd = this.playersData.get(InetAddress.getLocalHost());
            pd.setBallX(pd.getBallX()-1);
            pd.setBallY(pd.getBallY()-1);
            pd.setPlayerX(pd.getPlayerX()+1);
            pd.setPlayerY(pd.getPlayerY()+1);
            this.playersData.put(InetAddress.getLocalHost(), pd);
        }catch(Exception e){
            System.out.println("Exception getting localhost");
        }

    }

    public void setPlayerData(InetAddress address, PlayerData newPlayerData)
    {
        this.playersData.put(address, newPlayerData);
    }
    
    public void setPlayerData(InetAddress address, int playerX, int playerY, int ballX, int ballY)
    {
        PlayerData newPlayerData = new PlayerData(playerX, playerY, ballX, ballY);
        this.playersData.put(address, newPlayerData);
    }

    
    public PlayerData getPlayerData(InetAddress address)
    {
        return this.playersData.get(address);
    }

    public void addNewPlayer(InetAddress ip, PlayerData playerData){
        this.playersData.put(ip, playerData);
    }

    public HashMap<InetAddress, PlayerData> getAllPlayerData()
    {
        return this.playersData; 
    }
    
    public byte[] getBytesForClient()
    {
        int byteSizePerPlayer = 4 + PlayerData.SIZE_OF_BYTES_FOR_CLIENT;
        byte[] bytesToReturn = new byte[(this.playersData.size())*byteSizePerPlayer];
        byte[] ipAddressBuffer = null;
        byte[] playerDataBuffer = null;
        int index = 0;
        PlayerData playerData = null;
        
        for(InetAddress ipAddress : this.playersData.keySet())
        {
            
            ipAddressBuffer = ipAddress.getAddress();
            playerDataBuffer = this.playersData.get(ipAddress).getBytesForClient();
            
            for (int i = 0; i < 4; i++)
            {
                bytesToReturn[i + index*byteSizePerPlayer] = ipAddressBuffer[i];
            }
            
            for (int j = 0; j < PlayerData.SIZE_OF_BYTES_FOR_CLIENT; j++)
            {
                bytesToReturn[4 + j + index*byteSizePerPlayer] = playerDataBuffer[j];
            }
            
            index ++;
            
        }
        
        return bytesToReturn;
    }
    
    public byte[] getBytesForServer()
    {
        int byteSizePerPlayer = 4 + PlayerData.SIZE_OF_BYTES_FOR_SERVER;
        byte[] bytesToReturn = new byte[(this.playersData.size())*byteSizePerPlayer];
        byte[] ipAddressBuffer = null;
        byte[] playerDataBuffer = null;
        int index = 0;
        PlayerData playerData = null;
        
        for(InetAddress ipAddress : this.playersData.keySet())
        {
            
            ipAddressBuffer = ipAddress.getAddress();
            playerDataBuffer = this.playersData.get(ipAddress).getBytesForServer();
            
            for (int i = 0; i < 4; i++)
            {
                bytesToReturn[i + index*byteSizePerPlayer] = ipAddressBuffer[i];
            }
            
            for (int j = 0; j < PlayerData.SIZE_OF_BYTES_FOR_SERVER; j++)
            {
                bytesToReturn[4 + j + index*byteSizePerPlayer] = playerDataBuffer[j];
            }
            
            index ++;
            
        }
        
        return bytesToReturn;
    }
}
