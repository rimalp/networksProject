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
    
    public PlayerData(int _playerX, int _playerY)
    {
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = this.playerX + 100;
        this.ballY = this.playerY + 100;
        this.alive = Constants.ALIVE;
    }
    
    //constructor 2
    public PlayerData(int _playerX, int _playerY, int _ballX, int _ballY)
    {
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = _ballX;
        this.ballY = _ballY;
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


    //public InetAddress getAddress()
   // {
   //     return this.address;
   // }
}
