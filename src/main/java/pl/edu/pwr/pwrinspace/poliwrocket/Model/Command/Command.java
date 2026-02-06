package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Command<T> implements ICommand {
    @Expose
    protected T value;
    @Expose
    private boolean isFinal;
    @Expose
    private String trigger;
    @Expose
    private String description;
    @Expose
    protected String payload = "";
    @Expose
    protected CommandType commandType = CommandType.INPUT_COMMAND;
    @Expose
    private List<String> destinationControllerNames = new ArrayList<>();

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public String getCommandTriggerKey() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    @Override
    public String getCommandDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public List<String> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<String> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }

    @Override
    public byte[] getCommandValueAsBytes(boolean force) {
        return getCommandValueAsString().getBytes();
    }

    @Override
    public byte[] getCommandValueAsBytes() {
        return getCommandValueAsBytes(false);
    }

    @Override
    public String getCommandValueAsString() {
        return value.toString() + payload;
    }

    @Override
    public String toString() {
        return trigger;
    }

    @Override
    public int hashCode() {
        return trigger.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
