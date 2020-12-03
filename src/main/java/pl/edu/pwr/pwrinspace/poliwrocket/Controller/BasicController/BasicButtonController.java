package pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPortManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class BasicButtonController extends BasicController {

    protected HashMap<String,Button> buttonHashMap = new HashMap<>();

    protected HashSet<ICommand> commands = new HashSet<>();

    public void assignsCommands(Collection<ICommand> commands){
        this.commands.addAll(commands);

        for (ICommand command : commands) {
            var button = buttonHashMap.get(command.getCommandTriggerKey());
            button.setOnAction(handleButtonsClickByCommand(button,command));
        }

    }

    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command){
        button.setVisible(true);

        return actionEvent -> SerialPortManager.getInstance().write(command.getCommandValue());
    }

}
