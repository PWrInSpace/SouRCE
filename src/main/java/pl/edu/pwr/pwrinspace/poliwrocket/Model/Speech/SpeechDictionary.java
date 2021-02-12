package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpeechDictionary {
    @Expose
    private HashMap<String, Speech> speechRules = new HashMap<>();

    public Speech getSpeechByTrigger(String key) {
        return speechRules.get(key);
    }

    public void add(Speech sp) {
        speechRules.put(sp.getTrigger(),sp);
    }

    public boolean validateDictionary() {
        AtomicBoolean valid = new AtomicBoolean(true);
        speechRules.forEach((s, speech) -> {
            if(s != speech.getTrigger() && valid.get()) {
                valid.set(false);
            }
        });
        return valid.get();
    }
}
