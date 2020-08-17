package clients;

import java.io.IOException;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import FaultToleranceDPSS.DPSS_FT;
import FaultToleranceDPSS.DPSS_FTHelper;

public class Clients {

	static Scanner input = new Scanner(System.in);
	public static void main(String args[])
	{
	try {
		ORB orb = ORB.init(args, null);
		// -ORBInitialPort 900 -ORBInitialHost localhost
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//		System.out.println("Initial Data Has Been Succesfully Created");
		DPSS_FT eventObj = DPSS_FTHelper.narrow(ncRef.resolve_str("frontend"));
		startSystem(eventObj);
	} catch (Exception e) {
		System.out.println("Hello Client exception: " + e);
		e.printStackTrace();
	}
	
}

	public static void startSystem(DPSS_FT eventObj) throws NotFound, CannotProceed, InvalidName, IOException {
		String uname, pword, ip;
		String operation ;
		operation = displayMenu();
		if(operation.equals("0")) {
			System.out.print("Please enter your IPAddress : ");
		    ip = input.next();
		    while(!Utils.isValidIP(ip)) {
		    	System.out.print("Please enter Currect IPAddress : ");
		    	ip = input.next();
		    }
		    PlayerClient plc01 =  new PlayerClient(ip);
		    plc01.performOperations(eventObj, "2");
		    
		} else if (operation.equals("1")) {
			System.out.print("Please Enter Your User Name : ");
		    uname = input.next();
		    System.out.print("Please Enter Your Password : ");
		    pword = input.next();
		    System.out.print("Please enter your IPAddress : ");
		    ip = input.next();
		    while(!Utils.isValidIP(ip)) {
		    	System.out.print("Please enter Currect IPAddress : ");
		    	ip = input.next();
		    }
		  
		    if(uname.equals("admin") && pword.equals("admin")) { 
		    	AdministratorClient ad01 = new AdministratorClient(uname, pword, ip);
		    	ad01.performOperations(eventObj, "0");
		    } else {
		    	PlayerClient plc02 =	new PlayerClient(uname, pword, ip);
		    	plc02.performOperations(eventObj, "0");
		    } 
		} else {
			startSystem(eventObj);
		}
	}
		
			
	public static String displayMenu() {
//		System.out.println(" ");
		System.out.println(" ");
		System.out.println("--------------------------------");
		System.out.println("WELCOME TO DISTRIBUTED SYSTEM");
        System.out.println("--------------------------------");
        System.out.println("|	Available Operations |");
        System.out.println("--------------------------------");
        System.out.println("|0| If You Are A New User");
        System.out.println("|1| If You ARE An Existing User");
        System.out.println("|2| Not Interested");
        System.out.print("Input your operation number : ");
        String sendoption = input.next();
        return sendoption;
    }
	
	
	
}
	