/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.awt.Point;
import java.net.*;
import java.util.*;
import java.nio.*;
/**
 *
 * @author This PC
 */
public class PlayerData {
    //private InetAddress address = null;
    private int playerX;
    private int playerY;
    private int ballX;
    private int ballY;
    private int playerState;
    private int mousePressed; // 0 is not pressed, 1 is pressed

    //some more parameters for each player
    private int alive;
    private int team;
    private int exiting = Constants.NOT_EXITING;
    
    public static int SIZE_OF_BYTES_FOR_CLIENT = 8;
    public static int SIZE_OF_BYTES_FOR_SERVER = 16;
//    public static PlayerData DEFAULT_PLAYER_DATA= new PlayerData(300,300,450,450);
    
    //here are the variables for ball position logic
    final int RADIUS = 50;
    final int DELAY = 50;
    int frame_time = 20;
    
    double Vx = 0;
    double Vy = 0;
    double Ax = 0;
    double Ay = 0;
    double angle = 0;
    double dampingRatio = Math.pow(0.99, frame_time/4);
    final int MAXIMUM_WAIT_BEFORE_REVIVAL = 80;
    int timeToRevive = MAXIMUM_WAIT_BEFORE_REVIVAL;
    
    public PlayerData(int _playerX, int _playerY)
    {
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = this.playerX + 100;
        this.ballY = this.playerY + 100;
        this.alive = Constants.ALIVE;
        this.team = Constants.TEAM1;
        this.mousePressed = Constants.NOTPRESSED;
    }
    
    //constructor 2
    public PlayerData(int _playerX, int _playerY, int _ballX, int _ballY)
    {
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = _ballX;
        this.ballY = _ballY;
        this.alive = Constants.ALIVE;
        this.team = Constants.TEAM1;
        this.mousePressed = Constants.NOTPRESSED;
    }

    //constructor 3
    public PlayerData(int _playerX, int _playerY, int _ballX, int _ballY, int _alive, int _team)
    {
        //this.address = _address;
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = _ballX;
        this.ballY = _ballY;
        this.alive = _alive;
        this.team = _team;
        this.mousePressed = Constants.NOTPRESSED;
    }

    public PlayerData(int _team)
    {
        //this.address = _address;
        if(_team == Constants.TEAM1)
            this.playerX = 200;
        else if(_team == Constants.TEAM2)
            this.playerX = 700;
        this.playerY = 400;
        this.ballX = this.playerX + 100;
        this.ballY = this.playerY + 100;
        this.alive = Constants.ALIVE;
        this.team = _team;
        this.mousePressed = Constants.NOTPRESSED;
    }


    //see if this player's ball hits the other player itself
    public boolean hits(PlayerData other)
    {
        Point ballCenter = new Point (this.ballX, this.ballY);
        Point otherPlayerCenter = new Point(other.getPlayerX(), other.getPlayerY());

        if(ballCenter.distance(otherPlayerCenter) < 30){
            //kaboom
            return true;
        }else{
//            System.out.println(ballCenter.distance(otherPlayerCenter));
        }

        return false;
    }

    public int getPlayerX()
    {
        return this.playerX;
    }
    
    public int getPlayerY()
    {
        return this.playerY;
    }
    
    public int getBallX()
    {
        return this.ballX;
    }
    
    public int getBallY()
    {
        return this.ballY;
    }

    public void keepDying()
    {
        this.timeToRevive--;
        if(this.timeToRevive == 0)
        {
            this.alive = Constants.ALIVE;
            this.timeToRevive = MAXIMUM_WAIT_BEFORE_REVIVAL;
        }
    }
    
    public void setExiting(int status)
    {
        this.exiting = status;
    }

    public void setPlayerX(int x){
        this.playerX = x;
    }

    public void setPlayerY(int y){
        this.playerY = y;
    }

    public void setBallX(int x){
        this.ballX = x;
    }

    public void setBallY(int y){
        this.ballY = y;
    }

    public void setAlive(int state){
        if(state !=Constants.ALIVE && state != Constants.DEAD)
            return;
        
        this.alive = state;
    }

    public int isAlive(){
        return this.alive;
    }

    public int getTeam(){
        return this.team;
    }
    
    public int getMousePressed()
    {
        return this.mousePressed;
    }
    
    public void setMousePressed(int _mousePressed)
    {
        this.mousePressed = _mousePressed;
    }
    
    public int getExiting()
    {
        return this.exiting;
    }

    public byte[] getBytesForClient()
    {
        byte[] bytesToReturn = new byte[SIZE_OF_BYTES_FOR_CLIENT];
        
        bytesToReturn[0] = (byte)((this.playerX >> 8) & 0xFF);
        bytesToReturn[1] = (byte)((this.playerX) & 0xFF);
        bytesToReturn[2] = (byte)((this.playerY >> 8) & 0x000000FF);
        bytesToReturn[3] = (byte)((this.playerY) & 0x000000FF);
        bytesToReturn[4] = (byte)(this.mousePressed & 0x000000FF);

        return bytesToReturn;
    }
    
    public byte[] getBytesForServer()
    {
        byte[] bytesToReturn = new byte[SIZE_OF_BYTES_FOR_SERVER];
        
        bytesToReturn[0] = (byte)(this.playerX >> 8);
        bytesToReturn[1] = (byte)(this.playerX);
        bytesToReturn[2] = (byte)(this.playerY >> 8);
        bytesToReturn[3] = (byte)(this.playerY);
        bytesToReturn[4] = (byte)(this.ballX >> 8);
        bytesToReturn[5] = (byte)(this.ballX);
        bytesToReturn[6] = (byte)(this.ballY >> 8);
        bytesToReturn[7] = (byte)(this.ballY);
        bytesToReturn[8] = (byte)(this.team);
        bytesToReturn[9] = (byte)(this.alive);
        bytesToReturn[10] = (byte)(NetworkController.realTimeData.getScore(Constants.TEAM1));
        bytesToReturn[11] = (byte)(NetworkController.realTimeData.getScore(Constants.TEAM2));
        bytesToReturn[12] = (byte)(this.exiting);
        
        return bytesToReturn;
    }
    
    public boolean updateBasedOnBytesFromServer(byte[] newPlayerData)
    {        
        if(newPlayerData.length != SIZE_OF_BYTES_FOR_SERVER)
        {
            return false;
        }
        
        this.playerX = ((int)newPlayerData[0] & 0x000000FF) << 8 | ((int)newPlayerData[1] & 0x000000FF);
        this.playerY = ((int)newPlayerData[2] & 0x000000FF) << 8 | ((int)newPlayerData[3] & 0x000000FF);
        
        this.ballX = (((int)newPlayerData[4] & 0x000000FF) << 8) | ((int)newPlayerData[5] & 0x000000FF);
        this.ballY = (((int)newPlayerData[6] & 0x000000FF) << 8) | ((int)newPlayerData[7] & 0x000000FF);
        this.team = ((int)newPlayerData[8]);
        this.alive = ((int)newPlayerData[9]);
        NetworkController.realTimeData.setScore(Constants.TEAM1, ((int)newPlayerData[10]));
        NetworkController.realTimeData.setScore(Constants.TEAM2, ((int)newPlayerData[11]));
        this.exiting = ((int)(newPlayerData[12]));
        
        return true; 
    }
    
    public boolean getNextPlayerData(byte[] mousePositionUpdate)
    {
                
        if(mousePositionUpdate.length != SIZE_OF_BYTES_FOR_CLIENT)
        {
            return false;
        }
        
        this.playerX = ((int)mousePositionUpdate[0] & 0x000000FF) << 8 | ((int)mousePositionUpdate[1] & 0x000000FF);
        this.playerY = ((int)mousePositionUpdate[2] & 0x000000FF) << 8 | ((int)mousePositionUpdate[3] & 0x000000FF);
        
        if(this.team == Constants.TEAM2)
        {
            if(this.playerX >= ClientGUIController.minXForTEAM1 && this.playerX <= ClientGUIController.maxXForTEAM1)
            {
                this.team = Constants.TEAM1;
            }
        }else if(this.team == Constants.TEAM1)
        {
           if(this.playerX >= ClientGUIController.minXForTEAM2 && this.playerX <= ClientGUIController.maxXForTEAM2)
            {
                this.team = Constants.TEAM2;
            } 
        }
        
        this.mousePressed = (int)mousePositionUpdate[4];
        
        Ax = -0.001* frame_time * (this.ballX - this.playerX);
        Ay = 0.001 * frame_time * (this.playerY - this.ballY);
        
        Vx += Ax;
        
        if (this.mousePressed == 0)
        {
            
            Vx *= dampingRatio;
        }else
        {
            Vx *= 0.9999;
        }
        
        Vy += Ay; 
        //if (Vy > 0.001)
        Vy *= dampingRatio;
        
        
        if(this.ballX + Vx >= ClientGUIController.minXArena && this.ballX + Vx <= ClientGUIController.maxXArena)
        {
            this.ballX += Vx;
        }
        else if(this.ballX + Vx <= ClientGUIController.minXArena)
        {
            this.ballX = ClientGUIController.minXArena;
            Vx = 0;
        }else if(this.ballX + Vx >= ClientGUIController.maxXArena)
        {
            this.ballX = ClientGUIController.maxXArena;
            Vx = 0;
        }
        
        if(this.ballY + Vy >= ClientGUIController.minYArena && this.ballY + Vy <= ClientGUIController.maxYArena)
        {
            this.ballY += Vy;
        }else if(this.ballY <= ClientGUIController.minYArena)
        {
            this.ballY = ClientGUIController.minYArena;
            Vy = 0;
        }else if(this.ballY >= ClientGUIController.maxYArena)
        {
            this.ballY = ClientGUIController.maxYArena;
            Vy = 0;
        }
        
        if(this.alive == Constants.DEAD)
        {
            this.keepDying();
        }
        return true;
        
    }
    
    public int getIsAlive()
    {
        return this.alive;
    }
    
    public String toString()
    {
        String str = "";
        str += "Player X: " + this.playerX + "\n";
        str += "Player Y: " + this.playerY + "\n";
        str += "Ball X: " + this.ballX + "\n";
        str += "Ball Y: " + this.ballY + "\n";
        str += "Vx: " + this.Vx + "\n";
        str += "Vy: " + this.Vy + "\n";
        str += "Ax: " + this.Ax + "\n";
        str += "Ay: " + this.Ay + "\n";
        
        
        return str;
        
    }
    
    public boolean setTeam(int _team)
    {
        if(this.team != _team)
        {
            if(_team == Constants.TEAM1)
            {
                this.playerX = 200;
                this.playerY = 300;
            }else if(_team == Constants.TEAM2)
            {
                this.playerX = 700;
                this.playerY = 300;
            }
        }
        if(_team == Constants.TEAM1 || _team == Constants.TEAM2)
        {
            this.team = _team;
            return true;
        }else
        {
            return false;
        }
    }
    

}
