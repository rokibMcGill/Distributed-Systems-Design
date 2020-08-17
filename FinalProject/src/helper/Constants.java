package helper;

public class Constants {
	
	public static final int PORT_FE_TO_RM = 1412;
	public static final int PORT_RM_TO_FE = 1413;
	public static final int PORT_SEC = 1333;
	
	public static final String IP_FE_TO_RM = "230.1.1.10";
	public static final String IP_RM_TO_FE = "230.1.1.5";
	
	
	public static final String OP_CHECK_USER_NAME = "isUserNameExist";
    public static final String OP_PLAYER_UPDATE = "getPlayerUpdateForCurrentServer";
    public static final String OP_SUSPEND_ACCOUNT = "suspendAccountFromCurrecntServer";
    public static final String OP_TRANSFER_ACCOUNT = "createAccount";
    
    
    public static final String ADMIN_LOG_DIRECTORY = "logResult/admin/";
    public static final String PLAYER_LOG_DIRECTORY = "logResult/player/";
    public static final String SERVER_LOG_DIRECTORY = "logResult/server/";
    
//   public static final String SERVER_LOG_DIRECTORY_RM1 = "rmOne/logResult/server/";

}
