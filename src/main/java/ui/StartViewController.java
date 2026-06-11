package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import storage.*;

import java.util.ArrayList;

import engine.*;

public class StartViewController {
    private StorageManager sm;
    private AppWindow appWindow;

    @FXML private ToggleButton enableFreeParkingButton;
    @FXML private GridPane newGameGrid;

    @FXML private TextField firstPlayerField;
    @FXML private TextField secondPlayerField;
    @FXML private TextField thirdPlayerField;
    @FXML private TextField fourthPlayerField;
    @FXML private TextField fifthPlayerField;
    @FXML private TextField sixthPlayerField;

    @FXML private Label firstPlayerLabel;
    @FXML private Label secondPlayerLabel;

    @FXML private TextField startingMoneyField;
    @FXML private TextField passGoMoneyField;

    @FXML private Label startErrorLabel;
    @FXML private Label passGoErrorLabel;

    @FXML private Label generalErrorLabel;

    

    public void initData(StorageManager storageManager) {
        this.sm = storageManager;
    }

    public void setAppWindow(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    @FXML
    private void handleNewGame() {
        newGameGrid.setVisible(true);
    }

    @FXML
    private void handleLoadGame() {
        appWindow.openLoadGameDialog();
    }

    @FXML
    private void handleFreeParkingButtonPressed() {
        if (enableFreeParkingButton.isSelected()) {
            enableFreeParkingButton.setText("Enabled");
        } else {
            enableFreeParkingButton.setText("Disabled");
        }
    }

    @FXML
    private void handleStartNewGame() {
        generalErrorLabel.setText("");
        firstPlayerField.setStyle("");
        secondPlayerField.setStyle("");
        startingMoneyField.setStyle("");
        passGoMoneyField.setStyle("");
        startErrorLabel.setText("");
        passGoErrorLabel.setText("");

        boolean hasErrors = false;

        var firstPlayerName = firstPlayerField.getText().trim();
        var secondPlayerName = secondPlayerField.getText().trim();
        var thirdPlayerName = thirdPlayerField.getText().trim();
        var fourthPlayerName = fourthPlayerField.getText().trim();
        var fifthPlayerName = fifthPlayerField.getText().trim();
        var sixthPlayerName = sixthPlayerField.getText().trim();

        int countPlayer = 0;

        if (!firstPlayerName.isEmpty()) countPlayer++;
        if (!secondPlayerName.isEmpty()) countPlayer++;
        if (!thirdPlayerName.isEmpty()) countPlayer++;
        if (!fourthPlayerName.isEmpty()) countPlayer++;
        if (!fifthPlayerName.isEmpty()) countPlayer++;
        if (!sixthPlayerName.isEmpty()) countPlayer++;

        if (countPlayer < 2) {
            generalErrorLabel.setText("Must have atleast two (2) players!");
            hasErrors = true;
        }

        if (countPlayer < 2 && firstPlayerName.isEmpty()) {
            firstPlayerField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            countPlayer++;
        }

        if (countPlayer < 2 && secondPlayerName.isEmpty()) {
            secondPlayerField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            countPlayer++;
        }

        var startMoneyStr = startingMoneyField.getText().trim();
        int startMoney = 0;
        if (startMoneyStr.isEmpty()) {
            startingMoneyField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            startErrorLabel.setText("Required!");
            hasErrors = true;
        } else {
            try {
                startMoney = Integer.parseInt(startMoneyStr);
            } catch (NumberFormatException e) {
                startingMoneyField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                startErrorLabel.setText("Must be an integer!");
                hasErrors = true;
            }
        }

        var passGoMoneyStr = passGoMoneyField.getText().trim();
        int passGoMoney = 0;
        if (passGoMoneyStr.isEmpty()) {
            passGoMoneyField.setStyle("-fx-border-color: red; -fx-border-width: 2px");
            passGoErrorLabel.setText("Required!");
            hasErrors = true;
        } else {
            try {
                passGoMoney = Integer.parseInt(passGoMoneyStr);
            } catch (NumberFormatException e) {
                passGoMoneyField.setStyle("-fx-border-color: red; -fx-border-width: 2px");
                passGoErrorLabel.setText("Must be an integer!");
                hasErrors = true;
            }
        }

        if (hasErrors) return;

        System.out.println("Data is valid. Starting the new game.");

        AppData data = new AppData(startMoney, passGoMoney, enableFreeParkingButton.isSelected());

        PlayerController pc = new PlayerController(data, sm);

        int id = 1;

        ArrayList<Integer> playerIds = new ArrayList<>();

        if(!firstPlayerName.isEmpty()) {
            pc.newPlayer(id, firstPlayerName);
            playerIds.add(id);
            id++;
        }
        if (!secondPlayerName.isEmpty()) {
            pc.newPlayer(id, secondPlayerName);
            playerIds.add(id);
            id++;
        }
        if (!thirdPlayerName.isEmpty()) {
            pc.newPlayer(id, thirdPlayerName);
            playerIds.add(id);
            id++;
        }
        if (!fourthPlayerName.isEmpty()) {
            pc.newPlayer(id, fourthPlayerName);
            playerIds.add(id);
            id++;
        }
        if (!fifthPlayerName.isEmpty()) {
            pc.newPlayer(id, fifthPlayerName);
            playerIds.add(id);
            id++;
        }
        if (!sixthPlayerName.isEmpty()) {
            pc.newPlayer(id, sixthPlayerName);
            playerIds.add(id);
        }

        sm.setData(data);

        appWindow.switchToMainView(pc, playerIds);
    }
}
