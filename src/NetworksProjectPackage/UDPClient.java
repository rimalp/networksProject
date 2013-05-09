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
    
    // This will be the socket that this client will be sending packet from
    private DatagramSocket clientSocket = null;
    
    // This will be the socket that this client will be receiving pack from
    private DatagramSocket listen_socket = null;

    
    public UDPClient(NetworkController _networkController)
    {
        this.networkController = _networkController;

        try {
            listen_socket = new DatagramSocket(NetworkController.clientListenPortNumber);
        }
        catch(SocketException se)
        {
            System.err.println("Error: Could not open a server socket on port " + NetworkController.clientListenPortNumber + ".\n" + se.getMessage());
            System.exit(1);
        }
        
        try
        {
            clientSocket = new DatagramSocket();  
        }catch(SocketException e)
        {
            System.err.println("Cannot open an socket to transmit UDP Packet");
            System.exit(1);
        }
        
    }
    
    //process the packet received from the server
    public String processPacket(InetAddress clientAddress, byte[] data)
    {
        if(data.length <= 14)
        {
            return "";
        }
        
        //verify the header of the packet
        for(int i = 0; i < ProtocolInfo.PROTOCOL_HEADER.length; i++)
        {
            if(data[i] != (byte) ProtocolInfo.PROTOCOL_HEADER[i])
            {
                System.out.println("Invalid Header");
            }
        }
        
        //verify the version of the packet
        int server_version_major = (int)data[8];
        int server_version_minor = (int)data[9];
        
        if( server_version_major != ProtocolInfo.MAJOR_VERSION_NUMBER || 
            server_version_minor != ProtocolInfo.MINOR_VERSION_NUMBER )
        {
            System.out.println("Invalid version number");
        }
        
        //verify the type of the packet
        int packet_type = (int) ((data[10] << 8) | data[11]);
        
        byte[] dataBuffer = new byte[data.length - 14];
        for ( int i = 0; i < dataBuffer.length; i++ )
        {
            dataBuffer[i] = data[i + 14];
        }
//        System.out.println("Data Buffer size is: "+dataBuffer.length);
//        System.out.println("data size is: "+data.length);
//        System.out.println("Start switching!");
        //System.out.println("Type is:" +packet_type);
        switch(packet_type)
        {
            case ProtocolInfo.TYPE_REQUEST: break;
            case ProtocolInfo.TYPE_ERROR:
                this.processErrorPacket(dataBuffer);
                break;
            case ProtocolInfo.TYPE_UNICAST:
                if(data.length > 14)
                {
                    return processPacket(dataBuffer);
                }
                break;
            case ProtocolInfo.TYPE_UNICAST_WITH_SERVER_INFO:
                if(data.length > 14)
                {
                    return processPacketWithServerInformation(dataBuffer);

                }
                break;
            case ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO:
                if(data.length > 14)
                {
                    processPacketWithNewPlayerInformation(dataBuffer, clientAddress);
                    return "New Player Added";
                }
                break;
            case ProtocolInfo.TYPE_UNICAST_WITH_PLAYER_DATA:
                if(data.length > 14 && NetworkController.thisIsServer)
                {
                    return processUnicastPacketWithPlayerData(clientAddress, dataBuffer);
                }
            case ProtocolInfo.TYPE_MULTICAST:
                if(data.length > 14)
                {
                    return processMulticastPacket(dataBuffer);
                }
                break;
            case ProtocolInfo.TYPE_UNICAST_JOINGAME:
                if(data.length > 14){
                    return processPacketWithHostServersInformation(dataBuffer);
                }

                break;
            default: 
                System.out.println("Unknown Packet Type");
        }
        return "";
    }



    public String processMulticastPacket(byte[] data)
    {
        NetworkController.realTimeData.updateBasedOnBytesFromServer(data);
        //System.out.println("After received multicast packet");
//        System.out.println(NetworkController.realTimeData.printPlayersData());
        //System.out.println(NetworkController.realTimeData.printPlayersData());
        this.networkController.multicastReceived();
        
        return "Multicast Message received";
    }
    
    public String processUnicastPacketWithPlayerData(InetAddress address, byte[] data)
    {
        NetworkController.realTimeData.updateBasedOnBytesFromClient(data);
        //System.out.println("After receiving palyer data");
        //System.out.println(NetworkController.realTimeData.printPlayersData());
        this.networkController.broadcastMessage();
        return "Player Data Update Received";
    }
    
    public String processPacket(byte[] data)
    {
        String msg = new String(data);
        return msg;
    }
    
    public byte[] getBytesForNewPlayerInfo()
    {
        byte[] bytesToReturn = new byte[6];
        
        byte[] ipBuffer = new byte[4];
        byte[] portNumBuffer = new byte[2];
        
        ipBuffer = NetworkController.myIPAddress.getAddress();
        
        //System.out.println(NetworkController.clientListenPortNumber);
        
        portNumBuffer[1] = (byte)(NetworkController.clientListenPortNumber >> 8);
        portNumBuffer[0] = (byte)(NetworkController.clientListenPortNumber);
        
        for(int j = 0; j < 2; j++)
        {
            bytesToReturn[j] = portNumBuffer[j];
        }
        
        for(int i = 0; i < 4; i++)
        {
            bytesToReturn[2 + i] = ipBuffer[i];
        }
        
        return bytesToReturn;
    }


    public String processPacketWithHostServersInformation(byte[] data){
        int myServerListenPort = 0;
        myServerListenPort = 0;
        myServerListenPort += (data[4] << 8);
        myServerListenPort += data[5];

        byte[] ipBuffer = new byte[4];
        ipBuffer[0] = data[0];
        ipBuffer[1] = data[1];
        ipBuffer[2] = data[2];
        ipBuffer[3] = data[3];


        NetworkController.serverListenPortNumber = myServerListenPort;

        //construct a string from all the byte in the data section, which is the error message


        //update the main menu combobox items
        InetAddress inet = null;
        try{
         inet = InetAddress.getByAddress(ipBuffer);
//         newIp = inet.getHostAddress();
        }catch(Exception e){
            System.out.println("Exception in converting sessionserver");
            return "Cannot find server address";
        }
        if(inet != null){
//        String newIp = inet.toString();
        this.networkController.mainController.addHostServerInMainMenu(inet, myServerListenPort);
        }
        return "Received Server Information";
    }
    //rimalp
    public String processPacketWithServerInformation(byte[] data)
    {
        int myServerListenPort = 0;
        myServerListenPort = 0;
        myServerListenPort += (data[4] << 8);
        myServerListenPort += data[5];
        
        byte[] ipBuffer = new byte[4];
        ipBuffer[0] = data[0];
        ipBuffer[1] = data[1];
        ipBuffer[2] = data[2];
        ipBuffer[3] = data[3];
        
        
        NetworkController.serverListenPortNumber = myServerListenPort;
        
        //construct a string from all the byte in the data section, which is the error message


        //update the main menu combobox items
        InetAddress inet = null;
        try{
         inet = InetAddress.getByAddress(ipBuffer);
//         newIp = inet.getHostAddress();
        }catch(Exception e){System.out.println("Exception in converting sessionserver");}
        if(inet != null){
        String newIp = inet.toString();
        System.out.println("udpclient server: " + newIp);
        this.networkController.mainController.addHostServerInMainMenu(inet, myServerListenPort);
        }

        String msg = new String(data);
      	try{                  
            System.out.println(myServerListenPort);
            
            NetworkController.serverAddress = InetAddress.getByAddress(ipBuffer);
            
            System.out.println(NetworkController.serverAddress.getHostAddress());
            PlayerData defaultPlayerData = new PlayerData(300,300,450,450);
            NetworkController.realTimeData.addNewPlayer(NetworkController.myIPAddress, defaultPlayerData);
            this.sendPacket(NetworkController.serverAddress, NetworkController.serverListenPortNumber, this.getBytesForNewPlayerInfo(), ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO);
        }catch(UnknownHostException e)
        {
            return "Cannot find server address";
        }
        return "Received Server Information";
    }
    
    public void processPacketWithNewPlayerInformation(byte[] data, InetAddress clientAddress)
    {
        int newClientListenPort = (int)data[1];
        newClientListenPort <<= 8;
        newClientListenPort &= 0x0000FF00;
        newClientListenPort |= (((int)data[0]) & 0x000000FF);
        
        this.networkController.addPlayer(clientAddress, newClientListenPort, null);
    }
    
    
    //This method will process the error packet and display proper message to the user
    public void processErrorPacket(byte[] data)
    {
        //construct a string from all the byte in the data section, which is the error message
        String errorMsg = new String(data);
      	
      	//print the error message to the output.
        System.out.println("[An error has occurred]:"+errorMsg);
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
        data_to_send[14] = (byte)((NetworkController.clientListenPortNumber >> 8) & 0x000000FF);
        //System.out.println((int)data_to_send[14]);
        data_to_send[15] = (byte)(NetworkController.clientListenPortNumber & 0x000000FF);
        //System.out.println((int)data_to_send[15]);
        
        //construct a DatagramPacket
        DatagramPacket sendPacket =  new DatagramPacket(    data_to_send, 
                                                            data_to_send.length, 
                                                            NetworkController.sessionServerAddress, 
                                                            NetworkController.sessionServerListenPortNumber);
        
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
        //add my port number

        data_to_send[14] = (byte) (NetworkController.clientListenPortNumber >> 8);
        data_to_send[15] = (byte) (NetworkController.clientListenPortNumber);
        
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
        byte[] buffer = new byte[65535];
        
        while(true)
        {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            //System.out.println("Ready to receive a new packet!");
            
            try {
                listen_socket.receive(dp);
            }
            catch(IOException ioe)
            {
                System.err.println("Warning: Could not receive datagram packet.");
            }
                        
            byte[] dataFromServer = new byte[dp.getLength()];
            
            System.arraycopy(dp.getData(), 0, dataFromServer, 0, dp.getLength());
            if(dp.getLength() < 14)
            {
                sendErrorPacket(dp.getAddress(), dp.getPort(), "Incorrect packet length:"+dp.getLength());
                continue;
            }
            
            String status = processPacket(dp.getAddress(), dataFromServer);
            
            if(status.equals("No server's running."))
            {
                NetworkController.thisIsServer = true;
                try{
                    NetworkController.serverAddress = NetworkController.myIPAddress;
                    NetworkController.serverListenPortNumber = NetworkController.clientListenPortNumber;
                    this.sendPacket(NetworkController.serverAddress, NetworkController.serverListenPortNumber, this.getBytesForNewPlayerInfo(), ProtocolInfo.TYPE_UNICAST_WITH_NEW_PLAYER_INFO);
                }catch(Exception e)
                {
                    System.out.println(e);
                }
            }
//            System.out.println(status);
        }
    }
}
