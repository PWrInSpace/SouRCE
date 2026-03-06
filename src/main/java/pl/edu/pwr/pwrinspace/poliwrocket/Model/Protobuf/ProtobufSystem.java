package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProtobufSystem {

    @JsonProperty("systemDeviceId")
    private String systemDeviceId;

    public int getSystemDeviceId() {
        return Integer.decode(systemDeviceId);
    }
}
