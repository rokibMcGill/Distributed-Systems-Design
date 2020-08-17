package rmOne;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.PriorityQueue;

import UDP.UDPClient;
import helper.Constants;

public class RM1 {
	public static int nextSequence = 100000;
	public static PriorityQueue<Message> fiform1 = new PriorityQueue<Message>(new MessageComparator()); 
	
	public static void main(String[] args) {

		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();
		
		Runnable taskservers = () -> {
			try {
				RM1_server.main(new String[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread handleThread = new Thread(taskservers);
		handleThread.start();
	}
	

	private static void receive() {
			MulticastSocket aSocket = null;
			String message = null;
			try
			{
				aSocket = new MulticastSocket(Constants.PORT_FE_TO_RM);
				aSocket.joinGroup(InetAddress.getByName(Constants.IP_FE_TO_RM));
				byte[] buffer = new byte[1000];
				System.out.println("Server Started in RM1............");
				while (true) {
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//					System.out.println("Receiving...");
					aSocket.receive(request);
					message = new String(request.getData(),0,request.getLength());
//					System.out.println("  ");
//					System.out.println("RM1 received request.....: " + message);
					if(message.contains("fault"))
					{
						faultHandler(message);
					} else {
						String[] ms = message.split(":"); 
						int sequencerId = Integer.parseInt(ms[ms.length-1]);
						Message msg = new Message(message,sequencerId);
						fiform1.add(msg);
						sendMessageToReplica();
					}	
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
	

	private static void sendMessageToReplica() {
		System.out.println("  ");
		System.out.println("RM1: Size of FIFO :   " + fiform1.size());
		Iterator<Message> itr = fiform1.iterator(); 
//		ArrayList<Message> stlist = new  ArrayList<Message>();
		while (itr.hasNext()) {
			Message request = itr.next();
			if(request.getsequenceId()==nextSequence) {
//				stlist.add(request);
				nextSequence = nextSequence+1;
				String message = request.getMessage();
				sendMessageToServer(message);
			}
		}
	
		 while (!fiform1.isEmpty()) { 
         	fiform1.poll(); 
         }
//		for (int x=0; x < fiform1.size(); x++) {
//			fiform1.poll();
//		}
		
	}


	private static void faultHandler(String message) {
		String[] ms = message.split(";");   
			
		if(ms[2].equals("100") || ms[2].equals("101") || ms[2].equals("102")) {
			System.out.println("Fault Has Been Found in Replica Server : " + ms[2]+" and Fault No " + ms[3]);
			if(Integer.parseInt(ms[3])>= 3) System.out.println("This Server Has Made Consecutive 3 Faults, Please Take Action");
			sendMessageToServer(ms[0]);
		}
	}

	private static void sendMessageToServer(String msg) {
		String[] ms = msg.split(":"); 
		int port = 8881;
		if(ms[0].equals("AS")) {
			port = 8881;
		}else if(ms[0].equals("EU")) {
			port = 7771;
		}else if(ms[0].equals("NA")) {
			port = 6661;
		}
//		System.out.println("It's RM1 msg ............: " + msg +" and Port no: " + port);
		UDPClient.sendUDPMessage(msg, port);
	}
	
	
}
