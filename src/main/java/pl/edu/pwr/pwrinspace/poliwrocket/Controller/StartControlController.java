package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;

public class StartControlController extends BasicButtonSensorController {

    @FXML
    private Switch safeSwitch1;

    @FXML
    private Switch safeSwitch2;

    @FXML
    private Switch safeSwitch3;

    @FXML
    private Switch safeSwitch4;

    @FXML
    private Button qucikDistonectButton;

    @FXML
    private Button armingButton1;

    @FXML
    private Button armingButton2;

    @FXML
    private Button fireButton;

    @FXML
    private Switch safeSwitch5;

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.START_CONTROL_CONTROLLER;

        qucikDistonectButton.setDisable(true);
        armingButton1.setDisable(true);
        armingButton2.setDisable(true);
        fireButton.setDisable(true);

        safeSwitch1.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive()) {
                qucikDistonectButton.setDisable(false);
            } else {
                qucikDistonectButton.setDisable(true);
            }
        });

        safeSwitch2.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive()) {
                armingButton1.setDisable(false);
            } else {
                armingButton1.setDisable(true);
            }
        });
         safeSwitch3.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive()) {
                armingButton2.setDisable(false);
            } else {
                armingButton2.setDisable(true);
            }
        });
         safeSwitch4.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive()) {
                fireButton.setDisable(false);
            } else {
                fireButton.setDisable(true);
            }
        });

    }

    @Override
    protected void setUIBySensors() {
        //its only button panel
    }

    @Override
    public void invalidated(Observable observable) {
        //its only button panel
        throw new UnsupportedOperationException();
    }
}
