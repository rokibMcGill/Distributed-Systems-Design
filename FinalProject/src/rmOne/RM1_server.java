package rmOne;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
//import java.util.logging.Logger;

import UDP.UDPClient;
import helper.Constants;
import log.MyLogger;


public class RM1_server {

	public static void main(String[] args) {	
		int asport = 8881;
		int euport = 7771;
		int naport = 6661;
		
		int RMNoAS = 100;
		int RMNoEU = 101;
		int RMNoNA = 102;
		
        try {    
        	setupLogging();
        	
            GameServerRM1 eventObjAS_RM1 = new GameServerRM1("AS");
            GameServerRM1 eventObjEU_RM1 = new GameServerRM1("EU");
            GameServerRM1 eventObjNA_RM1 = new GameServerRM1("NA");
                        
         // start the city UDP server for inter-city communication
           // the UDP server is started on a new thread
            new Thread(() -> {
                ((GameServerRM1) eventObjAS_RM1).UDPServer();
            }).start();
            
            new Thread(() -> {
                ((GameServerRM1) eventObjEU_RM1).UDPServer();
            }).start();
            
            new Thread(() -> {
                ((GameServerRM1) eventObjNA_RM1).UDPServer();
            }).start();

            
            Runnable taskAS = () -> {
    			receiveFromRM(eventObjAS_RM1, asport, RMNoAS);
    		};
    		Thread threadAS = new Thread(taskAS);
    		threadAS.start();
    		
    		Runnable taskEU = () -> {
    			receiveFromRM(eventObjEU_RM1, euport, RMNoEU);
    		};
    		Thread threadEU = new Thread(taskEU);
    		threadEU.start();
    		
    		Runnable taskNA = () -> {
    			receiveFromRM(eventObjNA_RM1, naport, RMNoNA);
    		};
    		Thread threadNA = new Thread(taskNA);
    		threadNA.start();
    		
    		createInitialDatabase(eventObjAS_RM1, eventObjEU_RM1, eventObjNA_RM1);
          
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

	
	private static void createInitialDatabase(GameServerRM1 eventObjAS_RM1, GameServerRM1 eventObjEU_RM1,
			GameServerRM1 eventObjNA_RM1) {
		if(!eventObjAS_RM1.createPlayerAccount("Soma", "Islam", 33, "soma2000", "soma2000", "182.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjNA_RM1.createPlayerAccount("Sami", "Islam", 7, "sami2014", "sami2014", "132.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjEU_RM1.createPlayerAccount("Jami", "Islam", 1, "jami2020", "jami2020", "93.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjEU_RM1.createPlayerAccount("Rokib", "Islam", 35, "rokib2000", "rokib2000", "93.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjAS_RM1.createPlayerAccount("Rimjhim", "Islam", 2, "rimjhim2020", "rimjhim2020", "182.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjEU_RM1.createPlayerAccount("Ariful", "Islam", 25, "arif2000", "arif2000", "93.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjNA_RM1.createPlayerAccount("Robiul", "Islam", 60, "robi1000", "robi1000", "132.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjNA_RM1.createPlayerAccount("Roma", "Islam", 20, "roma1000", "roma1000", "132.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		System.out.println("Initial Data Has Been Succesfully Created");
		
	}


	private static void receiveFromRM(GameServerRM1 eventObj_RM1, int port, int RMno) {
		DatagramSocket aSocket = null;
		String message= null;
		try {
			aSocket = new DatagramSocket(port);
			byte[] buffer = new byte[1000];
			System.out.println("Sequencer UDP Server "+ port +" Started............");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				message = new String( request.getData(), 0, request.getLength() );	
				System.out.println("Sequencer UDP Server "+ port +" Started............: " + message);
				findMessage(message, eventObj_RM1, RMno);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		
	}

	private static void findMessage(String msg, GameServerRM1 eventObj_RM1, int RMno) {
			String input_message = msg;
			String[] parts = input_message.split(":");
			String function = parts[1];
			String sendingResult ="";
			
			if(function.equals("logIn")) {
				boolean output_message = eventObj_RM1.logIn(parts[2], parts[3], parts[4]);
				sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
				System.out.println("It's LogIn msg: " + output_message);
			} else if (function.equals("createPlayerAccount")){
				boolean output_message = eventObj_RM1.createPlayerAccount(parts[2], parts[3], Integer.parseInt(parts[4]), parts[5], parts[6], parts[7]);
				sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
				System.out.println("It's createPlayerAccount msg: " + output_message);
			} else if(function.equals("logOut")) {
				boolean output_message = eventObj_RM1.logOut(parts[2]);
				sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
				System.out.println("It's logOut msg: " + output_message);
			} else if(function.equals("getPlayerUpdate")) {
				String[] output_message = eventObj_RM1.getPlayerUpdate(parts[2]);
				sendingResult= input_message+";"+output_message[0]+"?"+output_message[1]+"?"+output_message[2]+";"+RMno;
				System.out.println("It's getPlayerUpdate msg: " + output_message[0]+" "+output_message[1]+" "+output_message[2]);
			}else if(function.equals("suspendAccount")) {
				boolean output_message = eventObj_RM1.suspendAccount(parts[2], parts[3], parts[4], parts[5]);
				sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
				System.out.println("It's suspendAccount msg: " + output_message);
			}else if(function.equals("transferAccount")) {
				boolean output_message = eventObj_RM1.transferAccount(parts[2], parts[3], parts[4], parts[5]);
				sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
				System.out.println("It's transferAccount msg: " + output_message);
			} 

			UDPClient.sendMessageToFrontEnd(sendingResult);			 
}

	private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "RM1_Server.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
			
}
	
	
	

