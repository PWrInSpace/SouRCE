package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.CountdownThread;

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
    private Label countdownTimer;

    private Thread countdownThread;

    private CountdownThread countdownTime;
    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.START_CONTROL_CONTROLLER;
        countdownTime = new CountdownThread();
        countdownTime.addListener(this);
        qucikDistonectButton.setDisable(true);
        armingButton1.setDisable(true);
        armingButton2.setDisable(true);
        fireButton.setDisable(true);
//        safeSwitch2.setDisable(true);
//        safeSwitch3.setDisable(true);
//        safeSwitch4.setDisable(true);
//        safeSwitch5.setDisable(true);

        safeSwitch1.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive()) {
                qucikDistonectButton.setDisable(false);
                safeSwitch2.setDisable(false);
            } else {
                qucikDistonectButton.setDisable(true);
                armingButton1.setDisable(true);
                armingButton2.setDisable(true);
                fireButton.setDisable(true);
                safeSwitch2.setActive(false);
                safeSwitch3.setActive(false);
                safeSwitch4.setActive(false);
                safeSwitch5.setActive(false);
            }
            checkReset();
        });

        safeSwitch2.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive()) {
                armingButton1.setDisable(false);
                safeSwitch3.setDisable(false);
            } else {
                armingButton1.setDisable(true);
                armingButton2.setDisable(true);
                fireButton.setDisable(true);
                safeSwitch3.setActive(false);
                safeSwitch4.setActive(false);
                safeSwitch5.setActive(false);
            }
            checkReset();
        });
        safeSwitch3.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive()) {
                armingButton2.setDisable(false);
                safeSwitch4.setDisable(false);
                safeSwitch5.setDisable(false);
            } else {
                armingButton2.setDisable(true);
                fireButton.setDisable(true);
                safeSwitch4.setActive(false);
                safeSwitch5.setActive(false);
            }
            checkReset();
        });
        safeSwitch4.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive()) {
                fireButton.setDisable(false);
            } else {
                fireButton.setDisable(true);
                safeSwitch5.setActive(false);
            }
            checkReset();
        });
        safeSwitch5.setOnMouseClicked(actionEvent -> {
            if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive()) {
                fireButton.setDisable(false);
            } else {
                fireButton.setDisable(true);
                safeSwitch4.setActive(false);
            }
            checkReset();
        });

        fireButton.setOnMouseClicked(mouseEvent -> {
            if (countdownTime != null && (countdownThread == null || !countdownThread.isAlive())) {
                countdownThread = new Thread(countdownTime, "coundown");
                countdownThread.start();
            }

        });

    }

    @Override
    protected void setUIBySensors() {
        //its only button panel
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            countdownTimer.setText(((CountdownThread) observable).getFormattedTimeResult());
            if(-1000 <= ((CountdownThread) observable).getCountdownTime() && ((CountdownThread) observable).getCountdownTime() <= 0) {
                //SerialPortManager.getInstance().write("FIRE MESSAGE");
            }
        });
    }

    private void checkReset() {
        if (!(safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive())) {
            countdownTime.resetCountdown();
        }
    }
}
