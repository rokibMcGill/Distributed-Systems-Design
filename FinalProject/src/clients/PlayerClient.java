package clients;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import FaultToleranceDPSS.DPSS_FT;
import helper.Constants;
import log.MyLogger;

public class PlayerClient {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	String userName;
	String Password;
	String string_ip;
	int ipaddress;
	String playerType; // new user and creat account 2; existing user 0
	
	Scanner input = new Scanner(System.in);

	public PlayerClient(String ip) {
		string_ip = ip;
		playerType = "2";
	}

	public PlayerClient(String uname, String pword, String ip) {
		userName = uname;
		Password = pword;
		string_ip = ip;
		playerType = "0";
	}
	
	public void performOperations(DPSS_FT eventObj, String operation) throws NotFound, CannotProceed, InvalidName, IOException{
		ipaddress = Utils.getIntegerIpaddress(string_ip);
		String serverName = Utils.decideServerPort(ipaddress);
		
		String fname, lname, uname, pword, st_ip;
		int age = 0;
		boolean validusername;
		
		if(operation.equals("0")) {
			validusername =  eventObj.logIn(userName, Password, string_ip);
			if(validusername) {
				setupLogging();
				System.out.println("Player Login Successful : " + userName);
				LOGGER.info("User"+ "[ " + userName + " ]" + " LogIn Successfully, " + " Sever"+ "[ " + serverName + " ]");
				operation = displayMenuC();
				performOperations(eventObj, operation);
			} else {
				System.out.println("Login Fail: Invalid User Name or Password");
				operation = displayMenuB();
				performOperations(eventObj, operation);
			}
		} else if(operation.equals("1")) {
			System.out.print("Please Enter Your User Name : ");
		    uname = input.next();
		    userName = uname;
		    System.out.print("Please Enter Your Password : ");
		    pword = input.next();
		    Password = pword;
		    System.out.print("Please enter your IPAddress : ");
		    st_ip = input.next();
		    while(!Utils.isValidIP(st_ip)) {
		    	System.out.print("Please enter Currect IPAddress : ");
		    	st_ip = input.next();
		    }
		    string_ip = st_ip;
			validusername =  eventObj.logIn(userName, Password, string_ip);
			if(validusername) {
				setupLogging();
				System.out.println("Player Login Successful : " + userName);
				LOGGER.info("User"+ "[ " + userName + " ]" + " LogIn Successfully, " + " Sever"+ "[ " + serverName + " ]");
				operation = displayMenuC();
				performOperations(eventObj, operation);
			} else {
				System.out.println("Login Fail: Invalid User Name or Password");
				System.out.println("Please Creat a Accont:");
				operation = displayMenuB();
				performOperations(eventObj, operation);
			}
			
		} else if(operation.equals("2")) {
			System.out.println("To Creat an Account Please Provide Following Information:");
			System.out.print("Please Enter Your First Name : ");
		    fname = input.next();
		    System.out.print("Please Enter Your Last Name : ");
		    lname = input.next();    
		    
		    boolean valid = false;
		    while(!valid) {    	
		    	try {
		    		System.out.print("Please Enter Your Age : ");
		    		age = input.nextInt();
		    		if(age>=1)  valid = true;
		    	} catch (InputMismatchException e) {
		    		System.out.println("Age Shuold Be An Integer And Positive");
		    		input.next();        
		    	}
		    }
		    
		    System.out.print("Please Chose a User Name : ");
		    uname = input.next();
		    while(!checkUserNameLenght(uname)) {
		    	System.out.println("Username is Too Short or Too Long ");
		    	System.out.print("Please Select Usrename Between 6 and 16 Character: ");
		    	uname = input.next();
		    }
		    userName = uname;
		    System.out.print("Please Chose Password: ");
		    pword = input.next();
		    while(!checkPasswordLenght(pword)) {
		    	System.out.println("Pasword is Too Short ");
		    	System.out.print("Please Select Password Larger Than 5  Character: ");
		    	pword = input.next();
		    }
		    Password = pword;

		    while(!eventObj.createPlayerAccount(fname, lname, age, uname, pword, string_ip)) {	
		    		System.out.println("This User Name is Already Used, Please Try Another One: ");
		    		System.out.print("Please Chose a User Name : ");
		    		uname = input.next();
		    	}
		    userName = uname;
		    setupLogging();
		    LOGGER.info("Player's Account Successfully Created :" + "User"+ "[ " + userName + " ]");
		    System.out.println("Login Player Account Successfully Created  as User Name: " + uname);
		    operation = displayMenuC();
		    performOperations(eventObj, operation);
		} else	if(operation.equals("3")) {
			eventObj.logOut(userName);
			LOGGER.info("User"+ "[ " + userName + " ]" + " LogOut Successfully, " + " Sever"+ "[ " + serverName + " ]");
			System.out.println("Player LogOut Successfully");
			Clients.startSystem(eventObj);
		} else	if(operation.equals("4")) {
			String newipaddress;
			System.out.print("Please Enter Your New IPAddress : ");
			newipaddress = input.next();
		    while(!Utils.isValidIP(newipaddress)) {
		    	System.out.print("Please Enter Currect New IPAddress : ");
		    	newipaddress = input.next();
		    }
		    
			if(eventObj.transferAccount(userName, Password, string_ip, newipaddress))
			{
				string_ip = newipaddress;
				ipaddress = Utils.getIntegerIpaddress(string_ip);
				serverName = Utils.decideServerPort(ipaddress);
			System.out.println("Account Has Been Transfered to new IP Address : ");
			LOGGER.info("User"+ "[ " + userName + " ]" + " Account Transfer Successfully, " + " To New Sever"+ "[ " + newipaddress + " ]");
			} else {
				System.out.println("Account Has NOT Been Transfered to new IP Address : ");	
			}
			operation = displayMenuC();
			performOperations(eventObj, operation);
		}	else {
			Clients.startSystem(eventObj);
		}
		
	}
		
	private boolean checkPasswordLenght(String pword) {
		char[] ch = pword.toCharArray();
		if(ch.length <=5 ) {
			return false;
		}
		return true;	
	}
	
	
	private boolean checkUserNameLenght(String username) {
		char[] ch = username.toCharArray();
		if(ch.length <=5 || ch.length >= 17) {
			return false;
		}
		return true;	
	}
	
//	private int displayMenuA() {
//        System.out.println("--------------------------------");
//        System.out.println("|	Available Operations |");
//        System.out.println("--------------------------------");
//        System.out.println("|5| Please LogOut and LogIn Again to Perform more Operation.");
//        System.out.print("Input your operation number : ");
//        return input.nextInt();
//    }
	
	private String displayMenuB() {
		String inputoperation = null;
		boolean isvalidinputoperation = true;
		while(isvalidinputoperation) {
        System.out.println("--------------------------------");
        System.out.println("|	Available Operations |");
        System.out.println("--------------------------------");
        System.out.println("|1| Re-LogIn ");
        System.out.println("|2| If You Are a New User, Creat an Accout");
        System.out.println("|5| Want To Leave");
        System.out.print("Input your operation number : ");
        inputoperation = input.next();
        if(inputoperation.equals("1") || inputoperation.equals("2") || inputoperation.equals("5")) {
        	isvalidinputoperation = false;
        }  else {
        	isvalidinputoperation = true;
        	System.out.println("WOOPS:...You Selected Wrong operation number:");
        }
		}
        return inputoperation;
    }
	
	private String displayMenuC() {
		String inputoperation = null;
		boolean isvalidinputoperation = true;
		while(isvalidinputoperation) {
		System.out.println("--------------------------------");
        System.out.println("| Available Operations |");
        System.out.println("--------------------------------");
        System.out.println("|3| LogOut");
        System.out.println("|4| Tranfer Account");
        System.out.print("Input your operation number : ");
        inputoperation = input.next();
        	if(inputoperation.equals("3") || inputoperation.equals("4") ) {
        		isvalidinputoperation = false;
        	}  else {
        		isvalidinputoperation = true;
        		System.out.println("WOOPS:...You Selected Wrong operation number:");
        	}
		}
        
        return inputoperation;
    }

		
	private void setupLogging() throws IOException {
        File files = new File(Constants.PLAYER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.PLAYER_LOG_DIRECTORY + userName + ".log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
		
	}
	


