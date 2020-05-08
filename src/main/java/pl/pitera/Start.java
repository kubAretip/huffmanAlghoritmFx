package pl.pitera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Start extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getFxmlUrl("views/mainStage.fxml"));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1024, 720);
        stage.setTitle("Metody kompresji informacji w sieci");
        stage.setScene(scene);
        stage.show();
    }

    private URL getFxmlUrl(String fxmlPath) {

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
