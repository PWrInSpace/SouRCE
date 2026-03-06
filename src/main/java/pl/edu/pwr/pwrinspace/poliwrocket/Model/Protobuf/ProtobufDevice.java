package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProtobufDevice {
    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("sudoMask")
    private String sudoMask;

    public int getSudoMask() {
        return Integer.decode(sudoMask);
    }

    public int getDeviceId() {
        return Integer.decode(deviceId);
    }
}
