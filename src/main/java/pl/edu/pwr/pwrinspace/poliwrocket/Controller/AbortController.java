package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPortManager;


public class AbortController extends BasicButtonController {

    private ControllerNameEnum controllerNameEnum = ControllerNameEnum.ABORT_CONTROLLER;

    @FXML
    private Switch safeSwitch1;

    @FXML
    private Switch safeSwitch2;

    @FXML
    private Button abortButton;

    @FXML
    void initialize() {
        abortButton.setDisable(true);
        buttonHashMap.put(abortButton.getId(), abortButton);

        safeSwitch1.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive()) {
                abortButton.setDisable(false);
            } else {
                abortButton.setDisable(true);
            }
        });

        safeSwitch2.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive()) {
                abortButton.setDisable(false);
            } else {
                abortButton.setDisable(true);
            }
        });
    }

    @Override
    public ControllerNameEnum getControllerNameEnum() {
        return controllerNameEnum;
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

    }
}
