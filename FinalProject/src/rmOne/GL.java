package rmOne;

public enum GL {

	NA(6664), EU(7774), AS(8884);
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
