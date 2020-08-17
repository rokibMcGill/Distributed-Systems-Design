package frontEnd;

	public class Utils {
		public static int getIntegerIpaddress(String ip2) {
			int intIp;
			String[] int_ip = ip2.split("\\.");
			intIp =  Integer.parseInt(int_ip[0]);
			return intIp;
	}
	
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

}
