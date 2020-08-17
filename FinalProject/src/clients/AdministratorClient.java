package clients;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import FaultToleranceDPSS.DPSS_FT;
import helper.Constants;
import log.MyLogger;

public class AdministratorClient {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	String userName;
	String Password;
	String string_ip;
	int ipaddress;
	
	Scanner input = new Scanner(System.in);

	public AdministratorClient(String uname, String pword, String ip) {
		userName = uname;
		Password = pword;
		string_ip = ip;
	}
	
	
	public void performOperations(DPSS_FT eventObj, String operation) throws NotFound, CannotProceed, InvalidName, IOException{

			ipaddress = Utils.getIntegerIpaddress(string_ip);
			String serverName = Utils.decideServerPort(ipaddress);
			String uname, pword;
			boolean validusername;
			
			if(operation.equals("0")) {
			validusername =  isvalidAdmin(userName, Password, ipaddress);
//				validusername = eventObj.logIn(userName, Password, string_ip);
			if(validusername) {
				setupLogging();
				System.out.println("Admin Login Successful : " + userName);
				LOGGER.info("Login Successful: " + " User Name: " + userName + "  Server Name: " + serverName );
				operation = displayMenuB();
				performOperations(eventObj, operation);
			} else {
				System.out.println("Login Fail: Invalid User Name or Password");
				operation = displayMenuA();
				performOperations(eventObj, operation);
			}
			} else if (operation.equals("1")) {
				System.out.print("Please Enter Your User Name : ");
			    uname = input.next();
			    userName = uname;
			    System.out.print("Please Chose Password Name : ");
			    pword = input.next();
			    Password = pword;
			    eventObj.logIn(userName, Password, string_ip);
				validusername =  isvalidAdmin(userName, Password, ipaddress);
//				validusername = eventObj.logIn(userName, Password, string_ip);
				if(validusername) {
					setupLogging();
					System.out.println("Admin Login Successful : " + userName);
					LOGGER.info("Login Successful: " + " User Name: " + userName + "  Server Name: " + serverName );
					operation = displayMenuB();
					performOperations(eventObj, operation);
				} else {
					System.out.println("Login Fail: Invalid User Name or Password");
					operation = displayMenuA();
					performOperations(eventObj, operation);
				}
			} else if (operation.equals("2")) {
				String[] loginupdate = eventObj.getPlayerUpdate(userName);	
				System.out.println(loginupdate[0]+" "+ loginupdate[1]+ " "+ loginupdate[2]);
				System.out.println(" ");
				LOGGER.info("Admin "  + userName + " Invoke Player Update from server : " + serverName );			
				operation = displayMenuB();
				performOperations(eventObj, operation);
			}  else if (operation.equals("3")) {
				System.out.print("Please Enter Account User Name You Want to Suspend : ");
			    String suspendacc = input.next();
				if( eventObj.suspendAccount(userName, Password, string_ip, suspendacc)) {
					System.out.println("Account Suspendded Succesfully");
				} else 
				{
					System.out.println("This Account Has Already Suspendded OR Incorrect Account:");	
				}
				operation = displayMenuB();
				performOperations(eventObj, operation);
			} 
			
			if (operation.equals("4")) {
				Clients.startSystem(eventObj);
			} 
			else {
				Clients.startSystem(eventObj);
			}
	}
			
		
		
		private boolean isvalidAdmin(String userName2, String password2, int ipaddress2) {
			String userName3 = "admin";
			String password3 = "admin";
			if(userName2.equals(userName3) && password2.equals(password3) && ipaddress2 == ipaddress) return true;
			return false;
		}

		private String displayMenuA() {
			String inputoperation = null;
			boolean isvalidinputoperation = true;
			while(isvalidinputoperation) {
	        System.out.println("--------------------------------");
	        System.out.println("|	Available Operations 	|");
	        System.out.println("--------------------------------");
	        System.out.println("|1| Re-LogIn");
	        System.out.println("|4| Quit");
	        System.out.print("Input your operation number : ");
	        inputoperation = input.next();
        	if(inputoperation.equals("1") || inputoperation.equals("4") ) {
        		isvalidinputoperation = false;
        	}  else {
        		isvalidinputoperation = true;
        		System.out.println("WOOPS:...You Selected Wrong operation number:");
        	}
		}
        
        return inputoperation;
    }
		
		private String displayMenuB() {
			String inputoperation = null;
			boolean isvalidinputoperation = true;
			while(isvalidinputoperation) {
	        System.out.println("--------------------------------");
	        System.out.println("|	Available Operations 	|");
	        System.out.println("--------------------------------");
	        System.out.println("|2| Check Player Status");
	        System.out.println("|3| Suspend Player Account");
	        System.out.println("|4| LogOut");
	        System.out.print("Input your operation number : ");
	        inputoperation = input.next();
        	if(inputoperation.equals("2") ||inputoperation.equals("3") || inputoperation.equals("4") ) {
        		isvalidinputoperation = false;
        	}  else {
        		isvalidinputoperation = true;
        		System.out.println("WOOPS:...You Selected Wrong operation number:");
        	}
		}
        
        return inputoperation;
    }
		
		private void setupLogging() throws IOException {
	        File files = new File(Constants.ADMIN_LOG_DIRECTORY);
	        if (!files.exists())
	            files.mkdirs();
	        files = new File(Constants.ADMIN_LOG_DIRECTORY + userName + ".log");
	        if (!files.exists())
	            files.createNewFile();
	        MyLogger.setup(files.getAbsolutePath());
	    }

}
