package engine;

import storage.*;

public class PlayerController {
    private AppData data;
    private StorageManager storageManager;

    /**
     * Initializes a new PlayerController.
     * @param data The App Data.
     * @param storageManager The App Storage Manager.
     */
    public PlayerController(AppData data, StorageManager storageManager) {
        this.data = data;
        this.storageManager = storageManager;
    }

    // General

    /**
     * Advances the turn for the next non-bankrupt player.
     */
    public void advanceTurn() {
        data.turnPlayerId++;
        if (data.turnPlayerId > data.players.size()) {
            data.turnPlayerId = 1;
        }
        try {
            if (isBankrupt(data.turnPlayerId)) {
                advanceTurn();
            } else {
                selectPlayer(data.turnPlayerId);
            }
        } catch (DoNotPassGoBankerException e) {}
    }

    /**
     * Checks if it's a player's turn.
     * @param playerId The player ID.
     * @return True if is. False if not.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public boolean isInTurn(int playerId) throws DoNotPassGoBankerException {
        getPlayerOrThrow(playerId, false);
        return playerId == data.turnPlayerId;
    }

    /**
     * Selects a player.
     * @param playerId The player ID.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    public void selectPlayer(int playerId) throws DoNotPassGoBankerException {
        getPlayerOrThrow(playerId);
        data.selectedPlayerId = playerId;
        storageManager.markAsChanged();
    }

    /**
     * Checks if the player is currently selected.
     * @param playerId The player ID.
     * @return True if is. False if not.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public boolean isSelected(int playerId) throws DoNotPassGoBankerException {
        getPlayerOrThrow(playerId, false);
        return playerId == data.selectedPlayerId;
    }

    /**
     * Gets the selected player's id.
     * @return The ID of the selected player.
     */
    public int getSelectedPlayerId() {
        return data.selectedPlayerId;
    }

    /**
     * Bankrupts the player.
     * @param playerId The player ID.
     * @return False if there is only one player left. True if the game should continue.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player was already bankrupt.
     */
    public boolean bankruptPlayer(int playerId) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId);
        player.bankrupt = true;
        data.playersLeft--;
        return data.playersLeft > 1;
    }

    /**
     * Checks if a player is bankrupt.
     * @param playerId The player ID.
     * @return True if the player is bankrupt. False if not.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public boolean isBankrupt(int playerId) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId, false);
        return player.bankrupt;
    }

    // Player

    /**
     * Creates a new player.
     * @param id UNIQUE id of the player.
     * @param name Name of the player.
     */
    public void newPlayer(int id, String name) {
        data.players.put(id, new Player(id, name, data.settings.startMoney));
        data.playersLeft++;
        storageManager.markAsChanged();
    }

    /**
     * Gets a player. Throws exception if not found or player is bankrupt.
     * @param id The player ID.
     * @return The player.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    private Player getPlayerOrThrow(int id) throws DoNotPassGoBankerException {
        var player = data.players.get(id);
        if (player == null) throw new DoNotPassGoBankerException("Player with id " + id + " not found.");
        if (player.bankrupt) throw new DoNotPassGoBankerException("Player " + id + " " + player.name + " is bankrupt.");
        return player;
    }

    /**
     * Gets a player. Throws exception if not found.
     * @param id The player ID.
     * @param throwForBankrupt True: throws also if the player is bankrupt. False: doesn't throw for the player being bankrupt.
     * @return The player.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    private Player getPlayerOrThrow(int id, boolean throwForBankrupt) throws DoNotPassGoBankerException {
        var player = data.players.get(id);
        if (player == null) throw new DoNotPassGoBankerException("Player with id " + id + " not found.");
        if (throwForBankrupt && player.bankrupt) throw new DoNotPassGoBankerException("Player " + id + " " + player.name + " is bankrupt.");
        return player;
    }

    /**
     * Gets the name of a player.
     * @param playerId The player ID.
     * @return The name of the player.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public String getName(int playerId) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId, false);
        return player.name;
    }

    // Money (between players and bank)

    /**
     * Adds money (e.g. from the bank) for a player.
     * @param playerId The player ID.
     * @param amount Amount of the money to add.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    public void addMoney(int playerId, int amount) throws DoNotPassGoBankerException {
        Player player = getPlayerOrThrow(playerId);
        player.money += amount;
        storageManager.markAsChanged();
    }

    /**
     * Adds money for the player for passing go.
     * @param playerId The player ID.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    public void addGoMoney(int playerId) throws DoNotPassGoBankerException {
        Player player = getPlayerOrThrow(playerId);
        player.money += data.settings.goMoney;
        storageManager.markAsChanged();
    }

    /**
     * Substracts money (e.g. to the bank) from a player.
     * @param playerId The player ID.
     * @param amount Amount of the money to substract.
     * @return True if successful. False if the player hasn't got enaugh money.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public boolean subtractMoney(int playerId, int amount) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId, false);

        if (!hasMoney(player, amount)) return false;

        player.money -= amount;
        storageManager.markAsChanged();

        return true;
    }

    /**
     * Transfers money from a player to another.
     * @param fromPlayerId The player ID of the player to transfer from.
     * @param toPlayerId The player ID of the player to transfer to.
     * @param amount Amount of the money to transfer.
     * @return True if successful. False if the player to transfer the money from hasn't got enaugh money.
     * @throws DoNotPassGoBankerException If a player wasn't found or the player to transfer to is bankrupt.
     */
    public boolean transferMoney(int fromPlayerId, int toPlayerId, int amount) throws DoNotPassGoBankerException {
        var toPlayer = getPlayerOrThrow(toPlayerId);
        var fromPlayer = getPlayerOrThrow(fromPlayerId, false);

        if (!hasMoney(fromPlayer, amount)) return false;

        fromPlayer.money -= amount;
        toPlayer.money += amount;
        storageManager.markAsChanged();

        return true;
    }

    /**
     * Gets the amount of money the player has as a formated string.
     * @param playerId The player ID.
     * @return The amount of money the player has as a formated string.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public String getMoneyStr(int playerId) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId, false);
        return String.format("%,d", player.money);
    }

    /**
     * Gets the amount of money the player has.
     * @param playerId The player ID.
     * @return The amount of money the player has.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public int getMoney(int playerId) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId, false);
        return player.money;
    }

    /**
     * Checks if the player has atleast the amount of money.
     * @param playerId The player ID.
     * @param amount The amount of money to check for.
     * @return True if the player has atleast the amount of money. False if not.
     * @throws DoNotPassGoBankerException If the player wasn't found.
     */
    public boolean hasMoney(int playerId, int amount) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId, false);
        return hasMoney(player, amount);
    }

    /**
     * Checks if the player has atleast the amount of money.
     * @param player The player.
     * @param amount The amount of money to check for.
     * @return True if the player has atleast the amount of money. False if not.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    public boolean hasMoney(Player player, int amount) {
        return amount <= player.money;
    }

    // Free Parking

    /**
     * Transfer money from a player to free parking.
     * @param playerId The player ID.
     * @param amount Amount of the money to transfer.
     * @return True if successful. False if the player hasn't got enaugh money.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    public boolean transferMoneyToFreeParking(int playerId, int amount) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId);
        
        if (!hasMoney(player, amount)) return false;

        player.money -= amount;
        data.freeParking.money += amount;
        storageManager.markAsChanged();

        return true;
    }

    /**
     * Transfer all the money from free parking to a player.
     * @param playerId The player ID.
     * @throws DoNotPassGoBankerException If the player wasn't found or the player is bankrupt.
     */
    public int takeMoneyFromFreeParking(int playerId) throws DoNotPassGoBankerException {
        var player = getPlayerOrThrow(playerId);

        int moneyInFreeParking = data.freeParking.money;

        if (moneyInFreeParking > 0) {
            player.money += data.freeParking.money;
            data.freeParking.money = 0;
            storageManager.markAsChanged(); 
        }
        
        return moneyInFreeParking;
    }

    /**
     * Checks if there is any money in free parking.
     * @return True if there is money in free parking. False if not.
     */
    public boolean freeParkingHasMoney() {
        return data.freeParking.money > 0;
    }
}
