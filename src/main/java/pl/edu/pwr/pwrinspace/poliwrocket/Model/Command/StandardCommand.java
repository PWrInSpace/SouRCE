package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import java.util.List;

public class StandardCommand extends Command<String> {

    public StandardCommand() {
    }

    public StandardCommand(String trigger, String value, String description, List<String> destinationControllerNames, boolean isFinal, String payload) {
        super(trigger, value, description, destinationControllerNames, isFinal, payload);
    }
}
