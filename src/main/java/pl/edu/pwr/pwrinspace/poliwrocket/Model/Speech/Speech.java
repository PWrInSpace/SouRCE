package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.google.gson.annotations.Expose;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Speech {



    @Expose
    private String trigger;

    @Expose
    private String textToSpeak;

    @Expose
    private  List<Rule> rules = new ArrayList<>();

    public String getTextToSpeak() {
        return textToSpeak;
    }

    public String getTextToSpeak(Object param) {
        return MessageFormat.format(textToSpeak,param);
    }

    public String getTrigger() {
        return trigger;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
