package rmThree;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.PriorityQueue;

import UDP.UDPClient;
import helper.Constants;

public class RM3 {
	
	public static int nextSequence = 100000;
	public static PriorityQueue<Message> fiform3 = new PriorityQueue<Message>(new MessageComparator());
	public static void main(String[] args) {

		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();
		
		Runnable taskservers = () -> {
			try {
				RM3_server.main(new String[0]);
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
				System.out.println("Server Started in RM3............");
				while (true) {
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//					System.out.println("Receiving...");
					aSocket.receive(request);
					message = new String(request.getData(),0,request.getLength());
//					System.out.println("  ");
//					System.out.println("RM3 received request.....: " + message);				
					if(message.contains("fault"))
					{
						faultHandler(message);
					} else {
						String[] ms = message.split(":"); 
						int sequencerId = Integer.parseInt(ms[ms.length-1]);
						Message msg = new Message(message,sequencerId);
						fiform3.add(msg);
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
		System.out.println("RM3: Size of FIFO :   " + fiform3.size());
		Iterator<Message> itr = fiform3.iterator(); 
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
		
		 while (!fiform3.isEmpty()) { 
         	fiform3.poll(); 
         }
//		for (int x=0; x < stlist.size(); x++) {
//			fiform2.remove(stlist.get(x));
//		}	
	}
	
	private static void faultHandler(String message) {
		String[] ms = message.split(";");
		
		if(ms[2].equals("300") || ms[2].equals("301") || ms[2].equals("302")) {
			System.out.println("Fault Has Been Found in Replica Server: " + ms[2]+" and Fault No " + ms[3]);
			if(ms[3].equals("3")) System.out.println("This Server Has Made Consecutive 3 Faults, Please Take Action");
			sendMessageToServer(ms[0]);
		}
	}
	

	private static void sendMessageToServer(String msg) {
		String[] ms = msg.split(":"); 
		int port = 8883;
		if(ms[0].equals("AS")) {
			port = 8883;
		}else if(ms[0].equals("EU")) {
			port = 7773;
		}else if(ms[0].equals("NA")) {
			port = 6663;
		}
//		System.out.println("It's RM2 msg ............: " + msg);
		UDPClient.sendUDPMessage(msg, port);
	}
	
	
}
