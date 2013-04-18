/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

/**
 *
 * @author This PC
 */
public class MainController {
    RealTimeData realTimeData = null;
    NetworkController networkController = null;
    
    public MainController()
    {
        realTimeData = new RealTimeData();
        networkController = new NetworkController(null, -1, null);
    }
    
    public RealTimeData getRealTimedata()
    {
        return this.realTimeData;
    }
}
