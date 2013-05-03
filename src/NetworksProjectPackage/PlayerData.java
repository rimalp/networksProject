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

}
