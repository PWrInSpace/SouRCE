package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

public class ProtobufSystemRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufSystemRepository.class);

    @JsonProperty("systems")
    private HashMap<String, ProtobufSystem> systems = new HashMap<>();

    public ProtobufSystem getSystem(String key) {
        try {
            return systems.get(key);
        } catch (NullPointerException e) {
            logger.error("Interpreter not found in repository: {}",key);
            throw e;
        }
    }

    public Set<String> getSystemSet() {
        return systems.keySet();
    }
 }
