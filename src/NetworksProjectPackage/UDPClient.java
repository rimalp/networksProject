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
    
    private static final String USAGE_MESSAGE = "Usage: <program_name> <IP_address> <list_port>";
    private static final char[] PROTOCOL_HEADER = {'C','S','3','0','5','T','S','P'};
    private static final int MAJOR_VERSION_NUMBER = 1;
    private static final int MINOR_VERSION_NUMBER = 0;
    private static final int TYPE_REQUEST = 1;
    private static final int TYPE_RESPONSE = 2;
    private static final int TYPE_ERROR = 3;
    private static final int TYPE_UNICAST = 4;
    private static final int TYPE_MULTICAST = 5;
    private static NetworkController networkManager = null;
    
    private static final byte[] REQUEST_PACKET_WIHTOUT_DATA = 
    {(byte) PROTOCOL_HEADER[0], 
        (byte) PROTOCOL_HEADER[1], 
        (byte) PROTOCOL_HEADER[2],
        (byte) PROTOCOL_HEADER[3], 
        (byte) PROTOCOL_HEADER[4], 
        (byte) PROTOCOL_HEADER[5], 
        (byte) PROTOCOL_HEADER[6], 
        (byte) PROTOCOL_HEADER[7],
        MAJOR_VERSION_NUMBER, 
        MINOR_VERSION_NUMBER, 
        (byte)(TYPE_REQUEST>>8), 
        (byte)TYPE_REQUEST,  
        0, 
        0};
    
    private static DatagramSocket clientSocket = null;
    private static String address = null;
    private InetAddress IPAddress = null;
    private int portNum = -1;
    public static byte[] dataFromServer;
    private DatagramSocket listen_socket = null;

    public UDPClient(int listen_port, NetworkController _networkManager)
    {
        this.networkManager = _networkManager;
        this.portNum = 6666;
        try {
            listen_socket = new DatagramSocket(listen_port);
        }
        catch(SocketException se)
        {
            System.err.println("Error: Could not open a server socket on port " + listen_port + ".\n" + se.getMessage());
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
        
        try{
            this.IPAddress = InetAddress.getByName("localhost");
        }catch(Exception e)
        {
            System.out.println("Cannot find local host");
        }
    }
    
    //send the request packet
    public void sendRequestPacket()
    {
        //create a buffer that stores all the byte to send
        byte[] data_to_send = new byte[14];
        System.arraycopy(REQUEST_PACKET_WIHTOUT_DATA, 0, data_to_send, 0, 14);
        
        //construct a DatagramPacket
        DatagramPacket sendPacket =  new DatagramPacket(data_to_send, data_to_send.length, this.IPAddress, this.portNum);
        
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
    public static void processPacketFromServer()
    {
        //verify the header of the packet
        for(int i = 0; i < PROTOCOL_HEADER.length; i++)
        {
            if(dataFromServer[i] != (byte) PROTOCOL_HEADER[i])
            {
                System.out.println("Invalid Header");
            }
        }
        
        
        //verify the version of the packet
        int server_version_maj = (int)dataFromServer[8];
        int server_version_minor = (int)dataFromServer[9];
        
        if(server_version_maj != MAJOR_VERSION_NUMBER || server_version_minor != MINOR_VERSION_NUMBER)
        {
            System.out.println("Invalid version number");
        }
        
        //verify the type of the packet
        int packet_type = (int) ((dataFromServer[10] << 8) | dataFromServer[11]);
        
        if(packet_type != TYPE_RESPONSE)
        {		
        		//if the packet is type error,then use a separate method to process the packet
            if(packet_type == TYPE_ERROR)
            {
                processErrorPacket();
             
            //If the packet type cannot be recognized, then it is not a valid packet type,
            //process of this packet will not be continued.
            }else if( packet_type == TYPE_UNICAST || packet_type == TYPE_MULTICAST )
            {
                processPacket();
            }else
            {
                System.out.println("Invalid packet type!");
            }
        }else
        {
        		//verify the length of the packet
            if(dataFromServer[12]!= 0 || dataFromServer[13] != 8)
            {
                System.out.println("Invalid Packet Length");
            }
            
            //Shift all the byte to its original position in the long variable that represents the
            //time sent from the server  
            long timeFromServer = 0;
            byte newByte = 0;
            long decodedByte = 0;
            for(int i = 7; i>=0; i--)
            {
                newByte = dataFromServer[14+i];
                decodedByte = ((long)newByte) & 0x00FF;
                timeFromServer += (decodedByte<<(8*(7-i)));
            }
            
            //reconstruct the Date variable from the long variable to able to print a human-readable
            //time and date
            Date dataReceivedFromServer = new Date(timeFromServer);
            System.out.println(dataReceivedFromServer);
        }
    }
    
    public static void processPacket()
    {
        byte[] msgBuffer = new byte[dataFromServer.length-14];
        System.out.println("I received "+msgBuffer.length+" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-14);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
      	
      	//print the error message to the output.
        System.out.println("[I Received Packet]:"+msg);

    }
    //This method will process the error packet and display proper message to the user
    public static void processErrorPacket()
    {
    		//create a buffer that can hold all the byte in the data section of the packet
        byte[] errorMsgBuffer = new byte[dataFromServer.length-14];
        System.arraycopy(dataFromServer,14,errorMsgBuffer,0,dataFromServer.length-14);
        
        //construct a string from all the byte in the data section, which is the error message
        String errorMsg = new String(errorMsgBuffer);
      	
      	//print the error message to the output.
        System.out.println("[An error has occurred]:"+errorMsg);
    }
    
    public static void main(String args[]) throws Exception  
    { 
        //make sure that the user enters a valid command
        //and let the user know about the correct format when the command
        //user entered was invalid
        /*
        if(args.length != 2)
        {
            System.err.println("Invalid Arguments");
            System.out.println(USAGE_MESSAGE);
            System.exit(1);
        }
        
        address = args[0];
        portNum = Integer.parseInt(args[1]);
        
        if(portNum == -1)
        {
            System.err.println("Invalid Port Number");
            System.exit(1);
        }
        */
        //try establish a connection and let the user know what went wrong if the 
        //connection cannot be established 
        /*
        try
        {
            clientSocket = new DatagramSocket();  
        }catch(SocketException e)
        {
            System.out.println("The IP address or the port number for the server is not valid");
            System.exit(1);
        }
        
        //get the IP address
        address = "139.147.103.11";
        portNum = 4444;
        IPAddress = InetAddress.getByName(address);    


        sendRequestPacket();
        
        //construct a DatagramPacket
        byte[] receiveData = new byte[1024];  
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);  
        
        //set the timeout to be 3 seconds
        try{
            clientSocket.setSoTimeout(3000);
        }catch(SocketException e)
        {
            System.out.println("SocketException has occurred.");
        }
        
        //Try receive a packet. If the 3-seconds timer expires, throw an exception and let the user
        //know that the server isn't responding.
        try{
            clientSocket.receive(receivePacket);  
        }catch(SocketTimeoutException e)
        {
            System.out.println("The server is not responding.(3 sec exceeded)");
            System.exit(1);
        }
        
        //if the packet is received from the server within 3 seconds,
        //verify the length of the packet and interpret the packet from the server
        dataFromServer = receivePacket.getData(); 
        if(receivePacket.getLength() >= 14)
        {
            processPacketFromServer();
        }else
        {
            System.out.println("Invalid Data Packet Length");
        }
        
        //close the connection
        clientSocket.close();  
        */
        UDPClient ts = new UDPClient(4445, null);
        System.out.println("Time server started on port 4445.");
        ts.start();
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
    
    private final void displayErrorFromClient(InetAddress ip, int port, String message)
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

            if(packet_type == TYPE_UNICAST)
            {
                dataFromServer = dp.getData(); 
                if(dp.getLength() >= 14)
                {
                    processPacketFromServer();
                }else
                {
                    System.out.println("Invalid Data Packet Length");
                }
                continue;
            }
        }
        
    }

}
