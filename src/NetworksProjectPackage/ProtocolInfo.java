/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;

/**
 * This interface wraps all the constants regarding network protocol
 * @author Siyuan Wang
 */
interface ProtocolInfo {
    public final char[] PROTOCOL_HEADER = {'C','S','3','0','5','T','S','P'};
    public final int MAJOR_VERSION_NUMBER = 1;
    public final int MINOR_VERSION_NUMBER = 0;
    public final int TYPE_ERROR = 1;
    public final int TYPE_REQUEST = 2;
    public final int TYPE_UNICAST = 3;
    public final int TYPE_UNICAST_WITH_SERVER_INFO = 4;
    public final int TYPE_UNICAST_WITH_NEW_PLAYER_INFO = 5;
    public final int TYPE_UNICAST_WITH_PLAYER_DATA = 6;
    public final int TYPE_MULTICAST = 7;
    public final int TYPE_UNICAST_JOINGAME = 8;
    public final int TYPE_UNICAST_HOSTGAME = 9;
    public final int TYPE_UNICAST_EXITGAME = 10;

    public static final int DEFAULT_CLIENT_LISTEN_PORT_NUMBER = 4444;
    public static final int DEFAULT_SERVER_LISTEN_PORT_NUMBER = 5555;
    public static final int DEFAULT_SESSION_SERVER_PORT_NUMBER = 6666;
    public static final String DEFAULT_SESSION_SERVER_ADDRESS = "139.147.37.17";
    
    public final byte[] REQUEST_PACKET_WIHTOUT_LENGTH_DATA = { (byte) PROTOCOL_HEADER[0], 
                                                                (byte) PROTOCOL_HEADER[1], 
                                                                (byte) PROTOCOL_HEADER[2],
                                                                (byte) PROTOCOL_HEADER[3], 
                                                                (byte) PROTOCOL_HEADER[4], 
                                                                (byte) PROTOCOL_HEADER[5], 
                                                                (byte) PROTOCOL_HEADER[6], 
                                                                (byte) PROTOCOL_HEADER[7],
                                                                (byte)MAJOR_VERSION_NUMBER, 
                                                                (byte)MINOR_VERSION_NUMBER, 
                                                                (byte)(TYPE_REQUEST >> 8), 
                                                                (byte)TYPE_REQUEST};
    
    public final byte[] REQUEST_PACKET_WIHTOUT_TYPE_LENGTH_DATA = { (byte) PROTOCOL_HEADER[0], 
                                                                    (byte) PROTOCOL_HEADER[1], 
                                                                    (byte) PROTOCOL_HEADER[2],
                                                                    (byte) PROTOCOL_HEADER[3], 
                                                                    (byte) PROTOCOL_HEADER[4], 
                                                                    (byte) PROTOCOL_HEADER[5], 
                                                                    (byte) PROTOCOL_HEADER[6], 
                                                                    (byte) PROTOCOL_HEADER[7],
                                                                    (byte)MAJOR_VERSION_NUMBER, 
                                                                    (byte)MINOR_VERSION_NUMBER};
    
}
