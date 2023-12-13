package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content;

import com.google.gson.annotations.Expose;

public class SimpleProtobufContent extends BaseProtobufContent {
    @Expose
    private String loraDevId;

    @Expose
    private String sysDevId;

    @Expose
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
