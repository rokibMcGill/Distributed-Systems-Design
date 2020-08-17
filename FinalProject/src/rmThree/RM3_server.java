package rmThree;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import UDP.UDPClient;
import helper.Constants;
import log.MyLogger;


public class RM3_server {

	public static void main(String[] args) {	
		int asport = 8883;
		int euport = 7773;
		int naport = 6663;
		
		int RMNoAS = 300;
		int RMNoEU = 301;
		int RMNoNA = 302;
        try {           
        	setupLogging();
        	
            GameServerRM3 eventObjAS_RM3 = new GameServerRM3("AS");
            GameServerRM3 eventObjEU_RM3 = new GameServerRM3("EU");
            GameServerRM3 eventObjNA_RM3 = new GameServerRM3("NA");
                        
         // start the city UDP server for inter-city communication
           // the UDP server is started on a new thread
            new Thread(() -> {
                ((GameServerRM3) eventObjAS_RM3).UDPServer();
            }).start();
            
            new Thread(() -> {
                ((GameServerRM3) eventObjEU_RM3).UDPServer();
            }).start();
            
            new Thread(() -> {
                ((GameServerRM3) eventObjNA_RM3).UDPServer();
            }).start();

            
            Runnable taskAS = () -> {
    			receiveFromRM(eventObjAS_RM3, asport, RMNoAS);
    		};
    		Thread threadAS = new Thread(taskAS);
    		threadAS.start();
    		
    		Runnable taskEU = () -> {
    			receiveFromRM(eventObjEU_RM3, euport, RMNoEU);
    		};
    		Thread threadEU = new Thread(taskEU);
    		threadEU.start();
    		
    		Runnable taskNA = () -> {
    			receiveFromRM(eventObjNA_RM3, naport, RMNoNA);
    		};
    		Thread threadNA = new Thread(taskNA);
    		threadNA.start();
    		
    		createInitialDatabase(eventObjAS_RM3, eventObjEU_RM3, eventObjNA_RM3);
          
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

	
	private static void createInitialDatabase(GameServerRM3 eventObjAS_RM3, GameServerRM3 eventObjEU_RM3,
			GameServerRM3 eventObjNA_RM3) {
		if(!eventObjAS_RM3.createPlayerAccount("Soma", "Islam", 33, "soma2000", "soma2000", "182.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjNA_RM3.createPlayerAccount("Sami", "Islam", 7, "sami2014", "sami2014", "132.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjEU_RM3.createPlayerAccount("Jami", "Islam", 1, "jami2020", "jami2020", "93.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjEU_RM3.createPlayerAccount("Rokib", "Islam", 35, "rokib2000", "rokib2000", "93.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjAS_RM3.createPlayerAccount("Rimjhim", "Islam", 2, "rimjhim2020", "rimjhim2020", "182.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjEU_RM3.createPlayerAccount("Ariful", "Islam", 25, "arif2000", "arif2000", "93.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjNA_RM3.createPlayerAccount("Robiul", "Islam", 60, "robi1000", "robi1000", "132.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		
		if(!eventObjNA_RM3.createPlayerAccount("Roma", "Islam", 20, "roma1000", "roma1000", "132.123.123.123"))
		{System.out.println("This Client is Already Initiated in Database ");}
		System.out.println("Initial Data Has Been Succesfully Created");
		
	}


	private static void receiveFromRM(GameServerRM3 eventObj_RM3, int port, int RMno) {
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
				findMessage(message, eventObj_RM3, RMno);
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

	private static void findMessage(String msg, GameServerRM3 eventObj_RM3, int RMno) {
		String input_message = msg;
		String[] parts = input_message.split(":");
		String function = parts[1];
		String sendingResult ="";
		
		if(function.equals("logIn")) {
			boolean output_message = eventObj_RM3.logIn(parts[2], parts[3], parts[4]);
			sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
			System.out.println("It's LogIn msg: " + output_message);
//			System.out.println("  ");
		} else if (function.equals("createPlayerAccount")){
			boolean output_message = eventObj_RM3.createPlayerAccount(parts[2], parts[3], Integer.parseInt(parts[4]), parts[5], parts[6], parts[7]);
			sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
			System.out.println("It's createPlayerAccount msg: " + output_message);	
//			System.out.println("  ");
		} else if(function.equals("logOut")) {
			boolean output_message = eventObj_RM3.logOut(parts[2]);
			sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
			System.out.println("It's logOut msg: " + output_message);
//			System.out.println("  ");
		} else if(function.equals("getPlayerUpdate")) {
			String[] output_message = eventObj_RM3.getPlayerUpdate(parts[2]);
			sendingResult= input_message+";"+output_message[0]+"?"+output_message[1]+"?"+output_message[2]+";"+RMno;
			System.out.println("It's getPlayerUpdate msg: " + output_message[0]+" "+output_message[1]+" "+output_message[2]);
//			System.out.println("  ");
		}else if(function.equals("suspendAccount")) {
			boolean output_message = eventObj_RM3.suspendAccount(parts[2], parts[3], parts[4], parts[5]);
			sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
			System.out.println("It's suspendAccount msg: " + output_message);
//			System.out.println("  ");
		}else if(function.equals("transferAccount")) {
			boolean output_message = eventObj_RM3.transferAccount(parts[2], parts[3], parts[4], parts[5]);
			sendingResult= input_message+";"+Boolean.toString(output_message)+";"+RMno;
			System.out.println("It's transferAccount msg: " + output_message);
//			System.out.println("  ");
		} 
		UDPClient.sendMessageToFrontEnd(sendingResult);	
}

	private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "RM3_Server.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
			
}
	
	
	

