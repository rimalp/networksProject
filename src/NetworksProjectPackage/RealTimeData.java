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
    private static final int BYTES_SIZE_PER_PLAYER_CLIENT =  4 + PlayerData.SIZE_OF_BYTES_FOR_CLIENT;
    private static final int BYTES_SIZE_PER_PLAYER_SERVER =  4 + PlayerData.SIZE_OF_BYTES_FOR_SERVER;

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
        if(newPlayerData == null)
        {
            this.playersData.put(address, PlayerData.DEFAULT_PLAYER_DATA);
        }else
        {
            this.playersData.put(address, newPlayerData);
        }
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
    public void printPlayersData()
    {
        System.out.println("*************************Start of Printing All Players' Data*************************");

        for(InetAddress ip : this.playersData.keySet())
        {
            System.out.println(ip.toString());
        }
        
        System.out.println("*************************End of Printing All Players' Data*************************");
    }
    public byte[] getBytesForClient(InetAddress clientIPAddress)
    {
        byte[] bytesToReturn = new byte[BYTES_SIZE_PER_PLAYER_CLIENT];
        byte[] ipAddressBuffer = new byte[4];
        byte[] playerDataBuffer = new byte[PlayerData.SIZE_OF_BYTES_FOR_CLIENT];
        
        ipAddressBuffer = clientIPAddress.getAddress();
        //printPlayersData();
        
        playerDataBuffer = this.playersData.get(clientIPAddress).getBytesForClient();

        for (int i = 0; i < 4; i++)
        {
            bytesToReturn[i] = ipAddressBuffer[i];
        }
            
        for (int j = 0; j < PlayerData.SIZE_OF_BYTES_FOR_CLIENT; j++)
        {
            bytesToReturn[4 + j] = playerDataBuffer[j];
        }
        
        return bytesToReturn;
    }
    
    public byte[] getBytesForServer()
    {
        byte[] bytesToReturn = new byte[(this.playersData.size())*BYTES_SIZE_PER_PLAYER_SERVER];
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
                bytesToReturn[i + index*BYTES_SIZE_PER_PLAYER_SERVER] = ipAddressBuffer[i];
            }
            
            for (int j = 0; j < PlayerData.SIZE_OF_BYTES_FOR_SERVER; j++)
            {
                bytesToReturn[4 + j + index*BYTES_SIZE_PER_PLAYER_SERVER] = playerDataBuffer[j];
            }
            
            index ++;
            
        }
        
        return bytesToReturn;
    }
    
    public boolean updateBasedOnBytesFromServer(byte[] bytesFromServer)
    {
        if(bytesFromServer.length != (this.playersData.size())*BYTES_SIZE_PER_PLAYER_SERVER)
        {
            return false;
        }
        
        byte[] ipBuffer = new byte[4];
        byte[] playerData = new byte[PlayerData.SIZE_OF_BYTES_FOR_SERVER];
        InetAddress tempIP = null;
        
        for(int i = 0; i < this.playersData.size(); i++)
        {
            for(int j = 0; j < 4; j++)
            {
                ipBuffer[j] = bytesFromServer[i*BYTES_SIZE_PER_PLAYER_SERVER + j];
            }
            
            try
            {
                tempIP = InetAddress.getByAddress(ipBuffer);
            }catch(Exception e)
            {
                return false;
            }
            
            for(int k = 0; k < PlayerData.SIZE_OF_BYTES_FOR_SERVER; k++)
            {
                playerData[k] = bytesFromServer[i*BYTES_SIZE_PER_PLAYER_SERVER + k + 4];
            }
            
            this.playersData.get(tempIP).updateBasedOnBytesFromServer(playerData);
        }
        return true;
    }
    
    public boolean updateBasedOnBytesFromClient(byte[] bytesFromClient)
    {
        if(bytesFromClient.length != (BYTES_SIZE_PER_PLAYER_CLIENT))
        {
            return false;
        }
        
        byte[] ipBuffer = new byte[4];
        byte[] playerData = new byte[PlayerData.SIZE_OF_BYTES_FOR_CLIENT];
        InetAddress tempIP = null;

        for(int i = 0; i < 4; i++)
        {
            ipBuffer[i] = bytesFromClient[i];
        }

        try
        {
            tempIP = InetAddress.getByAddress(ipBuffer);
        }catch(Exception e)
        {
            return false;
        }

        for(int j = 0; j < PlayerData.SIZE_OF_BYTES_FOR_CLIENT; j++)
        {
            playerData[j] = bytesFromClient[j + 4];
        }

        if(this.playersData.get(tempIP).getNextPlayerData(playerData))
        {
            return true;
        }
        
        return false;
    }
}
