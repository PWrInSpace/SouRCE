package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleProtobufContent extends BaseProtobufContent {
    @JsonProperty("loraDevId")
    private String loraDevId;

    @JsonProperty("sysDevId")
    private String sysDevId;

    @JsonProperty("sudoMask")
    private String sudoMask;

    public String getLoraDevId() {
        return loraDevId;
    }

    public String getSysDevId() {
        return sysDevId;
    }

    public String getSudoMask() {
        return sudoMask;
    }

    @Override
    public String toString() {
        return super.toString() + ", loraDevId: " + loraDevId + ", sysDevId: " + sysDevId;
    }
}
