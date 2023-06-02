package pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf;

import com.google.gson.annotations.Expose;

public class ProtobufDevice {
    @Expose
    private String deviceId;

    @Expose
    private String sudoMask;

    public int getSudoMask() {
        return Integer.decode(sudoMask);
    }

    public int getDeviceId() {
        return Integer.decode(deviceId);
    }
}
