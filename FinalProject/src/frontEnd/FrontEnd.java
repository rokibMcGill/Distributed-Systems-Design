package frontEnd;

import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

import FaultToleranceDPSS.DPSS_FT;
import FaultToleranceDPSS.DPSS_FTHelper;
import helper.Constants;
import log.MyLogger;

public class FrontEnd {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		
			try {
				setupLogging();
				FrontEndImp eventObj = new FrontEndImp();
				Runnable task = () -> {
					receive(eventObj);
				};
				
				Thread thread = new Thread(task);
				thread.start();
				
				// create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
				ORB orb = ORB.init(args, null);
	        	POA rootpoa = (POA)orb.resolve_initial_references("RootPOA");
	        	rootpoa.the_POAManager().activate();
	        	
	        	// create servant and register it with the ORB
	            eventObj.setORB(orb);

	            // get object reference from the servant
	            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(eventObj);
	            DPSS_FT href = DPSS_FTHelper.narrow(ref);

	            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	            NameComponent path[] = ncRef.to_name("frontend");
	            ncRef.rebind(path, href);

	            System.out.println("Front End Server ready and waiting ...");
	        
	         // wait for invocations from clients
	            for (;;) {
	                orb.run();
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }

	private static void receive(FrontEndImp eventObj) {
		MulticastSocket aSocket = null;
		String message = null;
		try
		{
			aSocket = new MulticastSocket(Constants.PORT_RM_TO_FE);
			aSocket.joinGroup(InetAddress.getByName(Constants.IP_RM_TO_FE));
			System.out.println("Server Started FE............");
			byte[] buffer = new byte[1000];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//				System.out.println("Receiving...");
				aSocket.receive(request);
				message = new String(request.getData(),0,request.getLength());
				System.out.println("Response From Replica Server: " + message);
				eventObj.messagelist.add(message);
				System.out.println("Response Added ");
				
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
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
	
	private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "FE_Server.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
		
	}
