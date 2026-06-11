package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import engine.*;
import storage.*;

public class MainViewController {
    private static final String APP_NAME = "Do Not Pass Go Banker";

    private StorageManager sm;
    private PlayerController pc;
    private ArrayList<Integer> playerIds;
    private AppWindow appWindow;
    private Dice dice = new Dice();

    private boolean gameFinished = false;

    @FXML private MenuItem saveMenuItem;

    @FXML private VBox playerListContainer;
    @FXML private Button nextTurnButton;

    @FXML private Menu advancedMenu;

    @FXML private TabPane mainTabPane;
    @FXML private Tab freeParkingTab;

    @FXML private Label passGoPaidLabel;
    @FXML private Button passGoButton;
    @FXML private MenuItem payGoMoneyAgainMenuItem;
    private boolean payGoMoneyExecuted = false;

    @FXML private TextField getMoneyFromBankField;
    @FXML private Label getMoneyFromBankInfoLabel;
    @FXML private Label getMoneyFromBankDoneLabel;
    @FXML private Button getMoneyFromBankButton;
    @FXML private MenuItem getMoneyFromBankAgainMenuItem;
    @FXML private Button resetGetMoneyFromBankButton;
    private boolean getMoneyFromBankExecuted = false;

    @FXML private TextField payMoneyToBankField;
    @FXML private Label payMoneyToBankInfoLabel;
    @FXML private Label payMoneyToBankDoneLabel;
    @FXML private Button payMoneyToBankButton;
    @FXML private MenuItem payMoneyToBankAgainMenuItem;
    @FXML private Button resetPayMoneyToBankButton;
    private boolean payMoneyToBankExecuted = false;

    @FXML private ComboBox<Integer> transferToBox;
    @FXML private TextField transferToField;
    @FXML private Label transferToInfoLabel;
    @FXML private Label transferToDoneLabel;
    @FXML private Button transferToButton;
    @FXML private MenuItem transferToAgainMenuItem;
    @FXML private Button resetTransferToButton;
    private boolean transferToExecuted = false;
    private final ObservableList<Integer> transferToPlayerList = FXCollections.observableArrayList();

    @FXML private ComboBox<Integer> transferFromBox;
    @FXML private TextField transferFromField;
    @FXML private Label transferFromInfoLabel;
    @FXML private Label transferFromDoneLabel;
    @FXML private Button transferFromButton;
    @FXML private MenuItem transferFromAgainMenuItem;
    @FXML private Button resetTransferFromButton;
    private boolean transferFromExecuted = false;
    private final ObservableList<Integer> transferFromPlayerList = FXCollections.observableArrayList();

    @FXML private TextField putMoneyToFreeParkingField;
    @FXML private Label putMoneyToFreeParkingInfoLabel;
    @FXML private Label putMoneyToFreeParkingDoneLabel;
    @FXML private Button putMoneyToFreeParkingButton;
    @FXML private Menu freeParkingMenu;
    @FXML private MenuItem putMoneyToFreeParkingAgainMenuItem;
    @FXML private Button resetPutMoneyToFreeParkingButton;
    private boolean putMoneyToFreeParkingExecuted = false;

    @FXML private Button takeMoneyFromFreeParkingButton;
    @FXML private Label takeMoneyFromFreeParkingDoneLabel;
    private boolean takeMoneyFromFreeParkingExcecuted = false;

    @FXML private Button declareBankruptcyButton;
    @FXML private Label declareBankruptyInfoLabel;
    private boolean declareBankruptcyNeedsReset = false;

    @FXML private Label twoDicesLabel;
    @FXML private Button resetTwoDicesButton;
    @FXML private Label oneDiceLabel;
    @FXML private Button resetOneDiceButton;

    @FXML
    public void initialize() {
        getMoneyFromBankField.textProperty().addListener((observable, oldValue, newValue) -> {
            getMoneyFromBankExecuted = false;
            getMoneyFromBankAgainMenuItem.setDisable(true);
            getMoneyFromBankDoneLabel.setText("");
            updateGetMoneyInfoText(newValue);
        });

        payMoneyToBankField.textProperty().addListener((observable, oldValue, newValue) -> {
            payMoneyToBankExecuted = false;
            payMoneyToBankAgainMenuItem.setDisable(true);
            payMoneyToBankDoneLabel.setText("");
            updatePayMoneyToBankInfoLabel(newValue);
        });

        putMoneyToFreeParkingField.textProperty().addListener((observable, oldValue, newValue) -> {
            putMoneyToFreeParkingExecuted = false;
            putMoneyToFreeParkingAgainMenuItem.setDisable(true);
            putMoneyToFreeParkingDoneLabel.setText("");
            updatePutMoneyToFreeParkingInfoLabel(newValue);
        });

        transferToBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateTransferToInfoLabel();
        });

        transferToField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTransferToInfoLabel();
        });

        transferFromBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateTransferFromInfoLabel();
        });

        transferFromField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTransferFromInfoLabel();
        });
    }

    public void initData(PlayerController playerController, StorageManager storageManager, ArrayList<Integer> activePlayerIds, AppWindow appWindow) {
        this.sm = storageManager;
        this.pc = playerController;
        this.playerIds = activePlayerIds;
        this.appWindow = appWindow;
        applyHouseRules();
        updateSidebarDisplay();
        updateSaveMenuItemDisable();
        updateWindowTitleIndicator();

        if (gameFinished) nextTurnButton.setDisable(true);

        transferFromBox.setItems(transferToPlayerList);
        configurePlayerChoiceBoxDisplay(transferToBox);
        updatePlayerComboBox(transferToBox, transferToPlayerList);

        transferToBox.setItems(transferFromPlayerList);
        configurePlayerChoiceBoxDisplay(transferFromBox);
        updatePlayerComboBox(transferFromBox, transferFromPlayerList);
    }

    private void updateSidebarDisplay() {
        playerListContainer.getChildren().clear();

        for (Integer playerId : playerIds) {
            try {
                VBox playerBox = new VBox();
                playerBox.setSpacing(4);
                playerBox.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-backgroud-radius: 5;");

                Label inTurnLabel = new Label("IN TURN");
                inTurnLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");

                Label nameLabel = new Label(String.format("%d. %s", playerId, pc.getName(playerId)));
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                Label moneyLabel = new Label("$" + pc.getMoneyStr(playerId));
                moneyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

                playerBox.getChildren().addAll(inTurnLabel, nameLabel, moneyLabel);

                if (pc.isInTurn(playerId) && pc.isSelected(playerId)) {
                    inTurnLabel.setText("IN TURN & SELECTED");
                    playerBox.setStyle(playerBox.getStyle() + "-fx-border-color: #1500ff; -fx-border-width: 2px;");
                } else if (pc.isInTurn(playerId)) {

                } else if (pc.isSelected(playerId)) {
                    inTurnLabel.setText("SELECTED");
                    playerBox.setStyle(playerBox.getStyle() + "-fx-border-color: #1500ff; -fx-border-width: 2px;");
                } else {
                    inTurnLabel.setVisible(false);
                }

                if (pc.isBankrupt(playerId)) {
                    nameLabel.setStyle(nameLabel.getStyle() + "-fx-text-fill: #555555;");
                    moneyLabel.setText("Bankrupt");
                } else {
                    playerBox.setOnMouseClicked(event -> {
                        try { pc.selectPlayer(playerId); }
                        catch (DoNotPassGoBankerException e) {}
                        handleSelectedPlayerSwitch();
                        updateSidebarDisplay();
                    });
                }

                playerListContainer.getChildren().add(playerBox);

            } catch (DoNotPassGoBankerException e) {
                error("Unexpected failure", e.getMessage(), null);
            }
        }
    }

    private void applyHouseRules() {
        if (!sm.getSettings().freeParkingEnabled) {
            mainTabPane.getTabs().remove(freeParkingTab);
            freeParkingMenu.setVisible(false);
        }
    }

    private void handleSelectedPlayerSwitch() {
        updateWindowTitleIndicator();
        if (payGoMoneyExecuted) {
            resetPayGoButton();
        }

        if (getMoneyFromBankExecuted) {
            resetGetMoneyFromBank();
        } else {
            String newValue = getMoneyFromBankField.getText().trim();
            updateGetMoneyInfoText(newValue);
        }

        if (payMoneyToBankExecuted) {
            resetPayMoneyToBank();
        } else {
            String newValue = payMoneyToBankField.getText().trim();
            updatePayMoneyToBankInfoLabel(newValue);
        }

        if (putMoneyToFreeParkingExecuted) {
            resetPutMoneyToFreeParking();
        } else {
            String newValue = putMoneyToFreeParkingField.getText().trim();
            updatePutMoneyToFreeParkingInfoLabel(newValue);
        }

        if (takeMoneyFromFreeParkingExcecuted) {
            resetTakeMoneyFromFreeParking();
        }

        if (transferToExecuted) {
            resetTransferTo();
        } else {
            updatePlayerComboBox(transferToBox, transferToPlayerList);
            updateTransferToInfoLabel();
        }

        if (transferFromExecuted) {
            resetTransferFrom();
        } else {
            updatePlayerComboBox(transferFromBox, transferFromPlayerList);
            updateTransferFromInfoLabel();
        }

        if (declareBankruptcyNeedsReset) {
            resetDeclareBankruptcy();
        }

        resetTwoDices();
        resetOneDice();
    }

    private void configurePlayerChoiceBoxDisplay(ComboBox<Integer> comboBox) {
        comboBox.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer id) {
                if (id == null) return "";
                try {
                    return pc.getName(id);
                } catch (DoNotPassGoBankerException e) {
                    return "id:" + id;
                }
            }

            @Override
            public Integer fromString(String string) {
                return null;
            }
        });
    }

    private void updatePlayerComboBox(ComboBox<Integer> comboBox, ObservableList<Integer> list) {
        try {
            Integer currentSelection = comboBox.getValue();
            comboBox.setValue(null);
            comboBox.getSelectionModel().clearSelection();
            list.clear();
            Integer selectedPlayer = pc.getSelectedPlayerId();
            for (Integer id : playerIds) {
                if(!id.equals(selectedPlayer) && !pc.isBankrupt(id)) {
                    list.add(id);
                }
            }

            javafx.application.Platform.runLater(() -> {
                if (list.contains(currentSelection)) {
                    comboBox.setValue(currentSelection);
                }
            });

        } catch (DoNotPassGoBankerException e) {
            error("Something broke unexpectedly", "Failed to update a combobox, some id is faulty: " + e.getMessage(), null);
        }
    }

    // FXML

    @FXML
    private void handleNextTurn() {
        pc.advanceTurn();
        handleSelectedPlayerSwitch();
        updateSidebarDisplay();
        updateWindowTitleIndicator();
    }

    private void error(String header, String content, Label label) {
        if (label != null) {
            label.setText(header);
        }
        System.err.println(header + ": " + content);
        displayError(header, content);
    }

    private void displayError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(APP_NAME + " Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void displayInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(APP_NAME + " Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void displayTransactionRejected(String playerName, int amountMissing) {
        String heading = "Transaction rejected";
        String content = String.format("%s needs $%,d more to finish the transaction.", playerName, amountMissing);
        displayInfo(heading, content);
    }

    private boolean askConfirmation(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(APP_NAME + " Confirmation");
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == buttonYes) {
            return true;
        } else {
            return false;
        }
    }

    private void finsihTheGame() {
        nextTurnButton.setDisable(true);
        displayInfo("Game over", "The game has ended.");
        gameFinished = true;
    }

    // Menu / File

    @FXML
    private void handleLoad() {
        appWindow.openLoadGameDialog();
        updateSaveMenuItemDisable();
        updateWindowTitleIndicator();
    }

    @FXML
    private boolean handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game As");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(APP_NAME + " Save Files (*.json)", "*.json")
        );

        fileChooser.setInitialFileName("donotpassgobanker_game.json");

        File file = fileChooser.showSaveDialog(getMoneyFromBankField.getScene().getWindow());
        
        if (file != null) {
            try {
                sm.saveAs(file);
                updateSaveMenuItemDisable();
                updateWindowTitleIndicator();
                return true;
            } catch (IOException e) {
                System.err.println("Saving failed: " + e.getMessage());
                displaySavingFailed(e.getMessage());
                return false;
            }
        } else {
            displaySavingFailed("Choosing the file failed.");
            return false;
        }

    }

    @FXML
    private boolean handleSave() {
        try {
            sm.save();
            updateWindowTitleIndicator();
            return true;
        } catch (DoNotPassGoBankerException | IOException e) {
            System.err.println("Saving failed: " + e.getMessage());
            displaySavingFailed(e.getMessage());
            return false;
        }
    }

    private void updateSaveMenuItemDisable() {
        saveMenuItem.setDisable(!sm.hasLastFile());
    }

    public boolean triggerExternalSaveRequest() {
        if (sm.hasLastFile()) {
            return handleSave();
        } else {
            return handleSaveAs();
        }
    }

    private void displaySavingFailed(String msg) {
        displayError("Saving failed", msg);
    }

    private void updateWindowTitleIndicator() {
        if (appWindow != null && sm != null) {
            if (sm.hasUnsavedChanges()) {
                appWindow.updateWindowTitle(APP_NAME + " - Dashboard *");
            } else {
                appWindow.updateWindowTitle(APP_NAME + " - Dashboard");
            }
        }
    }

    // Bank

    // Go Money

    @FXML
    private void handlePayGoMoney() {
        try {
            pc.addGoMoney(pc.getSelectedPlayerId());
            passGoPaidLabel.setText("Pass go money paid");
            passGoButton.setDisable(true);
            payGoMoneyAgainMenuItem.setDisable(false);
            updateSidebarDisplay();
            payGoMoneyExecuted = true;
            updateWindowTitleIndicator();
        } catch (DoNotPassGoBankerException e) {
            error("Paying pass go money failed", e.getMessage(), passGoPaidLabel);
        }
    }

    private void resetPayGoButton() {
        passGoPaidLabel.setText("");
        passGoButton.setDisable(false);
        payGoMoneyAgainMenuItem.setDisable(true);
        payGoMoneyExecuted = false;
    }

    // Get money from bank

    @FXML
    private void handleGetMoneyFieldEnterAction() {
        if (!getMoneyFromBankButton.isDisable()) {
            handleGetMoneyFromBank();
        }
    }

    @FXML
    private void handleGetMoneyFromBank() {
        try {
            int amount = Integer.parseInt(getMoneyFromBankField.getText().trim());
            pc.addMoney(pc.getSelectedPlayerId(), amount);
            getMoneyFromBankButton.setDisable(true);
            getMoneyFromBankDoneLabel.setText("Done");
            getMoneyFromBankAgainMenuItem.setDisable(false);
            updateSidebarDisplay();
            getMoneyFromBankExecuted = true;
            updateWindowTitleIndicator();
        } catch (NumberFormatException | DoNotPassGoBankerException e) {
            error("Getting money from the bank failed", e.getMessage(), getMoneyFromBankDoneLabel);
        }
    }

    private void updateGetMoneyInfoText(String newValue) {
        if (newValue.isEmpty()) {
            getMoneyFromBankInfoLabel.setText("");
            getMoneyFromBankButton.setDisable(true);
            resetGetMoneyFromBankButton.setVisible(false);
        } else {
            resetGetMoneyFromBankButton.setVisible(true);
            try {
                int valueInt = Integer.parseInt(newValue);
                getMoneyFromBankInfoLabel.setText(String.format("Adding $%,d to %s from the bank.", valueInt, pc.getName(pc.getSelectedPlayerId())));
                getMoneyFromBankButton.setDisable(false);
            } catch (NumberFormatException e) {
                getMoneyFromBankInfoLabel.setText("Amount must be a valid integer.");
                getMoneyFromBankButton.setDisable(true);
            } catch (DoNotPassGoBankerException e) {}
        }
    }

    @FXML
    private void resetGetMoneyFromBank() {
        getMoneyFromBankField.clear();
    }

    // Pay money to bank

    @FXML
    private void handlePayMoneyFieldEnterAction() {
        if (!payMoneyToBankButton.isDisable()) {
            handlePayMoneyToBank();
        }
    }

    @FXML
    private void handlePayMoneyToBank() {
        try {
            int amount = Integer.parseInt(payMoneyToBankField.getText().trim());
            if (pc.subtractMoney(pc.getSelectedPlayerId(), amount)) {
                payMoneyToBankButton.setDisable(true);
                payMoneyToBankDoneLabel.setText("Done");
                payMoneyToBankAgainMenuItem.setDisable(false);
                updateSidebarDisplay();
                updateWindowTitleIndicator();
            } else {
                payMoneyToBankDoneLabel.setText("Transaction rejected");
                displayTransactionRejected(pc.getName(pc.getSelectedPlayerId()), amount - pc.getMoney(pc.getSelectedPlayerId()));
            }
            payMoneyToBankExecuted = true;
        } catch (NumberFormatException | DoNotPassGoBankerException e) {
            error("Paying money to the bank failed", e.getMessage(), payMoneyToBankDoneLabel);
        }
    }

    private void updatePayMoneyToBankInfoLabel(String newValue) {
        if (newValue.isEmpty()) {
            payMoneyToBankInfoLabel.setText("");
            payMoneyToBankButton.setDisable(true);
            resetPayMoneyToBankButton.setVisible(false);
        } else {
            resetPayMoneyToBankButton.setVisible(true);
            try {
                int valueInt = Integer.parseInt(newValue);
                payMoneyToBankInfoLabel.setText(String.format("Paying $%,d from %s to the bank.", valueInt, pc.getName(pc.getSelectedPlayerId())));
                payMoneyToBankButton.setDisable(false);
            } catch (NumberFormatException e) {
                payMoneyToBankInfoLabel.setText("Amount must be a valid integer.");
                payMoneyToBankButton.setDisable(true);
            } catch (DoNotPassGoBankerException e) {}
        }
    }

    @FXML
    private void resetPayMoneyToBank() {
        payMoneyToBankField.clear();
    }

    // Other players

    // Transfer to

    private void updateTransferToInfoLabel() {
        transferToExecuted = false;
        transferToAgainMenuItem.setDisable(true);
        transferToDoneLabel.setText("");
        try {
            Integer toPlayerId = transferToBox.getValue();
            String amountStr = transferToField.getText().trim();

            if (toPlayerId == null && amountStr.isEmpty()) {
                transferToInfoLabel.setText("");
                resetTransferToButton.setVisible(false);
                transferToButton.setDisable(true);
            } else if (toPlayerId == null) {
                transferToInfoLabel.setText("Player to transfer to not selected.");
                resetTransferToButton.setVisible(true);
                transferToButton.setDisable(true);
            } else if (amountStr.isEmpty()) {
                transferToInfoLabel.setText("Amount not set.");
                resetTransferToButton.setVisible(true);
                transferToButton.setDisable(true);
            } else {
                int amount = Integer.parseInt(amountStr);
                transferToInfoLabel.setText(String.format("Transfering $%,d from %s to %s.", amount, pc.getName(pc.getSelectedPlayerId()), pc.getName(toPlayerId)));
                resetTransferToButton.setVisible(true);
                transferToButton.setDisable(false);
            }
        } catch (NumberFormatException e) {
            transferToInfoLabel.setText("Amount needs to be a valid integer.");
            transferToButton.setDisable(true);
            resetTransferToButton.setVisible(true);
        } catch (DoNotPassGoBankerException e) {
            transferToInfoLabel.setText("Failed to update info.");
            displayError("Failed to update info", e.getMessage());
            transferToButton.setDisable(true);
            resetTransferToButton.setVisible(true);
        }
    }

    @FXML
    private void handleTransferToEnterAction() {
        if (!transferToButton.isDisable()) {
            handleTransferTo();
        }
    }

    @FXML
    private void handleTransferTo() {
        try {
            Integer toPlayerId = transferToBox.getValue();
            if (toPlayerId == null) {
                transferToDoneLabel.setText("Transfering money failed");
                displayError("Transfering money failed", "Player to transfer to not set.");
                return;
            }
            int amount = Integer.parseInt(transferToField.getText().trim());
            if (!pc.transferMoney(pc.getSelectedPlayerId(), toPlayerId, amount)) {
                transferToDoneLabel.setText("Transaction rejected");
                displayTransactionRejected(pc.getName(pc.getSelectedPlayerId()), amount - pc.getMoney(pc.getSelectedPlayerId()));
                return;
            }
            transferToDoneLabel.setText("Done");
            transferToButton.setDisable(true);
            transferToAgainMenuItem.setDisable(false);
            transferToExecuted = true;
            updateSidebarDisplay();
            updateWindowTitleIndicator();
        } catch (NumberFormatException | DoNotPassGoBankerException e) {
            error("Transfering money failed", e.getMessage(), transferToDoneLabel);
        } 
    }

    @FXML
    private void resetTransferTo() {
        transferToField.clear();
        transferToBox.getSelectionModel().clearSelection();
    }

    // Transfer from

    private void updateTransferFromInfoLabel() {
        transferFromExecuted = false;
        transferFromAgainMenuItem.setDisable(true);
        transferFromDoneLabel.setText("");
        try {
            Integer fromPlayerId = transferFromBox.getValue();
            String amountStr = transferFromField.getText().trim();

            if (fromPlayerId == null && amountStr.isEmpty()) {
                transferFromInfoLabel.setText("");
                resetTransferFromButton.setVisible(false);
                transferFromButton.setDisable(true);
            } else if (fromPlayerId == null) {
                transferFromInfoLabel.setText("Player to transfer from not selected.");
                resetTransferFromButton.setVisible(true);
                transferFromButton.setDisable(true);
            } else if (amountStr.isEmpty()) {
                transferFromInfoLabel.setText("Amount not set.");
                resetTransferFromButton.setVisible(true);
                transferFromButton.setDisable(true);
            } else {
                int amount = Integer.parseInt(amountStr);
                transferFromInfoLabel.setText(String.format("Transfering $%,d from %s to %s.", amount, pc.getName(fromPlayerId), pc.getName(pc.getSelectedPlayerId())));
                resetTransferFromButton.setVisible(true);
                transferFromButton.setDisable(false);
            }
        } catch (NumberFormatException e) {
            transferFromInfoLabel.setText("Amount needs to be a valid integer.");
            transferFromButton.setDisable(true);
            resetTransferFromButton.setVisible(true);
        } catch (DoNotPassGoBankerException e) {
            transferFromInfoLabel.setText("Failed to update info.");
            displayError("Failed to update info", e.getMessage());
            transferFromButton.setDisable(true);
            resetTransferFromButton.setVisible(true);
        }
    }

    @FXML
    private void handleTransferFromEnterAction() {
        if (!transferFromButton.isDisable()) {
            handleTransferFrom();
        }
    }

    @FXML
    private void handleTransferFrom() {
        try {
            Integer fromPlayerId = transferFromBox.getValue();
            if (fromPlayerId == null) {
                transferFromDoneLabel.setText("Transfering money failed");
                displayError("Transfering money failed", "Player to transfer from not set.");
                return;
            }
            int amount = Integer.parseInt(transferFromField.getText().trim());
            if (!pc.transferMoney(fromPlayerId, pc.getSelectedPlayerId(), amount)) {
                transferFromDoneLabel.setText("Transaction rejected");
                displayTransactionRejected(pc.getName(fromPlayerId), amount - pc.getMoney(fromPlayerId));
                return;
            }
            transferFromDoneLabel.setText("Done");
            transferFromButton.setDisable(true);
            transferFromAgainMenuItem.setDisable(false);
            transferFromExecuted = true;
            updateSidebarDisplay();
            updateWindowTitleIndicator();
        } catch (NumberFormatException | DoNotPassGoBankerException e) {
            error("Transfering money failed", e.getMessage(), transferFromDoneLabel);
        }
    }

    @FXML
    private void resetTransferFrom() {
        transferFromField.clear();
        transferFromBox.getSelectionModel().clearSelection();
    }

    // Free Parking

    // Put money to Free Parking

    @FXML
    private void handlePutMoneyToFreeParkingEnterAction() {
        if (!putMoneyToFreeParkingButton.isDisable()) {
            handlePutMoneyToFreeParking();
        }
    }
    
    @FXML
    private void handlePutMoneyToFreeParking() {
        try {
            int amount = Integer.parseInt(putMoneyToFreeParkingField.getText().trim());
            if (pc.transferMoneyToFreeParking(pc.getSelectedPlayerId(), amount)) {
                putMoneyToFreeParkingButton.setDisable(true);
                putMoneyToFreeParkingDoneLabel.setText("Done");
                putMoneyToFreeParkingAgainMenuItem.setDisable(false);
                updateSidebarDisplay();
                updateWindowTitleIndicator();
            } else {
                putMoneyToFreeParkingDoneLabel.setText("Transaction rejected");
                displayTransactionRejected(pc.getName(pc.getSelectedPlayerId()), amount - pc.getMoney(pc.getSelectedPlayerId()));
            }
        } catch (NumberFormatException | DoNotPassGoBankerException e) {
            error("Putting money to Free Parking failed", e.getMessage(), putMoneyToFreeParkingDoneLabel);
        }
        putMoneyToFreeParkingExecuted = true;
    }

    private void updatePutMoneyToFreeParkingInfoLabel(String newValue) {
        if (newValue.isEmpty()) {
            putMoneyToFreeParkingInfoLabel.setText("");
            putMoneyToFreeParkingButton.setDisable(true);
            resetPutMoneyToFreeParkingButton.setVisible(false);
        } else {
            resetPutMoneyToFreeParkingButton.setVisible(true);
            try {
                int valueInt = Integer.parseInt(newValue);
                putMoneyToFreeParkingInfoLabel.setText(String.format("Putting $%,d from %s to Free Parking.", valueInt, pc.getName(pc.getSelectedPlayerId())));
                putMoneyToFreeParkingButton.setDisable(false);
            } catch (NumberFormatException e) {
                putMoneyToFreeParkingInfoLabel.setText("Amount must be a valid integer.");
                putMoneyToFreeParkingButton.setDisable(true);
            } catch (DoNotPassGoBankerException e) {}
        }
    }

    @FXML
    private void resetPutMoneyToFreeParking() {
        putMoneyToFreeParkingField.clear();
    }

    // Take all money from free parking

    @FXML
    private void handleTakeMoneyFromFreeParking() {
        try {
            int amount = pc.takeMoneyFromFreeParking(pc.getSelectedPlayerId());
            if (amount > 0) {
                takeMoneyFromFreeParkingDoneLabel.setText(String.format("%s took $%,d from Free Parking.", pc.getName(pc.getSelectedPlayerId()), amount));
                updateSidebarDisplay();
                updateWindowTitleIndicator();
            } else {
                takeMoneyFromFreeParkingDoneLabel.setText("There wasn't any money in Free Parking.");
            }
            takeMoneyFromFreeParkingButton.setDisable(true);
        } catch (DoNotPassGoBankerException e) {
            error("Taking money from Free Parking failed", e.getMessage(), takeMoneyFromFreeParkingDoneLabel);
        }
        takeMoneyFromFreeParkingExcecuted = true;
    }

    private void resetTakeMoneyFromFreeParking() {
        takeMoneyFromFreeParkingDoneLabel.setText("");
        takeMoneyFromFreeParkingButton.setDisable(false);
    }

    // Bankruptcy

    @FXML
    private void handleDeclareBankruptcy() {
        try {
            String header = "Are you sure you want to declare bankruptcy?";
            String content = String.format("If you declare bankruptcy you can no longer access player %s nor their money.", pc.getName(pc.getSelectedPlayerId()));
            if (askConfirmation(header, content)) {
                if (!pc.bankruptPlayer(pc.getSelectedPlayerId())) {
                    finsihTheGame();
                }
                declareBankruptcyButton.setDisable(true);
                declareBankruptyInfoLabel.setText("Declared bankruptcy.");
                updateSidebarDisplay();
                updateWindowTitleIndicator();
                for (var tab : mainTabPane.getTabs()) {
                    tab.setDisable(true);
                }
                advancedMenu.setDisable(true);
            } else {
                declareBankruptyInfoLabel.setText("Backed out.");
            }
        } catch (DoNotPassGoBankerException e) {
            error("Failed to declare bankruptcy", e.getMessage(), declareBankruptyInfoLabel);
        }
        declareBankruptcyNeedsReset = true;
    }

    private void resetDeclareBankruptcy() {
        declareBankruptcyButton.setDisable(false);
        declareBankruptyInfoLabel.setText("");
        for (var tab : mainTabPane.getTabs()) {
            tab.setDisable(false);
        }
        advancedMenu.setDisable(false);
    }

    // Dice

    @FXML
    private void throwTwoDices() {
        int diceOne = dice.throwDice();
        int diceTwo = dice.throwDice();
        twoDicesLabel.setText(String.format("%d + %d = %d", diceOne, diceTwo, diceOne + diceTwo));
        resetTwoDicesButton.setVisible(true);
    }

    @FXML
    private void resetTwoDices() {
        twoDicesLabel.setText("");
        resetTwoDicesButton.setVisible(false);
    }

    @FXML
    private void throwOneDice() {
        int result = dice.throwDice();
        oneDiceLabel.setText(String.format("%d", result));
        resetOneDiceButton.setVisible(true);
    }

    @FXML
    private void resetOneDice() {
        oneDiceLabel.setText("");
        resetOneDiceButton.setVisible(false);
    }
}
