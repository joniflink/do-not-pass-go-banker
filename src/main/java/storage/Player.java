package storage;

public class Player {
    public final int id;
    public final String name;
    public int money;
    public boolean bankrupt = false;

    /**
     * Initializes a new player.
     * @param name The unique nametag given to this player.
     * @param money The amount of money this player starts the game with.
     */
    public Player(int id, String name, int money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }
}
