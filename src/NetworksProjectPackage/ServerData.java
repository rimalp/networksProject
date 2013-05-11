/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NetworksProjectPackage;

/**
 * This class wraps everything the client needs to know about a game server
 * @author prabhat
 */
public class ServerData {
    public String ip;
    public int port;
    public String name;
    public int currentPlayers;
    public int maxPlayers;

    /**
     * constructor
     * 
     * @param _port
     * @param _name
     * @param _max 
     */
    public ServerData(int _port, String _name, int _max){
        this.port = _port;
        this.name = _name;
        this.maxPlayers = _max;

        //update player count
        this.currentPlayers++;
    }
    
    /**
     * add a new player
     * deprecated
     */
    public void addPlayer()
    {
        this.currentPlayers++;
    }
    
    /**
     * deprecated
     */
    public void exitPlayer()
    {
        this.currentPlayers--;
    }


}
