package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TextToSpeech {

    @JsonProperty("textToSpeak")
    private String textToSpeak;

    @JsonProperty("rules")
    private  List<Rule> rules = new ArrayList<>();

    public TextToSpeech() {
    }

    public TextToSpeech(String textToSpeak, List<Rule> rules) {
        this.textToSpeak = textToSpeak;
        this.rules = rules;
    }

    public String getTextToSpeak() {
        return textToSpeak;
    }

    public String getTextToSpeak(Object ... params) {
        return MessageFormat.format(textToSpeak,params);
    }

    public List<Rule> getRules() {
        return rules;
    }
}
