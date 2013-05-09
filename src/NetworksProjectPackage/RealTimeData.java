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
    private int scoreForTeam1 = 0;
    private int scoreForTeam2 = 0;
    
    
    public RealTimeData()
    {
        playersData = new HashMap<InetAddress, PlayerData>();
    }
    
    public RealTimeData(HashMap<InetAddress, PlayerData> newPlayersData)
    {
        this.playersData = newPlayersData;
    }
    
    public void score(int team)
    {
        if(team == Constants.TEAM1)
        {
            this.scoreForTeam1++;
        }else if(team == Constants.TEAM2)
        {
            this.scoreForTeam2++;
        }
    }
    
    public int getScore(int team)
    {
        if(team == Constants.TEAM1)
        {
            return scoreForTeam1;
        }else if(team == Constants.TEAM2)
        {
            return scoreForTeam2;
        }
        return 0;
    }
    
    public void setScore(int team, int newScore)
    {
        if(team == Constants.TEAM1)
        {
            this.scoreForTeam1 = newScore;
        }else if(team == Constants.TEAM2)
        {
            this.scoreForTeam2 = newScore;
        }
    }
    
    public void resetScore()
    {
        this.scoreForTeam1 = 0;
        this.scoreForTeam2 = 0;
    }
    
    public int isGameOver()
    {
        if(this.scoreForTeam1 >= 11)
        {
            return Constants.TEAM1;
        }else if(this.scoreForTeam2 >= 11)
        {
            return Constants.TEAM2;
        }else{
            return Constants.INVALID_TEAM;
        }
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
            PlayerData defaultPlayerData = new PlayerData(300,300,450,450);
            this.playersData.put(address, defaultPlayerData);
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
    
    public void setPlayerData(InetAddress address, int playerX, int playerY, int isPressed)
    {
        PlayerData playerData = this.playersData.get(address);
        playerData.setPlayerX(playerX);
        playerData.setPlayerY(playerY);
        playerData.setMousePressed(isPressed);
        this.playersData.put(address, playerData);
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
    public String printPlayersData()
    {
        String str = "";
        
        str += "*************************Start of Printing All Players' Data*************************\n";

        for(InetAddress ip : this.playersData.keySet())
        {
            str += ip.toString() + "\n";
            str += this.playersData.get(ip).toString();
        }
        
        str += "*************************End of Printing All Players' Data*************************\n";
        
        return str;
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
        if((bytesFromServer.length) % RealTimeData.BYTES_SIZE_PER_PLAYER_SERVER != 0)
        {
            return false;
        }
        
        int currentNumOfPlayers = (bytesFromServer.length)/(RealTimeData.BYTES_SIZE_PER_PLAYER_SERVER);
        
        byte[] ipBuffer = new byte[4];
        byte[] playerData = new byte[PlayerData.SIZE_OF_BYTES_FOR_SERVER];
        InetAddress tempIP = null;
        
        for(int i = 0; i < currentNumOfPlayers; i++)
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
            
            if(this.playersData.get(tempIP) == null)
            {
                PlayerData defaultPlayerData = new PlayerData(300,300,450,450);
                this.playersData.put(tempIP, defaultPlayerData);
                this.playersData.get(tempIP).updateBasedOnBytesFromServer(playerData);
            }else
            {
                this.playersData.get(tempIP).updateBasedOnBytesFromServer(playerData);
            }
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
            //System.out.println("IP From Buffer" + tempIP);
        }catch(Exception e)
        {
            return false;
        }

        for(int j = 0; j < PlayerData.SIZE_OF_BYTES_FOR_CLIENT; j++)
        {
            playerData[j] = bytesFromClient[j + 4];
        }
        
        if(this.playersData.get(tempIP) == null)
        {
            PlayerData defaultPlayerData = new PlayerData(300,300,450,450);
            this.playersData.put(tempIP, defaultPlayerData);
        }
        
//        System.out.println("hahaahahahaah");
        if(this.playersData.get(tempIP).getNextPlayerData(playerData))
        {
            for(InetAddress playerAddress: NetworkController.realTimeData.getAllPlayerData().keySet())
            {
                for(InetAddress anotherPlayerAddress: NetworkController.realTimeData.getAllPlayerData().keySet())
                {
                    if(NetworkController.realTimeData.getAllPlayerData().get(playerAddress).isAlive() == Constants.ALIVE &&
                            NetworkController.realTimeData.getAllPlayerData().get(anotherPlayerAddress).isAlive() == Constants.ALIVE &&
                            !playerAddress.equals(anotherPlayerAddress) && 
                            NetworkController.realTimeData.getAllPlayerData().get(playerAddress).getTeam() != NetworkController.realTimeData.getAllPlayerData().get(anotherPlayerAddress).getTeam())
                    {
                        if(NetworkController.realTimeData.getAllPlayerData().get(playerAddress).hits(NetworkController.realTimeData.getAllPlayerData().get(anotherPlayerAddress)) && NetworkController.realTimeData.getAllPlayerData().get(anotherPlayerAddress).isAlive() == Constants.ALIVE)
                        {
                            NetworkController.realTimeData.getAllPlayerData().get(anotherPlayerAddress).setAlive(Constants.DEAD);
                            if(NetworkController.realTimeData.getAllPlayerData().get(playerAddress).getTeam() == Constants.TEAM1)
                            {
                                NetworkController.realTimeData.setScore(Constants.TEAM1, NetworkController.realTimeData.getScore(Constants.TEAM1)+1);
                            }else if(NetworkController.realTimeData.getAllPlayerData().get(playerAddress).getTeam() == Constants.TEAM2)
                            {
                                NetworkController.realTimeData.setScore(Constants.TEAM2, NetworkController.realTimeData.getScore(Constants.TEAM2)+1);
                            }
                        }
                    }
                }
            }
            return true;
        }
        
        return false;
    }
    
    public boolean isEveryOneDead(int team)
    {
        boolean everyoneIsDead = true;

        for(InetAddress playerAddress : this.playersData.keySet())
        {
            if(this.playersData.get(playerAddress).getTeam() == team && this.playersData.get(playerAddress).getIsAlive() == Constants.ALIVE)
            {
                everyoneIsDead = false;
            }
        }
        
        return everyoneIsDead;
    }
    
    public boolean setTeam(InetAddress inetAddress, int team)
    {
        return this.playersData.get(inetAddress).setTeam(team);
    }
    
    public void removePlayer(InetAddress ip)
    {
        this.playersData.remove(ip);
    }
}
