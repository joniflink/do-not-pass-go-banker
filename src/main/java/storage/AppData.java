package storage;

import java.util.TreeMap;

public class AppData {
    public TreeMap<Integer, Player> players = new TreeMap<>();
    public FreeParking freeParking;
    public Settings settings;
    public int turnPlayerId = 1;
    public int selectedPlayerId = 1;
    public int playersLeft = 0;

    public AppData(int startMoney, int goMoney, boolean freeParkingEnabled) {
        settings = new Settings(startMoney, goMoney, freeParkingEnabled);
        if (freeParkingEnabled) freeParking = new FreeParking();
    }
}
