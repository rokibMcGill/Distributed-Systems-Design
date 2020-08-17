package rmOne;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {
	
	public static String decideServerPort(int ip) {
        String serverName = null;
        if(ip == 132) {
            serverName = "NA";
        }else if(ip == 93) {
        	serverName = "EU";
        }else if(ip == 182) {
        	serverName = "AS";
        }else {
            System.out.println("This Is an Invalid Request. Please Check Your IPAddress");
        }
        return serverName;
    }
	
//	public static boolean isValidIP(String ipstring) {  
//		int ip = getIntegerIpaddress(ipstring);
//        if(ip == 132 || ip == 93 || ip == 182) return true;
//        return false;
//    }
//	
//	
	public static int getIntegerIpaddress(String ip2) {
		int intIp;
        String[] int_ip = ip2.split("\\.");
        intIp =  Integer.parseInt(int_ip[0]);
		return intIp;
	}
	
	public static byte[] objectToByteArray(Object obj) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteOut.toByteArray();
    }

    public static Object byteArrayToObject(byte[] data) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        Object result = null;
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(byteIn);
            result = (Object) in.readObject();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
