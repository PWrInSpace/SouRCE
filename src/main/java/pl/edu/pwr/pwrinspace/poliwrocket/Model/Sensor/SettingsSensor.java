package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;

public class SettingsSensor extends Sensor implements ISettingsSensor, ICommand {

    @Expose
    private double defaultValue;

    @Expose
    private String commandPrefixValue;

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
    public String getCommandValue() {
        return commandPrefixValue;
    }

    @Override
    public String getCommandTriggerKey() {
        return _destinationCommandPrefix + this.getDestination();
    }

    @Override
    public String getCommandDescription() {
        return this.getName();
    }
}
