package rmOne;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import helper.Constants;

public class GameServerRM1 {
	final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	String serverName;
	private GL GL;
	
	private HashMap<String, ArrayList<ArrayList<String>>> glDatabase = new  HashMap<>();
	private boolean checkFalutTolarence1 = true;
	private boolean checkFalutTolarence2 = true;
	
	private int faultCount = 0;
	
	public GameServerRM1(String serverName) {
		this.GL = rmOne.GL.valueOf(serverName);
		this.serverName = serverName;
	}

public boolean logIn(String userName, String Password, String ip){
	if(isUserNameExist(userName)) {
	ArrayList<String> value =  getAccountInfo(userName);
	if(value.get(4).equals(Password) && value.get(5).equals(ip)) 
			{
				changeStatus(userName, 1);
					if(checkFalutTolarence1 && userName.equals("soma2000")) {
						checkFalutTolarence1 = false;
						return false;
					} else if (userName.equals("robi1000")) {
						faultCount++;
						if (faultCount == 4) { 
							faultCount = 0;
							return true;
						}else {
							return false;
						}
					} else {
						return true;
					}
//				return true;
			}
		
		LOGGER.info("LogIN: " + " Server[ " + this.GL +" ]" + " user Name: " + userName);
	}
		return false;
	}
	
	
	public boolean createPlayerAccount(String firstName, String lastName, int Age, String userName, String Password,
			String IPAddress) {
		boolean isusernameexits = isUserNameExist(userName);
		ArrayList<Boolean> reponcelist = new ArrayList<>();
		reponcelist.add(isusernameexits);
		Object result;
		for (GL gl : rmOne.GL.values()) {
            if (gl != this.GL) { 
            	byte[] message = UDPClient(gl, userName, "isUserNameExist");
            	result =   Utils.byteArrayToObject(message);
            	reponcelist.add((Boolean) result);
            }
        }
		boolean isuniquserid = true; 
		for(Boolean bol : reponcelist) {
			if(bol) { 
				isuniquserid = false;
				break;
			}
		}		
		if(isuniquserid == false) {
			return false;
		} else {		
		ArrayList<String> clientdata = new ArrayList<String>();
		// synchronizing the write operation to the in-memory database
        synchronized (this) {
		clientdata.add(firstName);
		clientdata.add(lastName);
		clientdata.add(String.valueOf(Age));
		clientdata.add(userName);
		clientdata.add(Password);
		clientdata.add(String.valueOf(IPAddress));
		clientdata.add(String.valueOf(1));
        }
		char[] ch = userName.toCharArray();
		ArrayList<ArrayList<String>> dblist = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
	    if(dblist != null) {
	    	dblist.add(clientdata);
	    	synchronized (this) {
	    	glDatabase.put(String.valueOf(ch[0]).toUpperCase(), dblist);
	    	}
	    } else {
	    	glDatabase.put(String.valueOf(ch[0]).toUpperCase(), new ArrayList<ArrayList<String>>());
	    	dblist = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
	    	dblist.add(clientdata);
	    	synchronized (this) {
	    	glDatabase.put(String.valueOf(ch[0]).toUpperCase(), dblist);
	    	}
	    }
	    LOGGER.info("New Account Created " + " Server[ " + this.GL +" ]" + " user Name: " + userName);
		return true;
		}
	}
	
	public boolean logOut(String username) {
		changeStatus(username, 0);
		LOGGER.info("LogOUT: " + " Server[ " + this.GL +" ]" + " user Name: " + username);
		return true;
		
	}
	
	@SuppressWarnings({ "unchecked" })
	public String[] getPlayerUpdate(String adminId) {
		String pl;
		String[] plresult = new String[3];
		ArrayList<Integer> loglist;
		loglist = getPlayerUpdateForCurrentServer(adminId);
		pl = this.GL.toString()+":  " +loglist.get(0) + " online,  "+loglist.get(1) + " offline.";
		plresult[0] = pl.trim();
		
		Object result;
		int i = 1;
		for (GL gl : rmOne.GL.values()) {
            if (gl != this.GL) { 
            	byte[] message = UDPClient(gl, adminId, "getPlayerUpdateForCurrentServer");
            	result =   Utils.byteArrayToObject(message);
            	loglist = (ArrayList<Integer>) result;
            	pl = gl.toString()+":  " +loglist.get(0) + " online,  "+loglist.get(1) + " offline.";
            	plresult[i] = pl.trim();
            	i++;
            }
        }
		
		if(checkFalutTolarence2 ) {
			checkFalutTolarence2 = false;
			String[] plresult1 = new String[3]; 
			plresult1[0] = plresult[0];
			plresult1[1] = null;
			plresult1[2] = plresult[2];
			return plresult1;
		} 
		return plresult;
	}
	
	public ArrayList<Integer> getPlayerUpdateForCurrentServer(String adminId){
		int login = 0;
		int logout = 0;
		Collection<ArrayList<ArrayList<String>>> collection = glDatabase.values();
		ArrayList<Integer> loglist = new ArrayList<>();
		for(ArrayList<ArrayList<String>> dblist : collection) {
			for(List<String> db : dblist) {
				if(db.get(db.size()-1).equals(String.valueOf(1))) {
					login ++;
				} else {
					logout++;
				}
			}
		}
		loglist.add(login);
		loglist.add(logout);
		return loglist;
	}
	
	
	public void changeStatus(String username, int status) {
		char[] ch = username.toCharArray();
		ArrayList<ArrayList<String>> collection = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
		ArrayList<ArrayList<String>> updatedcollection = new ArrayList<ArrayList<String>>();
		ArrayList<String> clientdata = new ArrayList<String>();
		
		for(ArrayList<String> dblist : collection) {
				if(username.equals(dblist.get(3))) {
					for (int i = 0; i< dblist.size()-1; i++) {
						clientdata.add(dblist.get(i));	
					}
					clientdata.add(dblist.size()-1, String.valueOf(status));
				} else {
					updatedcollection.add(dblist);
				}
		}
		updatedcollection.add(clientdata);
		synchronized (this) {
		glDatabase.replace((String.valueOf(ch[0]).toUpperCase()), collection, updatedcollection);
		}
	}
	
	
	public boolean isUserNameExist(String username) {
		Collection<ArrayList<ArrayList<String>>> collection = glDatabase.values();	
		if (collection.isEmpty()) return false;
		for(ArrayList<ArrayList<String>> dblist : collection) {
			for(List<String> db : dblist) {
				if(username.equals(db.get(3))) return true;
			}
		}
		return false;
	}
	
	
	public boolean suspendAccount(String adminId, String adminPword, String adminIp, String suspendAccount)
			{
		ArrayList<Boolean> susspendedlist = new ArrayList<>();
		boolean susspended = suspendAccountFromCurrecntServer(suspendAccount);
		susspendedlist.add(susspended);
		Object result;
		for (GL gl : rmOne.GL.values()) {
            if (gl != this.GL) { 
            	byte[] message = UDPClient(gl, suspendAccount, "suspendAccountFromCurrecntServer");
            	result = Utils.byteArrayToObject(message);
            	susspendedlist.add((Boolean) result);
            }
        }
		    	
		boolean hassuspended = false; 
		for(Boolean bol : susspendedlist) {
			if(bol) { 
				hassuspended = true;
				break;
			}
		}
		
		if(hassuspended == true) {
			LOGGER.info("Account Suspected: " + "From Server[ " + this.GL +" ]" + " by Admin ID : " + adminId);
			return true;
		} else {
			return false;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean suspendAccountFromCurrecntServer(String suspendAccount) {
		char[] ch = suspendAccount.toCharArray();
		ArrayList<String> value =  new ArrayList<String>();
		ArrayList<ArrayList<String>> db = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
		
		if(db != null) {
		for(ArrayList<String> d: db) {
			if (d.contains(suspendAccount)) value =  (ArrayList<String>) d.clone();
		} 	
		}
		if(!value.isEmpty()) glDatabase.get(String.valueOf(ch[0]).toUpperCase()).remove(value);
		if(!value.isEmpty()) {
			System.out.println("Account Found");
			return true;
		} else {
			System.out.println("No Account");
			return false;
		}	
	}

	
	public boolean transferAccount(String userName, String Password, String oldIp, String newIp) {
		ArrayList<String> value =  getAccountInfo(userName);
		char[] ch = userName.toCharArray();
		if(!value.isEmpty()) glDatabase.get(String.valueOf(ch[0]).toUpperCase()).remove(value);

		String accountinfo = value.get(0)+" "+value.get(1)+" "+value.get(2).toString()+" "+value.get(3)+" "+value.get(4)+" "+newIp;
		int newipint = Utils.getIntegerIpaddress(newIp);
		String newserverName = Utils.decideServerPort(newipint);			
		Object result;
            	byte[] message = UDPClient(rmOne.GL.valueOf(newserverName), accountinfo.trim(), "createAccount");
            	result =   Utils.byteArrayToObject(message);
        		LOGGER.info("Account Transferd: " + "From Server[ " + oldIp +" ]" + " to Server[ " + newIp +" ]" +" user Name : " + userName);
		return (boolean) result;
	}
	
	public boolean createAccount(String firstName, String lastName, int Age, String userName, String Password,
			String IPAddress) {
		
		ArrayList<String> clientdata = new ArrayList<String>();
		synchronized (this) {
		clientdata.add(firstName);
		clientdata.add(lastName);
		clientdata.add(String.valueOf(Age));
		clientdata.add(userName);
		clientdata.add(Password);
		clientdata.add(String.valueOf(IPAddress));
		clientdata.add(String.valueOf(1));
		}
		char[] ch = userName.toCharArray();
		ArrayList<ArrayList<String>> dblist = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
	    if(dblist != null) {
	    	dblist.add(clientdata);
	    	synchronized (this) {
	    	glDatabase.put(String.valueOf(ch[0]).toUpperCase(), dblist);
	    	}
	    } else {
	    	glDatabase.put(String.valueOf(ch[0]).toUpperCase(), new ArrayList<ArrayList<String>>());
	    	dblist = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
	    	dblist.add(clientdata);
	    	synchronized (this) {
	    	glDatabase.put(String.valueOf(ch[0]).toUpperCase(), dblist);
	    	}
	    }
	    LOGGER.info("New Account Created " + " Server[ " + this.GL +" ]" + " user Name: " + userName);
		return true;
		}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> getAccountInfo(String userName) {
		char[] ch = userName.toCharArray();
		ArrayList<String> value =  new ArrayList<String>();
		ArrayList<ArrayList<String>> db = glDatabase.get(String.valueOf(ch[0]).toUpperCase());
		
		if(db != null) {
		for(ArrayList<String> d: db) {
			if (d.contains(userName)) value =  (ArrayList<String>) d.clone();
		} 	
		}
		
		return value;
	}
	
	private byte[] UDPClient(GL serverGL, String uname, String method) {
        LOGGER.info("Making UPD Socket Call to " + serverGL + " Server for method : " + method);
        // UDP SOCKET CALL AS CLIENT
        HashMap<String, Object> data = new HashMap<>();
        byte[] response = null;
        data.put(method, uname);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
			byte[] message = Utils.objectToByteArray(data);
            InetAddress remoteUdpHost = InetAddress.getLocalHost(); 
            DatagramPacket request = new DatagramPacket(message, message.length, remoteUdpHost, serverGL.getUdpPort());
            socket.send(request);
            byte[] buffer = new byte[65556];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            response = reply.getData();
        } catch (SocketException e) {
            LOGGER.severe("SocketException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("IOException : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }

        return response;
    }
	
	
	public void UDPServer() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(GL.getUdpPort());
            byte[] buffer = new byte[1000];// to stored the received data from the client.
            LOGGER.info(this.GL + " UDP Server Started............");
            // non-terminating loop as the server is always in listening mode.
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // Server waits for the request to come
                socket.receive(request); // request received
                byte[] response = processUDPRequest(request.getData());
                DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(),
                        request.getPort());// reply packet ready
                socket.send(reply);// reply sent
            }
        } catch (SocketException e) {
            LOGGER.severe("SocketException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("IOException : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }
	
	private byte[] processUDPRequest(byte[] data) {

        byte[] response = null;
        @SuppressWarnings("unchecked")
		HashMap<String, Object> request = (HashMap<String, Object>) Utils.byteArrayToObject(data);

        for (String key : request.keySet()) {
            LOGGER.info("Received UDP Socket call for method[" + key + "] with parameters[" + request.get(key) + "]");
            switch (key) {
                case Constants.OP_CHECK_USER_NAME :
                    String username = (String) request.get(key);
                    response = Utils.objectToByteArray(isUserNameExist(username));
                    break;
                case Constants.OP_PLAYER_UPDATE:
                	String adminname = (String) request.get(key);
                    response = Utils.objectToByteArray(getPlayerUpdateForCurrentServer(adminname));
                    break;     
                case Constants.OP_SUSPEND_ACCOUNT:
                	String suspendaccount = (String) request.get(key);
                    response = Utils.objectToByteArray(suspendAccountFromCurrecntServer(suspendaccount));
                    break;
                case Constants.OP_TRANSFER_ACCOUNT:
                	String accinfo = (String) request.get(key);
                	System.out.println("2. Account Info " + accinfo);
                	String[] ac = accinfo.trim().split("\\s");
                	System.out.println("3. Account Info " + ac.length);
                    response = Utils.objectToByteArray(createAccount(ac[0], ac[1], Integer.parseInt(ac[2]), ac[3], ac[4], ac[5]));
                    break;
            }
        }

        return response;
    }

	
	
}
