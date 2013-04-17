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
public class UDPClient extends Thread{
    
    /* This is the object that creats the UDPClient instance */
    private NetworkController networkController = null;
    
    /* This will be the socket this client will be sending packet from */
    private DatagramSocket clientSocket = null;
    private InetAddress myIPAddress = null;
    private InetAddress myServerIPAddress = null;
    private InetAddress sessionServerIPAddress = null;
    
    private int myListenPort;
    private int myServerListenPort;
    private int sessionServerListenPort;

    public byte[] dataFromServer;
    private DatagramSocket listen_socket = null;

    public UDPClient(int _myListenPort, NetworkController _networkController)
    {
        this.networkController = _networkController;
        
        if(_myListenPort < 1)
        {
            this.myListenPort = ProtocolInfo.DEFAULT_CLIENT_LISTEN_PORT_NUMBER;
        }else
        {
            this.myListenPort = _myListenPort;
        }
        
        
        try {
            listen_socket = new DatagramSocket(this.myListenPort);
        }
        catch(SocketException se)
        {
            System.err.println("Error: Could not open a server socket on port " + this.myListenPort + ".\n" + se.getMessage());
            System.exit(1);
        }
        
        try
        {
            clientSocket = new DatagramSocket();  
        }catch(SocketException e)
        {
            System.out.println("The IP address or the port number for the server is not valid");
            System.exit(1);
        }
        
        this.sessionServerIPAddress = this.networkController.getSessionServerAddress();
        this.sessionServerListenPort = ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER;
    }
    
    public InetAddress getClientAddress()
    {
        return this.myIPAddress;
    }
    
    //send the request packet
    public void sendRequestPacket()
    {
        //create a buffer that stores all the byte to send
        byte[] data_to_send = new byte[16];
        System.arraycopy(ProtocolInfo.REQUEST_PACKET_WIHTOUT_LENGTH_DATA, 0, data_to_send, 0, 12);
        data_to_send[13] = 0x02;
        System.out.println("My Listening port is:" + this.myListenPort);
        data_to_send[14] = (byte)((this.myListenPort >> 8) & 0x000000FF);
        System.out.println((int)data_to_send[14]);
        data_to_send[15] = (byte)(this.myListenPort & 0x000000FF);
        System.out.println((int)data_to_send[15]);
        
        //construct a DatagramPacket
        DatagramPacket sendPacket =  new DatagramPacket(    data_to_send, 
                                                            data_to_send.length, 
                                                            this.sessionServerIPAddress, 
                                                            this.sessionServerListenPort);
        
        //Try sending the packet. Let the user know when an exception has occurred.
        try{
            clientSocket.send(sendPacket);  
        }catch(IOException e)
        {
            System.out.println("IO Exception has occured while sending the request packet");
        }
        System.out.println("Request Packet Sent");
    } 
    
    //process the packet received from the server
    public String processPacketFromServer()
    {
        //verify the header of the packet
        for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
        {
            if(dataFromServer[i] != (byte) ProtocolInfo.PROTOCOL_HEADER[i])
            {
                System.out.println("Invalid Header");
            }
        }
        
        //verify the version of the packet
        int server_version_major = (int)dataFromServer[8];
        int server_version_minor = (int)dataFromServer[9];
        
        if( server_version_major != ProtocolInfo.MAJOR_VERSION_NUMBER || 
            server_version_minor != ProtocolInfo.MINOR_VERSION_NUMBER)
        {
            System.out.println("Invalid version number");
        }
        
        //verify the type of the packet
        int packet_type = (int) ((dataFromServer[10] << 8) | dataFromServer[11]);
        
        
        switch(packet_type)
        {
            case ProtocolInfo.TYPE_REQUEST:break;
            case ProtocolInfo.TYPE_ERROR:
                this.processErrorPacket();
                break;
            case ProtocolInfo.TYPE_UNICAST:
                if(dataFromServer.length > 14)
                {
                    return processPacket();
                }
                break;
            case ProtocolInfo.TYPE_UNICAST_WITH_SERVER_INFO:
                if(dataFromServer.length > 14)
                {
                    return processPacketWithServerInformation();
                }
                break;
            case ProtocolInfo.TYPE_MULTICAST:break;
            default: 
                System.out.println("Unknown Packet Type");
        }
        return "";
    }
    
    public String processPacket()
    {
        byte[] msgBuffer = new byte[dataFromServer.length-14];
        System.out.println("I received "+ (dataFromServer.length-14) +" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-14);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
      	
      	//print the error message to the output.
        System.out.println("[I Received Packet]:"+msg);
        return msg;
    }
    
    public String processPacketWithServerInformation()
    {
        this.myServerListenPort = 0;
        this.myServerListenPort += (dataFromServer[dataFromServer.length-2] << 8);
        this.myServerListenPort += dataFromServer[dataFromServer.length-1];
        System.out.println("My server's listening port is:"+this.myServerListenPort);
        byte[] msgBuffer = new byte[dataFromServer.length-16];
        System.out.println("I received "+ (dataFromServer.length-16) +" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-16);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
      	
      	//print the error message to the output.
        System.out.println("[I Received Packet]:"+msg);
        return msg;
    }
    
    
    //This method will process the error packet and display proper message to the user
    public void processErrorPacket()
    {
        //create a buffer that can hold all the byte in the data section of the packet
        byte[] errorMsgBuffer = new byte[dataFromServer.length-14];
        System.arraycopy(dataFromServer,14,errorMsgBuffer,0,dataFromServer.length-14);
        
        //construct a string from all the byte in the data section, which is the error message
        String errorMsg = new String(errorMsgBuffer);
      	
      	//print the error message to the output.
        System.out.println("[An error has occurred]:"+errorMsg);
    }
    
    private void sendErrorPacket(InetAddress address, int port, String message)
    {
        try {
            byte[] buffer = new byte[14+message.length()];
            for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
                    buffer[i] = (byte) ProtocolInfo.PROTOCOL_HEADER[i];
            buffer[8] = (byte)ProtocolInfo.MAJOR_VERSION_NUMBER;
            buffer[9] = (byte)ProtocolInfo.MINOR_VERSION_NUMBER;
            buffer[10] = (byte)(ProtocolInfo.TYPE_ERROR>>8);
            buffer[11] = (byte)(ProtocolInfo.TYPE_ERROR);
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
    
    private void displayErrorFromClient(InetAddress ip, int port, String message)
    {
        System.err.println("[" + ip + ":" + port + "] ERROR: " + message);
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
            this.dataFromServer = new byte[dp.getLength()];
            System.arraycopy(dp.getData(), 0, this.dataFromServer, 0, dp.getLength());
            if(dp.getLength() < 14)
            {
                sendErrorPacket(dp.getAddress(), dp.getPort(), "Incorrect packet length:"+dp.getLength());
                continue;
            }
            
            String messageFromServer = processPacketFromServer();
            if(messageFromServer.equals("No server's running."))
            {
                this.networkController.setThisToBeServer();
            }else
            {
                try{
                    messageFromServer = messageFromServer.substring(1, messageFromServer.length());
                    System.out.println("I'm about to connect to server:"+messageFromServer+"haha");
                    
                    this.myServerIPAddress = InetAddress.getByName(messageFromServer);
                }catch(UnknownHostException e)
                {
                    System.out.println("Cannot find server address");
                }
            }
        }
    }
}
