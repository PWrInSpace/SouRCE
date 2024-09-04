package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content;

import com.google.gson.annotations.Expose;

public class SimpleProtobufContent extends BaseProtobufContent {
    @Expose
    private String loraDevId;

    @Expose
    private String sysDevId;

    @Expose
    private String sudoMask;

    public SimpleProtobufContent(String command, String loraDevId, String sysDevId, String sudoMask) {
        super(command);
        this.loraDevId = loraDevId;
        this.sysDevId = sysDevId;
        this.sudoMask = sudoMask;
    }

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
