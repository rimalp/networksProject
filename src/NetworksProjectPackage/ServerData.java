/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NetworksProjectPackage;

/**
 *
 * @author prabhat
 */
public class ServerData {
    public String ip;
    public int port;
    public String name;
    public int currentPlayers;
    public int maxPlayers;

    public ServerData(int _port, String _name, int _max){
        this.port = _port;
        this.name = _name;
        this.maxPlayers = _max;

        //update player count
        this.currentPlayers++;
    }


}
