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
            System.err.println("The IP address or the port number for the server is not valid");
            System.exit(1);
        }
        
        this.sessionServerIPAddress = this.networkController.getSessionServerAddress();
        this.sessionServerListenPort = ProtocolInfo.DEFAULT_SESSION_SERVER_PORT_NUMBER;
    }
    
    public InetAddress getClientAddress()
    {
        try{
            
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;

            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            System.out.println(ip);
        
        
            this.myIPAddress = InetAddress.getByName(ip);
            
        }catch(Exception e)
        {
            System.err.println(e);
        }
        
        if(this.myIPAddress == null)
        {
            System.out.println("I don't have a client address!");
        }
//        else
//        {
//            System.out.println(this.myIPAddress.toString());
//        }
        return this.myIPAddress;
    }
    
    public InetAddress getServerAddress()
    {
        if(this.myServerIPAddress == null)
        {
            System.out.println("I don't have a server address!");
        }
//        else
//        {
//            System.out.println(this.myIPAddress.toString());
//        }
        return this.myServerIPAddress;
    }
    
    public int getServerPortNumber()
    {
        return this.myServerListenPort;
    }
    
    
    //process the packet received from the server
    public String processPacketFromServer(InetAddress clientAddress)
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
        
        //System.out.println("Start switching!");
        //System.out.println("Type is:" +packet_type);
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
            case ProtocolInfo.TYPE_UNICAST_WITH_CLIENT_INFO:
                if(dataFromServer.length > 14)
                {
                    processPacketWithClientInformation();
                    return "New Player Added";
                }
                break;
            case ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_INFO:
                //System.out.println("Congrats!");
                if(dataFromServer.length > 14 && this.networkController.isThisServer())
                {
                    return processPacketWithPlayerInformation(clientAddress);
                }
            case ProtocolInfo.TYPE_MULTICAST:
                if(dataFromServer.length > 14)
                {
                    return processMulticastPacket(clientAddress);
                }else
                {
                    //System.out.println("I'm here!");
                }
                break;
            default: 
                System.out.println("Unknown Packet Type");
        }
        return "";
    }
    
    public String processMulticastPacket(InetAddress address)
    {
//        byte[] msgBuffer = new byte[dataFromServer.length-14];
//        //System.out.println("I received "+ (dataFromServer.length-14) +" bytes of data.");
//        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-14);
//        //construct a string from all the byte in the data section, which is the error message
//        String msg = new String(msgBuffer);
//      	
//      	//print the error message to the output.
//        System.out.println("[I Received Packet]:"+msg);
//        return "Broadcast Message received";
        
        byte[] msgBuffer = new byte[dataFromServer.length-14];
        //System.out.println("I received "+ (dataFromServer.length-14) +" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-14);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
      	
      	//print the error message to the output.
        //System.out.println("[I Received Packet]:"+msg);
        int playerX = ((int)msgBuffer[0] << 8)|((int)msgBuffer[1] & 0xFF);
        int playerY = ((int)msgBuffer[2] << 8)|((int)msgBuffer[3] & 0xFF);
        int ballX = ((int)msgBuffer[4] << 8) |((int)msgBuffer[5] & 0xFF);
        int ballY = ((int)msgBuffer[6] << 8)|((int)msgBuffer[7] & 0xFF);
        System.out.println("playerX is:"+playerX);
        System.out.println("playerY is:"+playerY);
        System.out.println("ballX is:"+ballX);
        System.out.println("ballY is:"+ballY);
        PlayerData newPlayerData = new PlayerData(playerX, playerY, ballX, ballY);
        this.networkController.updatePlayerData(address, newPlayerData);
        if(this.networkController.isThisServer())
        {
            this.networkController.broadcastMessage();
        }
        return "Broadcast Message received";
    }
    
    public String processPacketWithPlayerInformation(InetAddress address)
    {
        byte[] msgBuffer = new byte[dataFromServer.length-14];
        //System.out.println("I received "+ (dataFromServer.length-14) +" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-14);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
      	
      	//print the error message to the output.
        //System.out.println("[I Received Packet]:"+msg);
        int playerX = ((int)msgBuffer[0] << 8)|((int)msgBuffer[1] & 0xFF);
        int playerY = ((int)msgBuffer[2] << 8)|((int)msgBuffer[3] & 0xFF);
//        int ballX = ((int)msgBuffer[4] << 8) |((int)msgBuffer[5] & 0xFF);
//        int ballY = ((int)msgBuffer[6] << 8)|((int)msgBuffer[7] & 0xFF);
        int mousePressed = (int)msgBuffer[4];
        
        System.out.println("playerX is:"+playerX);
        System.out.println("playerY is:"+playerY);
        System.out.println("mousePressed is:"+mousePressed);
        
        //PlayerData newPlayerData = new PlayerData(playerX, playerY, ballX, ballY);
        //this.networkController.updatePlayerData(address, newPlayerData);
        //if(this.networkController.isThisServer())
        //{
        //    this.networkController.broadcastMessage();
        //}
        return "Player Update Received";
    }
    
    public String processPacket()
    {
        byte[] msgBuffer = new byte[dataFromServer.length-14];
        //System.out.println("I received "+ (dataFromServer.length-14) +" bytes of data.");
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
        //System.out.println("My server's listening port is:"+this.myServerListenPort);
        byte[] msgBuffer = new byte[dataFromServer.length-16];
        //System.out.println("I received "+ (dataFromServer.length-16) +" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-16);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
      	
      	//print the error message to the output.
        //System.out.println("[I Received Packet]:"+msg);
        return msg;
    }
    
    public void processPacketWithClientInformation()
    {
        int newClientListenPort = (int)dataFromServer[dataFromServer.length-2];
        newClientListenPort <<= 8;
        newClientListenPort &= 0x0000FF00;
        newClientListenPort |= (((int)dataFromServer[dataFromServer.length-1]) & 0x000000FF);
        //System.out.println("My new Client's listening port is:"+newClientListenPort);
        byte[] msgBuffer = new byte[dataFromServer.length-16];
        //System.out.println("I received "+ (dataFromServer.length-16) +" bytes of data.");
        System.arraycopy(dataFromServer,14,msgBuffer,0,dataFromServer.length-16);
        //construct a string from all the byte in the data section, which is the error message
        String msg = new String(msgBuffer);
        InetAddress newClientIP = null;
      	try{
            newClientIP = InetAddress.getByName(msg);
        }catch(UnknownHostException e)
        {
            System.err.println(e);
        }
        
        this.networkController.addPlayer(newClientIP, newClientListenPort);
        
      	//print the error message to the output.
        //System.out.println("New Player with IP at "+newClientIP.toString().substring(1) + " at port: " + newClientListenPort);
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
        //System.out.println("[An error has occurred]:"+errorMsg);
    }
    
    //send the request packet
    public void sendRequestPacket()
    {
        //create a buffer that stores all the byte to send
        byte[] data_to_send = new byte[16];
        System.arraycopy(ProtocolInfo.REQUEST_PACKET_WIHTOUT_LENGTH_DATA, 0, data_to_send, 0, 12);
        data_to_send[12] = 0x00;
        data_to_send[13] = 0x02;
        //System.out.println("My Listening port is:" + this.myListenPort);
        data_to_send[14] = (byte)((this.myListenPort >> 8) & 0x000000FF);
        //System.out.println((int)data_to_send[14]);
        data_to_send[15] = (byte)(this.myListenPort & 0x000000FF);
        //System.out.println((int)data_to_send[15]);
        
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
    
    public void sendPacket(InetAddress targetAddress, int targetPort, byte[] msg, int type)
    {
        if(msg == null)
            return;
        //System.out.println("Regular Packet Sent");  
        byte[] data_to_send = new byte[14+msg.length];
        System.arraycopy(ProtocolInfo.REQUEST_PACKET_WIHTOUT_TYPE_LENGTH_DATA, 0, data_to_send, 0, 10);
        data_to_send[10] =(byte)(type >> 8); 
        data_to_send[11] = (byte)(type);
        data_to_send[12] = (byte)(msg.length >> 8);
        data_to_send[13] = (byte)(msg.length);
        
        for (int i = 0; i < msg.length; i++)
        {
            data_to_send[14+i] = msg[i];
        }
        
        //construct a DatagramPacket
        DatagramPacket sendPacket =  new DatagramPacket(    data_to_send, 
                                                            data_to_send.length, 
                                                            targetAddress, 
                                                            targetPort);
        
        //Try sending the packet. Let the user know when an exception has occurred.
        try{
            clientSocket.send(sendPacket);  
        }catch(IOException e)
        {
            System.out.println("IO Exception has occured while sending the request packet");
        }
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
            //System.out.println("Ready to receive a new packet!");
            
            try {
                listen_socket.receive(dp);
            }
            catch(IOException ioe)
            {
                System.err.println("Warning: Could not receive datagram packet.");
            }
            
            //System.out.println("I received a packet!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            
            this.dataFromServer = new byte[dp.getLength()];
            
            System.arraycopy(dp.getData(), 0, this.dataFromServer, 0, dp.getLength());
            if(dp.getLength() < 14)
            {
                sendErrorPacket(dp.getAddress(), dp.getPort(), "Incorrect packet length:"+dp.getLength());
                continue;
            }
            
            String messageFromServer = processPacketFromServer(dp.getAddress());
            if(messageFromServer.equals("No server's running."))
            {
                this.networkController.setThisToBeServer();
                try{
                    this.myServerIPAddress = this.getClientAddress();
                }catch(Exception e)
                {
                    System.out.println(e);
                }
            }else if(messageFromServer.equals("New Player Added"))
            {
                System.out.println("New Player just added!");
                continue;
            }else if(messageFromServer.equals("Broadcast Message received"))
            {
                System.out.println("Broadcast Packet Received");
            }else if(messageFromServer.equals("Player Update Received"))
            {
                System.out.println("Player Update Packet Received");
            }else
            {
                try{
                    //System.out.println(messageFromServer);
                    //messageFromServer = messageFromServer.substring(1);                    
                    this.myServerIPAddress = InetAddress.getByName(messageFromServer);
                    PlayerData newPlayerData = new PlayerData(100,100,120,120);
                    this.networkController.updatePlayerData(this.myServerIPAddress, newPlayerData);
                }catch(UnknownHostException e)
                {
                    System.out.println("Cannot find server address");
                    continue;
                }
            }
        }
    }
}
