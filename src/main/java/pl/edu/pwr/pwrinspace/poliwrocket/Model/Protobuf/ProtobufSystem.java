package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.google.gson.annotations.Expose;

public class ProtobufSystem {

    @Expose
    private String systemDeviceId;

    public int getSystemDeviceId() {
        return Integer.decode(systemDeviceId);
    }
}
