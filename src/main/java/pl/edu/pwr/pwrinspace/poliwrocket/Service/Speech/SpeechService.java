package pl.edu.pwr.pwrinspace.poliwrocket.Service.Speech;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech.SpeechDictionary;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Rule.RuleValidationService;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpeechService implements InvalidationListener {

    private Synthesizer synthesizer;

    private final RuleValidationService ruleValidationService;

    private final SpeechDictionary speechDictionary;

    public SpeechService(RuleValidationService ruleValidationService, SpeechDictionary speechDictionary) {
        this.ruleValidationService = ruleValidationService;
        this.speechDictionary = speechDictionary;
        try {
            // Set property as Kevin Dictionary
            System.setProperty(
                    "freetts.voices",
                    "com.sun.speech.freetts.en.us"
                            + ".cmu_us_kal.KevinVoiceDirectory");

            // Register Engine
            Central.registerEngineCentral(
                    "com.sun.speech.freetts"
                            + ".jsapi.FreeTTSEngineCentral");

            // Create a Synthesizer
            synthesizer
                    = Central.createSynthesizer(
                    new SynthesizerModeDesc(Locale.US));

            // Allocate synthesizer
            synthesizer.allocate();

            // Resume Synthesizer
            synthesizer.resume();

            // Speaks the given text
            // until the queue is empty.
            synthesizer.speakPlainText(
                    "GeeksforGeeks", null);
            synthesizer.waitEngineState(
                    Synthesizer.QUEUE_EMPTY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void speak(String textToSpeak) {
        if(synthesizer != null) {
            synthesizer.speakPlainText(textToSpeak, null);
        }
    }

    public void deallocate() {
        if(synthesizer != null) {
            try {
                synthesizer.deallocate();

            } catch (EngineException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void invalidated(Observable observable) {
        var sensor = (Sensor) observable;
        AtomicBoolean valid = new AtomicBoolean(false);
        var speechObject = speechDictionary.getSpeechByTrigger(sensor.getName());
        speechObject.getRules().forEach(rule -> {
            if(ruleValidationService.validate(sensor.getValue(),rule) && !valid.get()) {
                valid.set(true);
            }
        });
        if(valid.get()){
            speak(speechObject.getTextToSpeak(sensor.getValue()));
        }
    }
}
