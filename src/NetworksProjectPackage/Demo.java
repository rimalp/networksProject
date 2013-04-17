/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

/**
 *
 * @author This PC
 */
public class Demo {
    
    private static SessionServer sessionServer = null;
    private static NetworkController networkController = null;
    public static void main(String[] args) throws Exception  
    {
        sessionServer = new SessionServer(6666, null);
        sessionServer.start();
        networkController = new NetworkController(null, -1);
    }
}
