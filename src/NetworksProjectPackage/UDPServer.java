/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author This PC
 */
public class UDPServer extends Thread{
    private static final String USAGE_MESSAGE = "Usage: <program_name> <list_port>";
    private static final char[] PROTOCOL_HEADER = {'C','S','3','0','5','T','S','P'};
    private static final int MAJOR_VERSION_NUMBER = 1;
    private static final int MINOR_VERSION_NUMBER = 0;
    private static final byte TYPE_REQUEST = 1;
    private static final int TYPE_RESPONSE = 2;
    private static final int TYPE_ERROR = 3;

    private static final byte[] RESPONSE_PACKET = { (byte) PROTOCOL_HEADER[0], 
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

    private DatagramSocket listen_socket = null;

    /**
     * @param args
     */
    

    public UDPServer(int listen_port)
    {
        try {
            listen_socket = new DatagramSocket(listen_port);
        }
        catch(SocketException se)
        {
            System.err.println("Error: Could not open a server socket on port " + listen_port + ".\n" + se.getMessage());
            System.exit(1);
        }
    }

    public void run()
    {		
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
                sendErrorPacket(dp.getAddress(), dp.getPort(), "Incorrect packet length:"+dp.getLength());
                continue;
            }
            
            boolean error = false;
            String error_msg = null;

            for(int i = 0; i < PROTOCOL_HEADER.length; i++)
            {
                if(payload[i] != (byte) PROTOCOL_HEADER[i])
                {
                        error = true;
                        error_msg = "Invalid protocol header.";
                        break;
                }
            }

            if(error)
            {
                sendErrorPacket(dp.getAddress(), dp.getPort(), error_msg);
                continue;
            }

            int client_version_maj = (int)payload[8];
            int client_version_minor = (int)payload[9];

            if(client_version_maj != MAJOR_VERSION_NUMBER || client_version_minor != MINOR_VERSION_NUMBER)
            {
                sendErrorPacket(dp.getAddress(), dp.getPort(), "Version number mismatch(" + client_version_maj + "." + client_version_minor + ").");
                continue;
            }

            int packet_type = (int) ((payload[10] << 8) | payload[11]);

            if(packet_type != TYPE_REQUEST)
            {
                sendErrorPacket(dp.getAddress(), dp.getPort(), "Received non-request packet.");
                continue;
            }

            sendResponsePacket(dp.getAddress(), dp.getPort());
        }
    }

    private final void sendErrorPacket(InetAddress address, int port, String message)
    {
        try {
                final byte[] buffer = new byte[14+message.length()];
                for(int i = 0; i < PROTOCOL_HEADER.length; i++)
                        buffer[i] = (byte) PROTOCOL_HEADER[i];
                buffer[8] = (byte)MAJOR_VERSION_NUMBER;
                buffer[9] = (byte)MINOR_VERSION_NUMBER;
                buffer[10] = (byte)(TYPE_ERROR>>8);
                buffer[11] = (byte)(TYPE_ERROR);
                buffer[12] = (byte)(message.length()>>8);
                buffer[13] = (byte)message.length();
                int msg_len = message.length();
                for(int i = 0; i < msg_len; i++)
                        buffer[14+i] = (byte)message.charAt(i);
                this.listen_socket.send(new DatagramPacket(buffer, buffer.length, address, port));
        }
        catch(IOException ioe)
        {
                System.err.println("ERROR: Could not send error packet to " + address + ":" + port + ".");
        }
        displayErrorFromClient(address, port, message);
    }

    private final void sendResponsePacket(InetAddress address, int port)
    {
        byte[] buffer = new byte[22];
        System.arraycopy(RESPONSE_PACKET, 0, buffer, 0, 14);
        long time = System.currentTimeMillis();
        Date date = new Date(time);

        try {

                for(int i = 0; i < 8; i++)
                {
                        buffer[14+i] = (byte)((time>>((7-i)*8)) & 0xFF);
                }
                this.listen_socket.send(new DatagramPacket(buffer, 22, address, port));

        }
        catch(IOException ioe)
        {
                System.err.println("ERROR: Could not send response packet to " + address + ":" + port + ".");
        }
        System.err.println("INFO: Sent response packet to [" + address + ":" + port + "]:"+date);
    }
    
    public final void sendPacket(byte[] data, InetAddress address, int port)
    {
        byte[] buffer = new byte[22];
        System.arraycopy(RESPONSE_PACKET, 0, buffer, 0, 14);
        long time = System.currentTimeMillis();
        Date date = new Date(time);

        try {

                for(int i = 0; i < data.length; i++)
                {
                        buffer[14+i] = data[i];
                }
                this.listen_socket.send(new DatagramPacket(buffer, 14+data.length, address, port));

        }
        catch(IOException ioe)
        {
                System.err.println("ERROR: Could not send packet to " + address + ":" + port + ".");
        }
        System.err.println("INFO: Sent packet to [" + address + ":" + port + "]:"+date);
    }

    private final void displayErrorFromClient(InetAddress ip, int port, String message)
    {
        System.err.println("[" + ip + ":" + port + "] ERROR: " + message);
    }
    
    public static void main(String[] args) {
            if(args.length != 1)
            {
                System.err.println(USAGE_MESSAGE);
                System.out.println(args.length);
                for(int i = 0; i< args.length; i++)
                {
                    System.out.println(args[i]);
                }
                System.exit(1);
            }

            int port_num = -1;

            try {
                    port_num = Integer.parseInt(args[0]);
            }
            catch(NumberFormatException nfe)
            {
                    System.err.println("Error: Port number must be an integer above 1024 and below 65535.\n");
                    System.exit(1);
            }

            if(port_num < 1024 || port_num > 65535)
            {
                    System.err.println("Error: Port must be above 1024 and below 65535.\n");
                    System.exit(1);
            }

            UDPServer ts = new UDPServer(port_num);
            System.out.println("Time server started on port " + port_num + ".");
            ts.start();
    }
}
