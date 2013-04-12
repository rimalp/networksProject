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
public class UDPClient {
    
    private static final String USAGE_MESSAGE = "Usage: <program_name> <IP_address> <list_port>";
    private static final char[] PROTOCOL_HEADER = {'C','S','3','0','5','T','S','P'};
    private static final int MAJOR_VERSION_NUMBER = 1;
    private static final int MINOR_VERSION_NUMBER = 0;
    private static final byte TYPE_REQUEST = 1;
    private static final int TYPE_RESPONSE = 2;
    private static final int TYPE_ERROR = 3;
    
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
    private static InetAddress IPAddress = null;
    private static int portNum = -1;
    public static byte[] dataFromServer;
    private DatagramSocket listen_socket = null;

    public UDPClient(int listen_port)
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
    
    //send the request packet
    public static void sendRequestPacket()
    {
        //create a buffer that stores all the byte to send
        byte[] data_to_send = new byte[14];
        System.arraycopy(REQUEST_PACKET_WIHTOUT_DATA, 0, data_to_send, 0, 14);
        
        //construct a DatagramPacket
        DatagramPacket sendPacket =  new DatagramPacket(data_to_send, data_to_send.length, IPAddress, portNum);
        
        //Try sending the packet. Let the user know when an exception has occurred.
        try{
            clientSocket.send(sendPacket);  
        }catch(IOException e)
        {
            System.out.println("IO Exception has occured while sending the request packet");
        }
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
            }else{
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
        
        //try establish a connection and let the user know what went wrong if the 
        //connection cannot be established 
        try
        {
            clientSocket = new DatagramSocket();  
        }catch(SocketException e)
        {
            System.out.println("The IP address or the port number for the server is not valid");
            System.exit(1);
        }
        
        //get the IP address
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
        
    } 
    
    
   
}
