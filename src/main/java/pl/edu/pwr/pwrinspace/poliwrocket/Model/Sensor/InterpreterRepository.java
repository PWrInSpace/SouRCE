package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class InterpreterRepository {

    private static final Logger logger = LoggerFactory.getLogger(InterpreterRepository.class);

    @Expose
    private HashMap<String, CodeInterpreter> interpreters = new HashMap<>();

    public CodeInterpreter getInterpreter(String key) {
        try {
            return interpreters.get(key);
        } catch (NullPointerException e) {
            logger.error("Interpreter not found in repository: {}",key);
            throw e;
        }
    }

    public void addInterpreter(String key, CodeInterpreter interpreter) {
        interpreters.put(key,interpreter);
    }

    public HashMap<String, CodeInterpreter> getRepositorySet() {
        return interpreters;
    }
}
