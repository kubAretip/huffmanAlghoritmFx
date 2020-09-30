package pl.pitera.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CharacterModel {

    private StringProperty character;
    private IntegerProperty frequency;
    private StringProperty code;

    public CharacterModel(String character, Integer frequency, String code) {
        this.character = new SimpleStringProperty(character);
        this.frequency = new SimpleIntegerProperty(frequency);
        this.code = new SimpleStringProperty(code);
    }

    public String getCharacter() {
        return character.get();
    }

    public StringProperty characterProperty() {
        return character;
    }

    public Integer getFrequency() {
        return frequency.get();
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

}
