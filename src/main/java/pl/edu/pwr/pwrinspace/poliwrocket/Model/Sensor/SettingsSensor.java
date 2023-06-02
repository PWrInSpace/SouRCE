package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;

public class SettingsSensor extends Sensor implements ISettingsSensor, ICommand {

    @Expose
    private double defaultValue;

    @Expose
    private String commandPrefixValue;

    @Expose
    private String payload;

    private static final String _destinationCommandPrefix = "button";
    private static final String _destinationInoutPrefix = "button";

    @Override
    public double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getInputKey() {
        return _destinationInoutPrefix + this.getDestination();
    }

    @Override
    public String getCommandValueAsString() {
        return commandPrefixValue;
    }

    @Override
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String getCommandTriggerKey() {
        return _destinationCommandPrefix + this.getDestination();
    }

    @Override
    public String getCommandDescription() {
        return this.getName();
    }

    @Override
    public byte[] getCommandValueAsBytes(boolean force) {
        return new byte[0];
    }

    @Override
    public byte[] getCommandValueAsBytes() {
        return getCommandValueAsBytes(false);
    }
}
