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
public class PlayerData {
    //private InetAddress address = null;
    private int playerX;
    private int playerY;
    private int ballX;
    private int ballY;
    
    public PlayerData(int _playerX, int _playerY)
    {
        //this.address = _address;
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = this.playerX + 10;
        this.ballY = this.playerY + 10;
    }
    
    public PlayerData(int _playerX, int _playerY, int _ballX, int _ballY)
    {
        //this.address = _address;
        this.playerX = _playerX;
        this.playerY = _playerY;
        this.ballX = _ballX;
        this.ballY = _ballY;
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
    
    //public InetAddress getAddress()
   // {
   //     return this.address;
   // }
}
