package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import UDP.UDPClient;
import helper.Constants;

public class Sequencer {
	private static int sequencerId = 100000;
	public static void main(String[] args) {
		
//	}
//	public static void ReceviedRequestFrom(String message) {	
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Constants.PORT_SEC);
			byte[] buffer = new byte[1000];
			System.out.println("Sequencer UDP Server 1313 Started............");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String sentence = new String( request.getData(), 0,
						request.getLength() );
//				System.out.println("1. It's sequence msg: " + sentence);
				ForwardRequestToRM(sentence);
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
	
	public static void ForwardRequestToRM(String message) {
//			int port = 1412;
//			String IP = "230.1.1.10";
			message = message+sequencerId;
			System.out.println("It's sequence msg: " + message);
			sequencerId++;
		UDPClient.sendUDPMessageToRM(message.trim());
		
	}
}