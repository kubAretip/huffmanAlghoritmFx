package pl.pitera;


import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class MainStageController implements Initializable {

    @FXML
    TextArea textToEncodeTextArea;

    @FXML
    Label textLabel;

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

    private StringProperty stringProperty;
    private ObservableList<CharCode> characters;
    private HuffmanAlgorithm huffmanAlgorithm;
    private StringBuilder encodedMessageStringBuilder;

    public MainStageController() {
        stringProperty = new SimpleStringProperty();
        characters = FXCollections.observableArrayList();
        huffmanAlgorithm = new HuffmanAlgorithm();
        encodedMessageStringBuilder = new StringBuilder();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Bindings.bindBidirectional(textToEncodeTextArea.textProperty(), stringProperty);
        textLabel.textProperty().bindBidirectional(stringProperty);


        //table columns
        charsColumn.setCellValueFactory(cellData -> cellData.getValue().characterProperty());
        probabilityColumn.setCellValueFactory(charCodeStringCellDataFeatures ->
                new SimpleStringProperty(charCodeStringCellDataFeatures.getValue().getFrequency() + " / " + stringProperty.getValue().length()));
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());

        //  new sorting pattern in probabilityColumn
        probabilityColumn.setComparator((val1, val2) -> {
            Integer number1 = Integer.parseInt(val1.substring(0, val1.indexOf("/")).replaceAll("\\s+", ""));
            Integer number2 = Integer.parseInt(val2.substring(0, val2.indexOf("/")).replaceAll("\\s+", ""));
            return number1.compareTo(number2);
        });


        charsTableView.setItems(characters);

        //listen on the entered message to encode
        stringProperty.addListener((observableValue, oldVal, newVal) -> {

            if (observableValue.getValue().length() > 1) {
                Map<Character, Integer> charactersOccurrences = new HashMap<>();

                characters.clear();  //is not good solution but work

                //split string to chars
                Stream<Character> characterStream = observableValue.getValue().chars().mapToObj(c -> (char) c);

                //add characters to the map with their occurrence
                characterStream.forEach(character -> charactersOccurrences.compute(character, (k, v) -> (v == null) ? 1 : v + 1));

                charactersOccurrences.forEach((character, integer) -> characters.add(new CharCode(character.toString(), integer, "")));

                //encode
                huffmanAlgorithm.buildTree(characters);

                encodedMessageStringBuilder.setLength(0);
                encodedMessageLabel.setText("");

                characters.forEach(charCode -> encodedMessageStringBuilder.append(charCode.getCode()));
                encodedMessageLabel.setText(encodedMessageStringBuilder.toString());

            }


        });


    }


}
