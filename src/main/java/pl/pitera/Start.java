package pl.pitera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import pl.pitera.utils.Language;
import pl.pitera.utils.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Start extends Application {

    private final static int MIN_WINDOW_WIDTH = 1024;
    private final static int MIN_WINDOW_HEIGHT = 720;
    private final static String WINDOW_TITLE = "HuffmanFx";
    private static final String FXML_PATH = "mainStage.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        List<String> dialogData = Arrays.asList("Polski", "English");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
        dialog.setTitle("Select language");
        dialog.setHeaderText("Please select a language to run application");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {

            if (result.get().equals(dialogData.get(0))) {
                loadStage(stage, FXML_PATH, new ResourceLoader(Language.PL));
            } else if (result.get().equals(dialogData.get(1))) {
                loadStage(stage, FXML_PATH, new ResourceLoader(Language.EN));
            } else {
                stop();
            }

        }

    }

    void loadStage(Stage stage, String fxmlPath, ResourceLoader loader) throws IOException {
        FXMLLoader fxmlLoader = loader.fxmlLoader(fxmlPath);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

}
