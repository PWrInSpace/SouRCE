package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import java.util.LinkedList;
import java.util.List;

public class CommandsConfig extends BaseSaveModel {
    @Expose public List<Command> commandsList = new LinkedList<>();

    public CommandsConfig() {
        super(Configuration.CONFIG_PATH, "CommandConfig.json");
    }
}