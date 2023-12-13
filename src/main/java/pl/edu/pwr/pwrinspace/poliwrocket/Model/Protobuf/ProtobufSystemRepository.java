package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ProtobufSystemRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufSystemRepository.class);

    @Expose
    private HashMap<String, ProtobufSystem> systems = new HashMap<>();

    public ProtobufSystem getSystem(String key) {
        try {
            return systems.get(key);
        } catch (NullPointerException e) {
            logger.error("Interpreter not found in repository: {}",key);
            throw e;
        }
    }
}
