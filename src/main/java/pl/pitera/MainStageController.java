package pl.pitera;


import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class MainStageController implements Initializable {

    @FXML
    TextArea textToEncodeTextArea;

    @FXML
    TableView<CharacterViewModel> charsTableView;

    @FXML
    TableColumn<CharacterViewModel, String> charsColumn;

    @FXML
    TableColumn<CharacterViewModel, String> probabilityColumn;

    @FXML
    TableColumn<CharacterViewModel, String> codeColumn;

    @FXML
    TextArea avgWordLength;

    @FXML
    TextArea entropy;

    @FXML
    TextArea encodedMessage;

    private StringProperty messageStringProp;
    private ObservableList<CharacterViewModel> characters;
    private HuffmanAlgorithm huffmanAlgorithm;
    private StringBuilder encodedMessageStringBuilder;
    private StringProperty entropyStringProp;
    private StringProperty avgWordLengthStringProp;
    private StringProperty encodeMessageStingProp;
    private HuffmanTreeView huffmanTreeView;
    private TreeNode treeTop = null;

    public MainStageController() {
        messageStringProp = new SimpleStringProperty();
        characters = FXCollections.observableArrayList();
        huffmanAlgorithm = new HuffmanAlgorithm();
        encodedMessageStringBuilder = new StringBuilder();
        entropyStringProp = new SimpleStringProperty();
        avgWordLengthStringProp = new SimpleStringProperty();
        encodeMessageStingProp = new SimpleStringProperty();
        huffmanTreeView = new HuffmanTreeView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //setup bindings
        Bindings.bindBidirectional(textToEncodeTextArea.textProperty(), messageStringProp);
        encodedMessage.textProperty().bindBidirectional(encodeMessageStingProp);
        avgWordLength.textProperty().bindBidirectional(avgWordLengthStringProp);
        entropy.textProperty().bindBidirectional(entropyStringProp);

        setupTableView();


        //listen on the entered message to encode
        messageStringProp.addListener((observableValue, oldVal, newVal) -> {

            int textLength = observableValue.getValue().length();

            if (textLength > 1) {

                Map<Character, Integer> charactersFrequency = new HashMap<>();

                //is not good solution but work
                encodedMessageStringBuilder.setLength(0);
                characters.clear();

                //split string to chars
                Stream<Character> characterStream = observableValue.getValue().chars().mapToObj(c -> (char) c);

                //add characters to the map with their frequency
                characterStream.forEach(character -> charactersFrequency.compute(character, (k, v) -> (v == null) ? 1 : v + 1));

                //data models
                PriorityQueue<TreeNode> treeNodes = new PriorityQueue<>(Comparator.comparingInt(TreeNode::getFreq));
                charactersFrequency.forEach((character, freq) -> treeNodes.add(new TreeNode(freq, character)));

                //update view models
                charactersFrequency.forEach((character, freq) -> characters.add(new CharacterViewModel(character.toString(), freq, null)));

                //encode characters
                treeTop = huffmanAlgorithm.buildTree(treeNodes);
                Map<Character, String> huffmanCodes = huffmanAlgorithm.encodeCharacters(treeTop);
                updateViewList(characters, huffmanCodes);

                //entropy label
                entropyStringProp.setValue(String.valueOf(huffmanAlgorithm.calcEntropy(characters, textLength)));

                //avg word length label
                avgWordLengthStringProp.setValue(String.valueOf(huffmanAlgorithm.calcAvgWordLength(characters, textLength)));

                //joining of coded characters
                characters.forEach(characterViewModel -> encodedMessageStringBuilder.append(characterViewModel.getCode()));

                //encoded message label
                encodeMessageStingProp.setValue(encodedMessageStringBuilder.toString());


            } else {
                treeTop = null;
                encodedMessageStringBuilder.setLength(0);
                characters.clear();
                encodedMessage.setText("");
                avgWordLength.setText("");
                entropy.setText("");
            }


        });

    }

    private void setupTableView() {

        charsTableView.setPlaceholder(new Label("Brak wymaganej ilości znaków"));

        charsColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().characterProperty().getValue().toCharArray()[0] == ' ')
                return new SimpleStringProperty("<spacja>");

            if (cellData.getValue().characterProperty().getValue().toCharArray()[0] == '\n')
                return new SimpleStringProperty("<nowa linia>");

            if (cellData.getValue().characterProperty().getValue().toCharArray()[0] == '\t')
                return new SimpleStringProperty("<tabulator>");

            return cellData.getValue().characterProperty();
        });
        probabilityColumn.setCellValueFactory(charCodeStringCellDataFeatures ->
                new SimpleStringProperty(charCodeStringCellDataFeatures.getValue().getFrequency() + " / " + messageStringProp.getValue().length()));
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());

        //  new sorting pattern in probabilityColumn
        probabilityColumn.setComparator((val1, val2) -> {
            Integer number1 = Integer.parseInt(val1.substring(0, val1.indexOf("/")).replaceAll("\\s+", ""));
            Integer number2 = Integer.parseInt(val2.substring(0, val2.indexOf("/")).replaceAll("\\s+", ""));
            return number1.compareTo(number2);
        });
        charsTableView.setItems(characters);

    }

    @FXML
    public void treeButton() {

        if (treeTop != null) {
            Canvas treeCanvas = huffmanTreeView.drawTree(treeTop);
            if (treeCanvas != null) {
                Stage stage = new Stage();
                stage.setTitle("Drzewo");

                AnchorPane container = new AnchorPane();

                ScrollPane scrollPane = new ScrollPane(container);


                container.getChildren().add(treeCanvas);

                Scene scene = new Scene(scrollPane, 800, 800);
                stage.setScene(scene);
                stage.show();
            } else {
                // i know is not good solution... ;/
                showAlertMessage("Uwaga", "Błąd renderowania drzewa", "Ze względu na ograniczenia biblioteki JavaFx nie można stworzyć drzewa." +
                        "\nWielkość drzewa jest za duża." +
                        "\nWprowadź mniejszą wiadomość.");
            }
        } else {
            showAlertMessage("Uwaga!", "Nieprawidłowa wiadomość", "Wiadomość musi się składać z minimum 2 różnych znaków !");
        }
    }

    @FXML
    public void saveTree() {
        if (treeTop != null) {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            //build tree view
            Canvas treeCanvas = huffmanTreeView.drawTree(treeTop);

            if (treeCanvas != null) {
                //Show save file dialog
                File file = fileChooser.showSaveDialog(new Stage());
                if (file != null) {

                    Runnable runnable = new Thread(() -> {

                        WritableImage writableImage = snapshotScaledCanvas(treeCanvas, 2);
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        try {
                            ImageIO.write(renderedImage, "png", file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    runnable.run();
                }
            } else {
                showAlertMessage("Uwaga", "Błąd renderowania drzewa", "Ze względu na ograniczenia biblioteki JavaFx nie można stworzyć trzeba." +
                        "\nWielkość drzewa jest za duża." +
                        "\nWprowadź mniejszą wiadomość.");
            }


        } else {
            showAlertMessage("Uwaga!", "Nieprawidłowa wiadomość", "Wiadomość musi się składać z minimum 2 różnych znaków !");
        }

    }


    public static WritableImage snapshotScaledCanvas(Canvas canvas, double pixelScale) {
        WritableImage writableImage = new WritableImage((int) Math.rint(pixelScale * canvas.getWidth()), (int) Math.rint(pixelScale * canvas.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        return canvas.snapshot(spa, writableImage);
    }

    private void showAlertMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();

    }


    private void updateViewList(List<CharacterViewModel> characterViewModelList, Map<Character, String> huffmanCodes) {
        characterViewModelList.forEach(characterViewModel -> {
            huffmanCodes.forEach((character, s) -> {
                if (characterViewModel.getCharacter().equals(character.toString())) {
                    characterViewModel.setCode(s);
                }
            });
        });
    }


}
