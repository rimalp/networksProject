/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author This PC
 */
public class SessionServer extends Thread{
    private DatagramSocket listen_socket = null;
    private static final int DEFAULT_LISTEN_PORT = 6666;
    private int listen_port;
    private InetAddress ipAddress = null;
    private ProtocolInfo protocolInfo = null;
    private HashMap<InetAddress,Integer> sessionServers = null;
    
    public SessionServer(int _listen_port, String _ipAddress)
    {
        try {
            if(ipAddress != null)
            {
                ipAddress = InetAddress.getByName(_ipAddress);
            }else
            {
                ipAddress = InetAddress.getByName("localhost");
            }
            
            if(_listen_port != 0)
            {
                this.listen_port = _listen_port;
            }else
            {
                this.listen_port = DEFAULT_LISTEN_PORT;
            }
            this.listen_socket = new DatagramSocket(listen_port);
        }catch(SocketException se)
        {
            System.err.println("Error: Could not open a server socket on port " + listen_port + ".\n" + se.getMessage());
            System.exit(1);
        }catch(UnknownHostException e)
        {
            System.out.println("Error: could not open a server at ip:" + ipAddress + ".\r\n" + e.getMessage());
            System.exit(1);
        }
        
        this.protocolInfo = new ProtocolInfo();
        this.sessionServers = new HashMap<InetAddress, Integer>();
    }
    
    public void run()
    {
        System.out.println("Session server running at "+ ipAddress.toString() + " at port: "+this.listen_port +".\r\n");
        while(true)
        {
            byte[] buffer = new byte[65535];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                listen_socket.receive(dp);
            }
            catch(IOException ioe)
            {
                System.err.println("Warning: Could not receive datagram packet.");
            }

            byte[] payload = dp.getData();
            if(dp.getLength() < 14)
            {
                //sendErrorPacket(dp.getAddress(), dp.getPort(), "Incorrect packet length:"+dp.getLength());
                continue;
            }
            
            boolean error = false;
            String error_msg = null;

            for(int i = 0; i < this.protocolInfo.getProtocolHeader().length; i++)
            {
                if(payload[i] != (byte) this.protocolInfo.getProtocolHeaderElement(i))
                {
                        error = true;
                        error_msg = "Invalid protocol header.";
                        break;
                }
            }

            if(error)
            {
                //sendErrorPacket(dp.getAddress(), dp.getPort(), error_msg);
                System.out.println("Error");
                continue;
            }

            int client_version_maj = (int)payload[8];
            int client_version_minor = (int)payload[9];

            if(client_version_maj != this.protocolInfo.getMajorVersionNumber() || client_version_minor != this.protocolInfo.getMinorVersionNumber())
            {
                //sendErrorPacket(dp.getAddress(), dp.getPort(), "Version number mismatch(" + client_version_maj + "." + client_version_minor + ").");
                System.out.println("Error");
                continue;
            }

            int packet_type = (int) ((payload[10] << 8) | payload[11]);

            if(packet_type != this.protocolInfo.getTypeReqeust())
            {
                //sendErrorPacket(dp.getAddress(), dp.getPort(), "Received non-request packet.");
                System.out.println("Error");
                continue;
            }
            
            if(payload[12]!= 0 || payload[13] != 0)
            {
                System.out.println("Error");
            }
            
            System.out.println("Received Request");
            System.out.println(dp.getAddress());
            System.out.println(dp.getPort());
            this.sendPacket(dp.getAddress(), 4444, "Session Server is me!");
            //sendResponsePacket(dp.getAddress(), dp.getPort());
            //System.out.println(dp.getAddress().toString());
            //System.out.println(dp.getPort());
            //sendPacket(dp.getAddress(), dp.getPort(),"hahahahaha");
            
            //sendPacket(InetAddress.getByName("139.147.103.11"), 4445, "hahahalol");

        }
    }
    
    public void sendPacket(InetAddress address, int port, String message)
    {
        try {
                final byte[] buffer = new byte[14+message.length()];
                for(int i = 0; i < this.protocolInfo.getProtocolHeader().length; i++)
                        buffer[i] = (byte) this.protocolInfo.getProtocolHeaderElement(i);
                buffer[8] = (byte)this.protocolInfo.getMajorVersionNumber();
                buffer[9] = (byte)this.protocolInfo.getMinorVersionNumber();
                buffer[10] = (byte)(this.protocolInfo.getTypeUnicast()>>8);
                buffer[11] = (byte)(this.protocolInfo.getTypeUnicast());
                buffer[12] = (byte)(message.length()>>8);
                buffer[13] = (byte)message.length();
                int msg_len = message.length();
                for(int i = 0; i < msg_len; i++)
                        buffer[14+i] = (byte)message.charAt(i);
                this.listen_socket.send(new DatagramPacket(buffer, buffer.length, address, port));
        }
        catch(IOException ioe)
        {
                System.err.println("ERROR: Could not send packet to " + address + ":" + port + ".");
        }
    }
    
    
}
