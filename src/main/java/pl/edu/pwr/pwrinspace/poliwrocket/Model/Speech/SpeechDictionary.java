package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;

import java.util.HashMap;
import java.util.List;

public class SpeechDictionary extends BaseSaveModel {

    @Expose
    private HashMap<String, List<Speech>> speechRules = new HashMap<>();

    public SpeechDictionary() {
        super("./config/", "speechConfig.json");
    }

    public List<Speech> getSpeechByTrigger(String key) {
        return speechRules.get(key);
    }

    private void add(String key, List<Speech> value) {
        speechRules.put(key, value);
    }

    public static SpeechDictionary defaultModel() {
        SpeechDictionary speechDictionary = new SpeechDictionary();
        Rule rule = new Rule("{0} > 500",1);
        Rule rule2 = new Rule("{0} > 1500",1);
        Speech sp = new Speech("Reached altitude {0} meters.", List.of(rule,rule2));
        Rule apogee = new Rule("{0} < {1}",1);
        Speech sp2 = new Speech("Apogee detected on {0} meters.", List.of(apogee));
        speechDictionary.add("Altitude",List.of(sp,sp2));

        return speechDictionary;
    }
}
