package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class CodeInterpreter {

    @JsonProperty("interpreters")
    private HashMap<Integer, InterpreterValue> interpreters;

    public InterpreterValue getCodeMeaning(int code) {
        return interpreters.getOrDefault(code,  new InterpreterValue(code, CodeInterpreterUIHint.INFO));
    }
}
