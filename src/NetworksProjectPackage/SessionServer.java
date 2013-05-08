/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksProjectPackage;
import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

/**
 *
 * @author This PC
 */
public class SessionServer extends Thread{
    private DatagramSocket listen_socket = null;
    private int listen_port;
    private InetAddress ipAddress = null;
    private ProtocolInfo protocolInfo = null;
    private HashMap<InetAddress,ServerData> servers = null;

    private int request_type;
    
    public SessionServer(int _listen_port, String _ipAddress)
    {
        try {
            if(_ipAddress != null)
            {
                ipAddress = InetAddress.getByName(_ipAddress);
            }else
            {
                ipAddress = InetAddress.getByName(ProtocolInfo.DEFAULT_SESSION_SERVER_ADDRESS);
            }
            
            if(_listen_port != 0)
            {
                this.listen_port = _listen_port;
            }else
            {
                this.listen_port = ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER;
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
        
        //this.protocolInfo = new ProtocolInfo();
        this.servers = new HashMap<InetAddress, ServerData>();

        //test data
        ServerData newServer1 = new ServerData(4444, "", Constants.MAX_PLAYERS);
        ServerData newServer2 = new ServerData(4444, "", Constants.MAX_PLAYERS);
        String ip1 = "127.188.5.6";
        String ip2 = "127.188.5.7";

        try{
        this.servers.put(InetAddress.getByName(ip1), newServer1);
        this.servers.put(InetAddress.getByName(ip2), newServer2);
        }catch(Exception e){
            System.out.println("Exception in testing data in sessionserver");
        }

    }
    


    public int processRequest(byte[] payload)
    {
        boolean error = false;
        String error_msg = null;

        for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
        {
            if(payload[i] != (byte)ProtocolInfo.PROTOCOL_HEADER[i])
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
            return -1;
        }

        int client_version_maj = (int)payload[8];
        int client_version_minor = (int)payload[9];

        if(client_version_maj != ProtocolInfo.MAJOR_VERSION_NUMBER || client_version_minor != ProtocolInfo.MINOR_VERSION_NUMBER)
        {
            //sendErrorPacket(dp.getAddress(), dp.getPort(), "Version number mismatch(" + client_version_maj + "." + client_version_minor + ").");
            System.out.println("Error");
            return -1;
        }

        int packet_type = (int) ((payload[10] << 8) | payload[11]);

        this.request_type = packet_type;

        if(packet_type != ProtocolInfo.TYPE_REQUEST && packet_type != ProtocolInfo.TYPE_UNICAST_JOINGAME)
        {
            //sendErrorPacket(dp.getAddress(), dp.getPort(), "Received non-request packet.");
            System.out.println("adfsError: " + packet_type);
            return -1;
        }

        if(payload[12]!= 0 || payload[13] != 2)
        {
            System.out.println("Error");
        }




        System.out.println("Received Request");
        int clientPort = (int)payload[14];
        //System.out.println(clientPort);
        clientPort <<= 8;
        //System.out.println(clientPort);
        int temp = (int)(payload[15]);
        temp &= 0x000000FF;
        clientPort |=temp;

        //System.out.println(clientPort);
        return 4444;
    }
    
    public void run()
    {
        System.out.println("Session server running at "+ ipAddress.toString() + " at port: "+this.listen_port +".");
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

            System.out.println("Received a request!");
            
            if(dp.getLength() < 14)
            {
                //sendErrorPacket(dp.getAddress(), dp.getPort(), "Incorrect packet length:"+dp.getLength());
                continue;
            }
            byte[] payload = new byte[dp.getLength()];
            System.arraycopy(dp.getData(), 0, payload, 0, dp.getLength());
            
  
            int clientListenPort = this.processRequest(payload);
            
            if(clientListenPort == -1)
            {
                continue;
            }
            System.out.println("Client Port number:"+clientListenPort);

            if(this.servers.isEmpty())
            {
                //create a new serverdata wrapper class
                ServerData newServer = new ServerData(clientListenPort, "", Constants.MAX_PLAYERS);

                this.sendPacket(dp.getAddress(), clientListenPort, "No server's running.");
                this.servers.put(dp.getAddress(), newServer);
            }

            else if(this.request_type == ProtocolInfo.TYPE_UNICAST_JOINGAME)
            {
                for(InetAddress serverIps: this.servers.keySet()){
                    System.out.println("Serverdata.tostrint(): " + serverIps.toString());
                    this.sendPacketWithServerInformation(dp.getAddress(), clientListenPort, serverIps, this.servers.get(serverIps).port, ProtocolInfo.TYPE_UNICAST_JOINGAME );
                    System.out.println("Server Info Sent");

                }

//                Iterator it = this.servers.entrySet().iterator();
//                Map.Entry server = (Map.Entry)it.next();
//                this.sendPacketWithServerInformation(dp.getAddress(), clientListenPort, ((InetAddress)(server.getKey())).toString().substring(1), ((Integer)(server.getValue())).intValue());
//                //this.sendPacketWithClientInformation((InetAddress)server.getKey(), (int)servers.get((InetAddress)server.getKey()), dp.getAddress().toString().substring(1), clientListenPort);
//                System.out.println("Server Info Sent");
            }
        }
    }



    public void sendPacketWithServerInformation(InetAddress address, int port, InetAddress ipAddress, int portNum, int type)
    {
        try {
                final byte[] buffer = new byte[20];
                byte[] ipBuffer = new byte[4];
                if(type != ProtocolInfo.TYPE_UNICAST_JOINGAME)
                    ipBuffer = address.getAddress();
                else{
                    ipBuffer = ipAddress.getAddress();
                }

                for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
                        buffer[i] = (byte)ProtocolInfo.PROTOCOL_HEADER[i];
                buffer[8] = (byte)ProtocolInfo.MAJOR_VERSION_NUMBER;
                buffer[9] = (byte)ProtocolInfo.MINOR_VERSION_NUMBER;
                buffer[10] = (byte)(type>>8);
                buffer[11] = (byte)(type);
                
                int length = 6;
                
                buffer[12] = (byte)(length >>8);
                buffer[13] = (byte)(length);
                
//                int msg_len = ipAddress.length();

                for(int i = 0; i < 4; i++)
                        buffer[14+i] = ipBuffer[i];
                
                buffer[18] = (byte)((portNum & 0x0000FF00)>> 8);
                buffer[19] = (byte)((portNum & 0x000000FF));
                
                this.listen_socket.send(new DatagramPacket(buffer, buffer.length, address, port));
        }
        catch(IOException ioe)
        {
                System.err.println("ERROR: Could not send packet to " + address + ":" + port + ".");
        }
    }
    
    public void sendPacketWithClientInformation(InetAddress address, int port, String ipAddress, int portNum)
    {
        try {
                final byte[] buffer = new byte[16+ipAddress.length()];
                for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
                        buffer[i] = (byte)ProtocolInfo.PROTOCOL_HEADER[i];
                buffer[8] = (byte)ProtocolInfo.MAJOR_VERSION_NUMBER;
                buffer[9] = (byte)ProtocolInfo.MINOR_VERSION_NUMBER;
                buffer[10] = (byte)(ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO>>8);
                buffer[11] = (byte)(ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO);
                buffer[12] = (byte)((ipAddress.length()+2)>>8);
                buffer[13] = (byte)(ipAddress.length()+2);
                int msg_len = ipAddress.length();
                for(int i = 0; i < msg_len; i++)
                        buffer[14+i] = (byte)ipAddress.charAt(i);
                buffer[buffer.length-2] = (byte)((portNum & 0x0000FF00)>> 8);
                buffer[buffer.length-1] = (byte)((portNum & 0x000000FF));
                this.listen_socket.send(new DatagramPacket(buffer, buffer.length, address, port));
        }
        catch(IOException ioe)
        {
                System.err.println("ERROR: Could not send packet to " + address + ":" + port + ".");
        }
    }
    
    public void sendPacket(InetAddress address, int port, String message)
    {
        try {
                final byte[] buffer = new byte[14+message.length()];
                for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
                        buffer[i] = (byte)ProtocolInfo.PROTOCOL_HEADER[i];
                buffer[8] = (byte)ProtocolInfo.MAJOR_VERSION_NUMBER;
                buffer[9] = (byte)ProtocolInfo.MINOR_VERSION_NUMBER;
                buffer[10] = (byte)(ProtocolInfo.TYPE_UNICAST>>8);
                buffer[11] = (byte)(ProtocolInfo.TYPE_UNICAST);
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
