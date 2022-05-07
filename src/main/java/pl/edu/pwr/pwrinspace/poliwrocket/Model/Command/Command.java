package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Command implements ICommand {

    @Expose
    private String value;

    @Expose
    private String trigger;

    @Expose
    private String description;

    @Expose
    private List<String> destinationControllerNames = new ArrayList<>();

    public Command(String value, String trigger) {
        this.value = value;
        this.trigger = trigger;
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

    @Override
    public String getCommandValue() {
        return value;
    }

    @Override
    public String getCommandTriggerKey() {
        return trigger;
    }

    @Override
    public String getCommandDescription() {
        return description;
    }

    public List<String> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<String> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }
}
