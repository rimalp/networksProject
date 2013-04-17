/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

/**
 *
 * @author This PC
 */
public class ProtocolInfo {
    
    private final char[] PROTOCOL_HEADER = {'C','S','3','0','5','T','S','P'};
    private final int MAJOR_VERSION_NUMBER = 1;
    private final int MINOR_VERSION_NUMBER = 0;
    private final int TYPE_REQUEST = 1;
    private final int TYPE_RESPONSE = 2;
    private final int TYPE_ERROR = 3;
    private final int TYPE_UNICAST = 4;
    private final int TYPE_MULTICAST = 5;
    
    private final byte[] RESPONSE_PACKET = { (byte) PROTOCOL_HEADER[0], 
                                                    (byte) PROTOCOL_HEADER[1], 
                                                    (byte) PROTOCOL_HEADER[2],
                                                    (byte) PROTOCOL_HEADER[3], 
                                                    (byte) PROTOCOL_HEADER[4], 
                                                    (byte) PROTOCOL_HEADER[5], 
                                                    (byte) PROTOCOL_HEADER[6], 
                                                    (byte) PROTOCOL_HEADER[7],
                                                    MAJOR_VERSION_NUMBER, 
                                                    MINOR_VERSION_NUMBER, 
                                                    (byte)(TYPE_RESPONSE>>8), 
                                                    (byte)TYPE_RESPONSE, 	
                                                    0, 
                                                    8};
    
    public ProtocolInfo()
    {
        
    }
    
    public char[] getProtocolHeader()
    {
        return this.PROTOCOL_HEADER;
    }
    
    public char getProtocolHeaderElement(int i)
    {
        return this.PROTOCOL_HEADER[i];
    }
    
    public int getMajorVersionNumber()
    {
        return this.MAJOR_VERSION_NUMBER;
    }
    
    public int getMinorVersionNumber()
    {
        return this.MINOR_VERSION_NUMBER;
    }
    
    public int getTypeError()
    {
        return this.TYPE_ERROR;
    }
    
    public int getTypeReqeust()
    {
        return this.TYPE_REQUEST;
    }
    
    public int getTypeUnicast()
    {
        return this.TYPE_UNICAST;
    }
    
    
}
