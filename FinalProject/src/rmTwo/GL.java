package rmTwo;

public enum GL {

	NA(6665), EU(7775), AS(8885);
    int udpPort;
    
    private GL(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public static boolean CityExist(String gloc) {
        for (GL g : GL.values()) {
            if (g.toString().equals(gloc.toUpperCase()))
                return true;
        }
        return false;
    }
	
}
