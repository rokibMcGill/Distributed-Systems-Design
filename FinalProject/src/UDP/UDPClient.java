package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import helper.Constants;

public class UDPClient {
	
	public static void sendUDPMessage(String message, int port) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] messages = message.getBytes();
			InetAddress aHost = InetAddress.getLocalHost();
			DatagramPacket request = new DatagramPacket(messages, messages.length, aHost, port);
			aSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void sendUDPMessageToRM(String message) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] messages = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.IP_FE_TO_RM);

			DatagramPacket request = new DatagramPacket(messages, messages.length, aHost, Constants.PORT_FE_TO_RM);
			aSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void sendMessageToFrontEnd(String message) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.IP_RM_TO_FE);

			DatagramPacket request = new DatagramPacket(m, m.length, aHost, Constants.PORT_RM_TO_FE);
			aSocket.send(request);
			aSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void sendUDPMessageToSequence(String message, int port) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] messages = message.getBytes();
			InetAddress aHost = InetAddress.getLocalHost();
			DatagramPacket request = new DatagramPacket(messages, messages.length, aHost, port);
			aSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
		
}
