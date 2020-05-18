package pl.pitera;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View model
 */
public class CharCode {

    private StringProperty character;
    private IntegerProperty frequency;
    private StringProperty code;

    public CharCode(String character, Integer frequency, String code) {
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
