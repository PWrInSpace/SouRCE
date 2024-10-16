package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content;

import com.google.gson.annotations.Expose;

public class ProtobufContent extends BaseProtobufContent {
    @Expose
    private String device;

    @Expose
    private String system;

    public String getDevice() {
        return device;
    }

    public String getSystem() {
        return system;
    }

    @Override
    public String toString() {
        return super.toString() + ", device: " + device + ", system: " + system;
    }
}
