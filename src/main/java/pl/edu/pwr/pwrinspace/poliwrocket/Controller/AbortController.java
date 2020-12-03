package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPortManager;


public class AbortController extends BasicButtonController {

    @FXML
    private Switch safeSwitch1;

    @FXML
    private Switch safeSwitch2;

    @FXML
    private Button abortButton;

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.ABORT_CONTROLLER;

        abortButton.setDisable(true);
        buttonHashMap.put(abortButton.getId(), abortButton);

        safeSwitch1.setOnMouseClicked(actionEvent -> abortButton.setDisable(!(safeSwitch1.isActive() && safeSwitch2.isActive())));

        safeSwitch2.setOnMouseClicked(actionEvent -> abortButton.setDisable(!(safeSwitch1.isActive() && safeSwitch2.isActive())));
    }

    @Override
    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command) {
        button.setVisible(true);
        if (safeSwitch1.isActive() && safeSwitch2.isActive()) {
            return actionEvent -> SerialPortManager.getInstance().write(command.getCommandValue());
        }
        return actionEvent -> {};
    }


    @Override
    public void invalidated(Observable observable) {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
    }
}
