/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.awt.Point;
import java.net.*;
import java.util.*;

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
    
    public static int SIZE_OF_BYTES_FOR_CLIENT = 8;
    public static int SIZE_OF_BYTES_FOR_SERVER = 16;
    public static PlayerData DEFAULT_PLAYER_DATA= new PlayerData(100,100,150,150);
    
    //here are the variables for ball position logic
    final int RADIUS = 50;
    final int DELAY = 50;
    int frame_time = 33;
    double Vx = 0;
    double Vy = 0;
    double Ax = 0;
    double Ay = 0;
    double angle = 0;
    double dampingRatio = Math.pow(0.99, frame_time/5);
    
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
        Point ballCenter = new Point (this.ballX + 100, this.ballY + 100);
        Point otherPlayerCenter = new Point(other.getPlayerX()+100, other.getPlayerY()+100);

        if(ballCenter.distance(otherPlayerCenter) < 200){
            //kaboom
            return true;
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
        if(state !=Constants.ALIVE || state != Constants.DEAD)
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

    public byte[] getBytesForClient()
    {
        byte[] bytesToReturn = new byte[SIZE_OF_BYTES_FOR_CLIENT];
        
        bytesToReturn[0] = (byte)(this.playerX>>8);
        bytesToReturn[1] = (byte)(this.playerX);
        bytesToReturn[2] = (byte)(this.playerY>>8);
        bytesToReturn[3] = (byte)(this.playerY);
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
        
        return bytesToReturn;
    }
    
    public boolean updateBasedOnBytesFromServer(byte[] newPlayerData)
    {        
        if(newPlayerData.length != SIZE_OF_BYTES_FOR_SERVER)
        {
            return false;
        }
        
        //extract player information from the bytes passed in the parameter
        this.playerX = ((int)newPlayerData[0] << 8) | ((int)newPlayerData[1]);
        this.playerY = ((int)newPlayerData[2] << 8) | ((int)newPlayerData[3]);
        this.ballX = ((int)newPlayerData[4] << 8) | ((int)newPlayerData[5]);
        this.ballY = ((int)newPlayerData[6] << 8) | ((int)newPlayerData[7]);
        this.team = ((int)newPlayerData[8]);
        this.alive = ((int)newPlayerData[9]);
        
        return true; 
    }
    
    public boolean getNextPlayerData(byte[] mousePositionUpdate)
    {
        
        if(mousePositionUpdate.length != SIZE_OF_BYTES_FOR_CLIENT)
        {
            return false;
        }
        
        this.playerX = ((int)mousePositionUpdate[0]) << 8 | ((int)mousePositionUpdate[1]);
        this.playerY = ((int)mousePositionUpdate[2]) << 8 | ((int)mousePositionUpdate[3]);
        this.mousePressed = (int)mousePositionUpdate[4];
        
        Ax = -0.001* frame_time * (this.ballX - this.playerX);
        Ay = 0.001 * frame_time * (this.playerY - this.ballY);
        
        Vx += Ax;
        
        if (this.mousePressed == 0)
            Vx *= dampingRatio;
        else
            Vx *= 0.985;
        
        Vy += Ay; 
        //if (Vy > 0.001)
        Vy *= dampingRatio;
        
        this.playerX += Vx;
        this.playerY += Vy;
        
        return true;
        
    }
    
    

}
