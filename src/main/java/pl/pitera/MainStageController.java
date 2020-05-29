package pl.pitera;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
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

    @FXML
    TextArea inputBits;

    @FXML
    TextArea outputBits;

    @FXML
    TextArea compression;

    private StringProperty messageStringProp;
    private ObservableList<CharacterViewModel> characters;
    private HuffmanAlgorithm huffmanAlgorithm;
    private StringBuilder encodedMessageStringBuilder;
    private StringProperty entropyStringProp;
    private StringProperty avgWordLengthStringProp;
    private StringProperty encodeMessageStingProp;
    private StringProperty inputBitsStringProp;
    private StringProperty outputBitsStringProp;
    private StringProperty compressionStringProp;
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
        inputBitsStringProp = new SimpleStringProperty();
        outputBitsStringProp = new SimpleStringProperty();
        compressionStringProp = new SimpleStringProperty();
        huffmanTreeView = new HuffmanTreeView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //setup bindings
        Bindings.bindBidirectional(textToEncodeTextArea.textProperty(), messageStringProp);
        encodedMessage.textProperty().bindBidirectional(encodeMessageStingProp);
        avgWordLength.textProperty().bindBidirectional(avgWordLengthStringProp);
        entropy.textProperty().bindBidirectional(entropyStringProp);
        inputBits.textProperty().bindBidirectional(inputBitsStringProp);
        outputBits.textProperty().bindBidirectional(outputBitsStringProp);
        compression.textProperty().bindBidirectional(compressionStringProp);

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
                Supplier<Stream<Character>> characterStreamSupplier = () -> observableValue.getValue().chars().mapToObj(c -> (char) c);

                //add characters to the map with their frequency
                characterStreamSupplier.get().forEach(character -> charactersFrequency.compute(character, (k, v) -> (v == null) ? 1 : v + 1));

                //data models
                PriorityQueue<TreeNode> treeNodes = new PriorityQueue<>(Comparator.comparingInt(TreeNode::getFreq));
                charactersFrequency.forEach((character, freq) -> treeNodes.add(new TreeNode(freq, character)));

                //update view models
                charactersFrequency.forEach((character, freq) -> characters.add(new CharacterViewModel(character.toString(), freq, null)));

                //encode characters
                treeTop = huffmanAlgorithm.buildTree(treeNodes);
                Map<Character, String> huffmanCodes = huffmanAlgorithm.encodeCharacters(treeTop);
                updateViewList(characters, huffmanCodes);

                //entropy
                entropyStringProp.setValue(String.valueOf(huffmanAlgorithm.calcEntropy(characters, textLength)));

                //avg word length
                avgWordLengthStringProp.setValue(String.valueOf(huffmanAlgorithm.calcAvgWordLength(characters, textLength)));

                //joining of coded characters
                characterStreamSupplier.get().forEach(character -> characters.stream()
                        .anyMatch(codedCharacter -> character.equals(character.toString().equals(codedCharacter.getCharacter())
                                ? encodedMessageStringBuilder.append(codedCharacter.getCode()) : null)));

                //encoded message
                encodeMessageStingProp.setValue(encodedMessageStringBuilder.toString());

                //input bits
                int inputBits = huffmanAlgorithm.calcInputBits(textLength);
                inputBitsStringProp.setValue(inputBits + " b");

                //output bits
                int outputBits = encodedMessageStringBuilder.toString().length();
                outputBitsStringProp.setValue(outputBits + " b");

                //compression
                compressionStringProp.setValue(huffmanAlgorithm.calcCompressionPercent(inputBits, outputBits) + " %");

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

            Stage stage = new Stage();
            stage.setTitle("Drzewo");

            AnchorPane container = huffmanTreeView.drawTree(treeTop);

            ScrollPane scrollPane = new ScrollPane(container);

            Scene scene = new Scene(scrollPane, 800, 800);
            stage.setScene(scene);
            stage.show();

        } else {
            showAlertMessage("Uwaga!", "Nieprawidłowa wiadomość", "Wiadomość musi się składać z minimum 2 różnych znaków !");
        }
    }


    private void showAlertMessage(String title, String header, String content) {
        if (title != null && header != null && content != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
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
