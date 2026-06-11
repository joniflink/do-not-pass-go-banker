import javafx.application.Application;
import javafx.stage.Stage;
import ui.AppWindow;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppWindow app = new AppWindow(primaryStage);
        app.showStartView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}