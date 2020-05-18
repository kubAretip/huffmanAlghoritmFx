package pl.pitera;


import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class MainStageController implements Initializable {

    @FXML
    TextArea textToEncodeTextArea;

    @FXML
    TableView<CharCode> charsTableView;

    @FXML
    TableColumn<CharCode, String> charsColumn;

    @FXML
    TableColumn<CharCode, String> probabilityColumn;

    @FXML
    TableColumn<CharCode, String> codeColumn;

    @FXML
    Label encodedMessageLabel;

    @FXML
    Label avgWordLengthLabel;

    @FXML
    Label entropyLabel;

    private StringProperty messageStringProp;
    private ObservableList<CharCode> characters;
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

        Bindings.bindBidirectional(textToEncodeTextArea.textProperty(), messageStringProp);

        setupTableView();

        //listen on the entered message to encode
        messageStringProp.addListener((observableValue, oldVal, newVal) -> {

            int textLength = observableValue.getValue().length();

            if (textLength > 1) {
                Map<Character, Integer> charactersOccurrences = new HashMap<>();

                characters.clear();  //is not good solution but work

                //split string to chars
                Stream<Character> characterStream = observableValue.getValue().chars().mapToObj(c -> (char) c);

                //add characters to the map with their occurrence
                characterStream.forEach(character -> charactersOccurrences.compute(character, (k, v) -> (v == null) ? 1 : v + 1));

                charactersOccurrences.forEach((character, integer) -> characters.add(new CharCode(character.toString(), integer, null)));

                //encode characters
                treeTop = huffmanAlgorithm.buildTree(characters);
                Map<Character, String> huffmanCodes = huffmanAlgorithm.encodeCharacters(treeTop);
                updateList(characters, huffmanCodes);

                entropyStringProp.setValue(String.valueOf(huffmanAlgorithm.calcEntropy(characters, textLength)));
                entropyLabel.textProperty().bindBidirectional(entropyStringProp);
                avgWordLengthStringProp.setValue(String.valueOf(huffmanAlgorithm.avgWordLength(characters, textLength)));
                avgWordLengthLabel.textProperty().bindBidirectional(avgWordLengthStringProp);


                encodedMessageStringBuilder.setLength(0);
                characters.forEach(charCode -> encodedMessageStringBuilder.append(charCode.getCode()));
                encodeMessageStingProp.setValue(encodedMessageStringBuilder.toString());
                encodedMessageLabel.textProperty().bindBidirectional(encodeMessageStingProp);

            } else {
                treeTop = null;
                encodedMessageStringBuilder.setLength(0);
                characters.clear();
                encodedMessageLabel.setText("");
                avgWordLengthLabel.setText("");
                entropyLabel.setText("");
            }


        });

    }

    private void setupTableView() {

        charsTableView.setPlaceholder(new Label("Brak wymaganej ilości znaków"));
        charsColumn.setCellValueFactory(cellData -> cellData.getValue().characterProperty());
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
            Stage stage = new Stage();
            stage.setTitle("Drzewo");
            FlowPane flowPane = new FlowPane();
            ScrollPane scrollPane = new ScrollPane(flowPane);

            Canvas treeCanvas = huffmanTreeView.buildTreeView(treeTop);

            flowPane.getChildren().add(treeCanvas);
            flowPane.setStyle("-fx-background-color: #EEEEEE");
            stage.setScene(new Scene(scrollPane, 1024, 720));
            stage.show();

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uwaga!");
            alert.setHeaderText("Nieprawidłowa wiadomość");
            alert.setContentText("Wiadomość musi się składać z minimum 2 różnych znaków !");
            alert.showAndWait();
        }

    }

    private void updateList(List<CharCode> charCodeList, Map<Character, String> huffmanCodes) {
        charCodeList.forEach(charCode -> {
            huffmanCodes.forEach((character, s) -> {
                if (charCode.getCharacter().equals(character.toString())) {
                    charCode.setCode(s);
                }
            });
        });
    }

}
