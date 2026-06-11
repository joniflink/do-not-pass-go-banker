package ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import engine.DoNotPassGoBankerException;
import engine.PlayerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storage.*;

public class AppWindow {
    private static final String APP_NAME = "Do Not Pass Go Banker";

    private final Stage stage;
    private StorageManager sm = new StorageManager();

    private MainViewController mainViewController;

    public AppWindow(Stage stage) {
        this.stage = stage;
    }

    public void showStartView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/StartView.fxml"));
            Parent root = loader.load();

            StartViewController controller = loader.getController();
            controller.initData(sm);
            controller.setAppWindow(this);

            stage.setScene(new Scene(root));
            stage.setTitle(APP_NAME + " - Setup");

            stage.sizeToScene();

            stage.show();

            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToMainView(PlayerController pc, ArrayList<Integer> playerIds) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/MainView.fxml"));
            Parent root = loader.load();

            mainViewController = loader.getController();
            mainViewController.initData(pc, sm, playerIds, this);

            stage.setScene(new Scene(root));
            stage.setTitle(APP_NAME + " - Dashboard");
            stage.setOnCloseRequest(event -> {
                if (mainViewController != null && sm != null && sm.hasUnsavedChanges()) {
                    event.consume();
                    if (isItSaveToProceedBeforeExiting()) {
                        System.exit(0);
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to load Main Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openLoadGameDialog() {
        if (isItSaveToProceedBeforeExiting()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open " + APP_NAME + " Save File");

            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(APP_NAME + " Save Files (*.json)", "*.json")
            );

            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                try {
                    AppData data = sm.load(selectedFile);
                    ArrayList<Integer> playerIds = new ArrayList<>();
                    for (var player : data.players.values()) {
                        playerIds.add(player.id);
                    }
                    switchToMainView(new PlayerController(data, sm), playerIds);
                    System.out.println("Loaded file " + selectedFile.getName());
                } catch (DoNotPassGoBankerException | IOException e) {
                    System.err.println("Error loading file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isItSaveToProceedBeforeExiting() {
        if (sm == null || mainViewController == null || !sm.hasUnsavedChanges()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved changes");
        alert.setHeaderText("You have unsaved changes");
        alert.setContentText("Would you like to save your changes before exiting?");

        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonDontSave = new ButtonType("Don't save");
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonSave, buttonDontSave, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == buttonSave) {
                return mainViewController.triggerExternalSaveRequest();
            } else if (result.get() == buttonDontSave) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void updateWindowTitle(String newTitle) {
        stage.setTitle(newTitle);
    }
}
