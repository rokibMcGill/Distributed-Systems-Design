package rmThree;

public enum GL {

	NA(6666), EU(7776), AS(8886);
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
