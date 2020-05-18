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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
    Label encodedMessageLabel;

    @FXML
    Label avgWordLengthLabel;

    @FXML
    Label entropyLabel;

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
        encodedMessageLabel.textProperty().bindBidirectional(encodeMessageStingProp);
        avgWordLengthLabel.textProperty().bindBidirectional(avgWordLengthStringProp);
        entropyLabel.textProperty().bindBidirectional(entropyStringProp);

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

            StackPane container = new StackPane();
            ScrollPane scrollPane = new ScrollPane(container);

            Canvas treeCanvas = huffmanTreeView.drawTree(treeTop);
            container.getChildren().add(treeCanvas);

            container.setStyle("-fx-background-color: #D6D6D6");

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
