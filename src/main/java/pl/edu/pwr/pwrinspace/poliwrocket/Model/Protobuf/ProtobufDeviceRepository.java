package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ProtobufDeviceRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufDeviceRepository.class);

    @Expose
    private HashMap<String, ProtobufDevice> devices = new HashMap<>();

    public ProtobufDevice getDevice(String key) {
        try {
            return devices.get(key);
        } catch (NullPointerException e) {
            logger.error("Interpreter not found in repository: {}",key);
            throw e;
        }
    }
}
