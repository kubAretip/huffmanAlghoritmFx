package pl.pitera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Start extends Application {

    private final static int MIN_WINDOW_WIDTH = 1024;
    private final static int MIN_WINDOW_HEIGHT = 720;
    private final static String WINDOW_TITLE = "Metody kompresji informacji w sieci";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();

        String fxmlPath = "views/mainStage.fxml";
        fxmlLoader.setLocation(getFxmlUrl(fxmlPath));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public URL getFxmlUrl(String fxmlPath) {

        URL url = null;

        try {
            url = getClass().getClassLoader().getResource(fxmlPath);
            if (url == null) {
                throw new NullPointerException("** File with " + fxmlPath + " path doesn't exist **");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return url;
    }

}
