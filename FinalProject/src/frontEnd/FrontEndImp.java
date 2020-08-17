package frontEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.omg.CORBA.ORB;
import FaultToleranceDPSS.DPSS_FTPOA;
import UDP.UDPClient;
import helper.Constants;

public class FrontEndImp extends DPSS_FTPOA{
	final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private ORB orb;
	public ArrayList<String> messagelist = new ArrayList<String>();
	String ipAddress;
	String serverName;
	
	public FrontEndImp() {
		super();
	}
	
	public void setORB(ORB orb_val) {
        orb = orb_val;
    }


	@Override
	public boolean logIn(String userName, String password, String IP) {
		ipAddress = IP;
		int ipaddress = Utils.getIntegerIpaddress(ipAddress);
		serverName = Utils.decideServerPort(ipaddress);
		
		String input_message = serverName+":"+"logIn"+":"+userName+":"+password+":"+IP+":";
		sendMessage(input_message.trim());
		waitForResponse();
		String send_username = userName;
		String result = receiveMessage(send_username, input_message); 
		boolean sendMessageToClient = false;
		if(result.equals("true")) {
			LOGGER.info("LogIN: " + " Server[ " + serverName +" ]" + " user Name: " + userName);
			sendMessageToClient = true;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		else {
			sendMessageToClient = false;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		
		return sendMessageToClient; 
	}

	@Override
	public boolean createPlayerAccount(String firstName, String lastName, int Age, String userName, String Password,
			String IP) {
		ipAddress = IP;
		int ipaddress = Utils.getIntegerIpaddress(ipAddress);
		serverName = Utils.decideServerPort(ipaddress);
		
		String input_message = serverName+":"+"createPlayerAccount"+":"+firstName+":"+lastName
				+":"+String.valueOf(Age)+":"+userName+":"+Password+":"+IP+":";
		sendMessage(input_message.trim());
		waitForResponse();
		String send_username = userName;
		String result = receiveMessage(send_username, input_message);
		boolean sendMessageToClient = false;
		if(result.equals("true")) {
			 LOGGER.info("Player's Account Successfully Created :" + "User"+ "[ " + userName + " ]");
			sendMessageToClient = true;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		else {
			sendMessageToClient = false;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		
		return sendMessageToClient; 
	}

	@Override
	public boolean logOut(String userName) {
		String input_message = serverName+":"+"logOut"+":"+userName+":";
		sendMessage(input_message.trim());
		waitForResponse();
		String send_username = userName;
		String result = receiveMessage(send_username, input_message); 
		boolean sendMessageToClient = false;
		if(result.equals("true")) {
			LOGGER.info("User"+ "[ " + userName + " ]" + " LogOut Successfully, " + " Sever"+ "[ " + serverName + " ]");
			sendMessageToClient = true;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		else {
			sendMessageToClient = false;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		return sendMessageToClient; 
		
	}

	@Override
	public String[] getPlayerUpdate(String adminId) {
		String input_message = serverName+":"+"getPlayerUpdate"+":"+adminId+":";
		sendMessage(input_message.trim());
		waitForResponse();
		String send_username = adminId;
		String result = receiveMessage(send_username, input_message);
		String[] sendMessageToClient = result.split("\\?");
		System.out.println("Reply to Client: " + sendMessageToClient[0]+" "+ sendMessageToClient[1]+ " "+ sendMessageToClient[2]);
		System.out.println("  ");
		return sendMessageToClient;
	}

	@Override
	public boolean suspendAccount(String adminId, String adminPword, String adminIp, String suspendAccount) {
		String input_message = serverName+":"+"suspendAccount"+":"+adminId+":"+adminPword+":"+adminIp+":"+suspendAccount+":";
		sendMessage(input_message.trim());
		waitForResponse();
		String send_username = adminId;
		String result = receiveMessage(send_username, input_message); 
		boolean sendMessageToClient = false;
		if(result.equals("true")) {
			LOGGER.info("Admin "  + adminId + " Invoke Player Update from server : " + serverName );
			sendMessageToClient = true;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		else {
			sendMessageToClient = false;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		return sendMessageToClient; 
	}

	@Override
	public boolean transferAccount(String userName, String Password, String oldIp, String newIp) {
		String input_message = serverName+":"+"transferAccount"+":"+userName+":"+Password+":"+oldIp+":"+newIp+":";
		sendMessage(input_message.trim());
		waitForResponse();
		String send_username = userName;
		String result = receiveMessage(send_username, input_message);
		boolean sendMessageToClient = false;
		if(result.equals("true")) {
			sendMessageToClient = true;
			ipAddress = newIp;
			int ipaddress = Utils.getIntegerIpaddress(ipAddress);
			serverName = Utils.decideServerPort(ipaddress);
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
			LOGGER.info("User"+ "[ " + userName + " ]" + " Account Transfer Successfully, " + " To New Sever"+ "[ " + serverName + " ]");
		}
		else {
			sendMessageToClient = false;
			System.out.println("Reply to Client: " + sendMessageToClient);
			System.out.println("  ");
		}
		return sendMessageToClient; 
	}
	
	
	public void waitForResponse(){
		try {
            System.out.println("waiting for result...");
            Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String message) {
		UDPClient.sendUDPMessageToSequence(message, Constants.PORT_SEC);
	}

private String receiveMessage(String userid, String input_message) {
			CountFault fc = new CountFault();
			String sendmessagetoclient= null;
			boolean ismessagecorrect = true;
		
	while(ismessagecorrect) {
			waitForResponse();	
			if(messagelist.size()> 3) {
				List<String> distinctmessagelist = messagelist.stream().distinct().collect(Collectors.toList());
				messagelist.clear();
				messagelist.addAll(distinctmessagelist);
				}
				
			ArrayList<String> mlist_by_user = new ArrayList<String>();
			for (String data: messagelist) {
				if (data.contains(userid)) mlist_by_user.add(data);
			}
			
			if(mlist_by_user.size() == 3) {
			String[] msgarray1 =  mlist_by_user.get(0).split(";");
			String[] msgarray2 =  mlist_by_user.get(1).split(";");
			String[] msgarray3 =  mlist_by_user.get(2).split(";");
			
			if(msgarray1[1].equals(msgarray2[1]) && msgarray1[1].equals(msgarray3[1])) {
				sendmessagetoclient = msgarray1[1];
				messagelist.remove(mlist_by_user.get(0));
				messagelist.remove(mlist_by_user.get(1));
				messagelist.remove(mlist_by_user.get(2));
				ismessagecorrect = false;
			} else if (msgarray1[1].equals(msgarray2[1]) ){
				messagelist.remove(mlist_by_user.get(2));
				fc.setFaultCount(Integer.parseInt(msgarray3[2]));
				int cf = fc.getFaultCount(Integer.parseInt(msgarray3[2])); 
					String newinputmessage =  msgarray3[0]+";"+"fault"+";"+msgarray3[2]+";"+cf;
					System.out.println("Number of Fault: " + msgarray3[2] 
							+ ":" + cf);
					UDPClient.sendUDPMessageToRM(newinputmessage);	
					waitForResponse();
			} else if (msgarray1[1].equals(msgarray3[1]) ){
				messagelist.remove(mlist_by_user.get(1));
				fc.setFaultCount(Integer.parseInt(msgarray2[2]));
				int cf = fc.getFaultCount(Integer.parseInt(msgarray2[2])); 
					String newinputmessage =  msgarray2[0]+";"+"fault"+";"+msgarray2[2]+";"+cf;
					System.out.println("2A. Number of Fault: " + msgarray2[2] 
							+ ":" + cf);
					UDPClient.sendUDPMessageToRM(newinputmessage);
					waitForResponse();
			} else if (msgarray2[1].equals(msgarray3[1]) ){			
				messagelist.remove(mlist_by_user.get(0));
				fc.setFaultCount(Integer.parseInt(msgarray1[2]));
				int cf = fc.getFaultCount(Integer.parseInt(msgarray1[2])); 
					String newinputmessage =  msgarray1[0]+";"+"fault"+";"+msgarray1[2]+";"+cf;
					System.out.println("Number of Fault: " + msgarray1[2] 
							+ ":" + cf);
					UDPClient.sendUDPMessageToRM(newinputmessage);
					waitForResponse();
			} else {
				messagelist.remove(mlist_by_user.get(0));
				messagelist.remove(mlist_by_user.get(1));
				messagelist.remove(mlist_by_user.get(2));
				fc.setFaultCount(Integer.parseInt(msgarray1[2]));
				int cf1 = fc.getFaultCount(Integer.parseInt(msgarray1[2]));
				String newinputmessage1 =  msgarray1[0]+";"+"fault"+";"+msgarray1[2]+";"+cf1;
				UDPClient.sendUDPMessageToRM(newinputmessage1);
				waitForResponse();
				fc.setFaultCount(Integer.parseInt(msgarray2[2]));
				int cf2 = fc.getFaultCount(Integer.parseInt(msgarray2[2]));
				String newinputmessage2 =  msgarray2[0]+";"+"fault"+";"+msgarray2[2]+";"+cf2;
				UDPClient.sendUDPMessageToRM(newinputmessage2);
				waitForResponse();
				fc.setFaultCount(Integer.parseInt(msgarray3[2]));
				int cf3 = fc.getFaultCount(Integer.parseInt(msgarray3[2]));
				String newinputmessage3 =  msgarray3[0]+";"+"fault"+";"+msgarray3[2]+";"+cf3;
				UDPClient.sendUDPMessageToRM(newinputmessage3);
				waitForResponse();
			}
			} else {  // if any RM does not response
				String[] msgarray =  input_message.split(":");
				ismessagecorrect = false;
				messagelist.clear();
				if(msgarray[1].equals("getPlayerUpdate")) {
				sendmessagetoclient = "EU: 0 online, 0 offline.?NA: 0 online,  0 offline.?AS:  0 online,  0 offline.";
				} else {
				sendmessagetoclient = "false";
				}
				System.out.println("System Got Problem Please Sbbmit Your Job Again");
			}
	}
			
		return sendmessagetoclient;
}
	
		
	@Override
	public void shutdown() {
		orb.shutdown(false);
	}
}
