package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InterpreterValue {

    @JsonProperty("text")
    public String text;

    @JsonProperty("UIHint")
    public CodeInterpreterUIHint UIHint;

    public InterpreterValue() {
    }

    public InterpreterValue(String text, CodeInterpreterUIHint UIHint) {
        this.text = text;
        this.UIHint = UIHint;
    }

    public InterpreterValue(int text, CodeInterpreterUIHint UIHint) {
        this.text = Integer.toString(text);
        this.UIHint = UIHint;
    }

}
