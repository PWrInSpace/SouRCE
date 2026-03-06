package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

public class ProtobufDeviceRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufDeviceRepository.class);

    @JsonProperty("devices")
    private HashMap<String, ProtobufDevice> devices = new HashMap<>();

    public ProtobufDevice getDevice(String key) {
        try {
            return devices.get(key);
        } catch (NullPointerException e) {
            logger.error("Interpreter not found in repository: {}",key);
            throw e;
        }
    }

    public Set<String> getDeviceSet() {
        return devices.keySet();
    }
}
