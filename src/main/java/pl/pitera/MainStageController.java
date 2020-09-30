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
import pl.pitera.model.CharacterModel;
import pl.pitera.model.TreeNode;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MainStageController implements Initializable {

    @FXML
    private TextArea textToEncodeTextArea;
    @FXML
    private TableView<CharacterModel> charsTableView;
    @FXML
    private TableColumn<CharacterModel, String> charsColumn;
    @FXML
    private TableColumn<CharacterModel, String> probabilityColumn;
    @FXML
    private TableColumn<CharacterModel, String> codeColumn;
    @FXML
    private TextArea avgWordLength;
    @FXML
    private TextArea entropy;
    @FXML
    private TextArea encodedMessage;
    @FXML
    private TextArea inputBits;
    @FXML
    private TextArea outputBits;
    @FXML
    private TextArea compression;
    @FXML
    private TextArea efficiency;

    private ResourceBundle resourceBundle;
    private StringProperty messageStringProp;
    private ObservableList<CharacterModel> characters;
    private HuffmanAlgorithm huffmanAlgorithm;
    private StringBuilder encodedMessageStringBuilder;
    private StringProperty entropyStringProp;
    private StringProperty avgWordLengthStringProp;
    private StringProperty encodeMessageStingProp;
    private StringProperty inputBitsStringProp;
    private StringProperty outputBitsStringProp;
    private StringProperty compressionStringProp;
    private StringProperty efficiencyStringProp;
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
        efficiencyStringProp = new SimpleStringProperty();
        huffmanTreeView = new HuffmanTreeView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.resourceBundle = resourceBundle;

        Bindings.bindBidirectional(textToEncodeTextArea.textProperty(), messageStringProp);
        encodedMessage.textProperty().bindBidirectional(encodeMessageStingProp);
        avgWordLength.textProperty().bindBidirectional(avgWordLengthStringProp);
        entropy.textProperty().bindBidirectional(entropyStringProp);
        inputBits.textProperty().bindBidirectional(inputBitsStringProp);
        outputBits.textProperty().bindBidirectional(outputBitsStringProp);
        compression.textProperty().bindBidirectional(compressionStringProp);
        efficiency.textProperty().bindBidirectional(efficiencyStringProp);

        setupTableView();

        messageStringProp.addListener((observableValue, oldVal, newVal) -> {

            int textLength = observableValue.getValue().length();

            if (textLength > 1) {

                encodedMessageStringBuilder.setLength(0);
                characters.clear();

                Map<Character, Integer> charactersFrequency = new HashMap<>();

                //split string to chars
                Supplier<Stream<Character>> messageCharacterStreamSupplier = () -> observableValue.getValue().chars().mapToObj(c -> (char) c);

                //add characters to the map with their frequency
                messageCharacterStreamSupplier.get().forEach(character -> charactersFrequency.compute(character, (k, v) -> (v == null) ? 1 : v + 1));

                if (charactersFrequency.size() >= 2) {

                    //data models
                    PriorityQueue<TreeNode> treeNodes = new PriorityQueue<>(Comparator.comparingInt(TreeNode::getFreq));
                    charactersFrequency.forEach((character, freq) -> treeNodes.add(new TreeNode(freq, character)));

                    //update view models
                    charactersFrequency.forEach((character, freq) -> characters.add(new CharacterModel(character.toString(), freq, null)));

                    //encode characters
                    treeTop = huffmanAlgorithm.buildTree(treeNodes);
                    Map<Character, String> huffmanCodes = huffmanAlgorithm.encodeCharacters(treeTop);

                    //update view model data
                    updateViewList(characters, huffmanCodes);


                    double entropyValue = huffmanAlgorithm.calcEntropy(characters, textLength);
                    entropyStringProp.setValue(String.format("%.6f", entropyValue));

                    double avgWordLengthValue = huffmanAlgorithm.calcAvgWordLength(characters, textLength);
                    avgWordLengthStringProp.setValue(String.format("%.6f", avgWordLengthValue));

                    //joining of coded characters
                    messageCharacterStreamSupplier
                            .get()
                            .forEach(characterInMessage -> characters.stream()
                                    .anyMatch(codedCharacter -> characterInMessage.equals(characterInMessage.toString().equals(codedCharacter.getCharacter())
                                            ? encodedMessageStringBuilder.append(codedCharacter.getCode()) : null)));

                    encodeMessageStingProp.setValue(encodedMessageStringBuilder.toString());

                    int inputBits = huffmanAlgorithm.calcInputBits(textLength);
                    inputBitsStringProp.setValue(inputBits + " b");

                    int outputBits = encodedMessageStringBuilder.toString().length();
                    outputBitsStringProp.setValue(outputBits + " b");

                    compressionStringProp.setValue(String.format("%.2f", huffmanAlgorithm.calcCompressionInPercent(inputBits, outputBits)) + " %");
                    efficiencyStringProp.setValue(String.format("%.2f", (entropyValue / avgWordLengthValue) * 100) + " %");
                } else {
                    clearLabels();
                }

            } else {
                clearLabels();
            }


        });

    }

    private void clearLabels() {
        treeTop = null;
        encodedMessageStringBuilder.setLength(0);
        characters.clear();
        encodedMessage.setText("");
        avgWordLength.setText("");
        entropy.setText("");
    }


    private void setupTableView() {

        charsTableView.setPlaceholder(new Label(resourceBundle.getString("required-character-missing")));

        charsColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().characterProperty().getValue().toCharArray()[0] == ' ')
                return new SimpleStringProperty(resourceBundle.getString("space"));

            if (cellData.getValue().characterProperty().getValue().toCharArray()[0] == '\n')
                return new SimpleStringProperty(resourceBundle.getString("new-line"));

            if (cellData.getValue().characterProperty().getValue().toCharArray()[0] == '\t')
                return new SimpleStringProperty(resourceBundle.getString("tab"));

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
            stage.setTitle(resourceBundle.getString("tree"));

            AnchorPane container = huffmanTreeView.drawTree(treeTop);

            ScrollPane scrollPane = new ScrollPane(container);

            Scene scene = new Scene(scrollPane, 800, 800);
            stage.setScene(scene);
            stage.show();

        } else {
            showAlertMessage(resourceBundle.getString("error"),
                    resourceBundle.getString("invalid-message"),
                    resourceBundle.getString("invalid-message-content"));
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

    /**
     * update characters codes to view model list
     *
     * @param characterModelList ObservableList with model to TableView
     * @param huffmanCodes       List with encoded character by Huffman algorithm
     */
    private void updateViewList(ObservableList<CharacterModel> characterModelList, Map<Character, String> huffmanCodes) {
        characterModelList.forEach(characterModel -> {
            huffmanCodes.forEach((character, s) -> {
                if (characterModel.getCharacter().equals(character.toString())) {
                    characterModel.setCode(s);
                }
            });
        });
    }


}
