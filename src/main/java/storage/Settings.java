package storage;

public class Settings {
    public final int startMoney;
    public final int goMoney;
    public final boolean freeParkingEnabled;

    public Settings(int startMoney, int goMoney, boolean freeParkingEnabled) {
        this.startMoney = startMoney;
        this.goMoney = goMoney;
        this.freeParkingEnabled = freeParkingEnabled;
    }
}
