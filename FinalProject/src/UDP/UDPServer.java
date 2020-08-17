package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import helper.Constants;

public class UDPServer {
	
	public static String message = "";
	
	
	@SuppressWarnings({ "finally", "resource" })
	public static String ReceiveUDPMessageFromSequencer() {
		MulticastSocket aSocket = null;
//		String message = null;
		try
		{
			aSocket = new MulticastSocket(Constants.PORT_FE_TO_RM);
			aSocket.joinGroup(InetAddress.getByName(Constants.IP_FE_TO_RM));
			byte[] buffer = new byte[1000];
			System.out.println("Server Started in RM............");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				System.out.println("Receiving...");
				aSocket.receive(request);
				message = new String(request.getData(),0,request.getLength());
				System.out.println("1 Server Started in RM............" + message);
//				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
//						request.getPort());
//				aSocket.send(reply);
				return message;
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();				
	
			return message;
		}
//		System.out.println("2 Server Started in RM............" + message);
//		return message;
		
	}
	
	
	@SuppressWarnings({ "finally", "resource" })
	public static String ReceiveUDPMessageFromRM(int port) {
		DatagramSocket aSocket = null;
//		String message= null;
		try {
			aSocket = new DatagramSocket(port);
			byte[] buffer = new byte[1000];
			System.out.println("Sequencer UDP Server "+ port +" Started............");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				message = new String( request.getData(), 0, request.getLength() );	
				System.out.println("Sequencer UDP Server "+ port +" Started............" + message);
				return message;
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
			
			return message;
		}
		
//		return message;
		
		
	}
	
	
}