package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.CountdownThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;

import java.util.Collection;

public class StartControlController extends BasicButtonController {

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

    @FXML
    private Button stateButton1;

    @FXML
    private Button stateButton2;

    @FXML
    private Button stateButton3;

    @FXML
    private Button stateButton4;

    @FXML
    private Button stateButton5;

    @FXML
    private Button stateButton6;

    @FXML
    private Button stateButton7;

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
        safeSwitch2.setDisable(true);
        safeSwitch3.setDisable(true);
        safeSwitch4.setDisable(true);
        safeSwitch5.setDisable(true);
        buttonHashMap.put(qucikDistonectButton.getId(),qucikDistonectButton);
        buttonHashMap.put(armingButton1.getId(),armingButton1);
        buttonHashMap.put(armingButton2.getId(),armingButton2);

        buttonHashMap.put(stateButton1.getId(),stateButton1);
        buttonHashMap.put(stateButton2.getId(),stateButton2);
        buttonHashMap.put(stateButton3.getId(),stateButton3);
        buttonHashMap.put(stateButton4.getId(),stateButton4);
        buttonHashMap.put(stateButton5.getId(),stateButton5);
        buttonHashMap.put(stateButton6.getId(),stateButton6);
        buttonHashMap.put(stateButton7.getId(),stateButton7);

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
                countdownThread = new Thread(countdownTime, ThreadName.COUNTDOWN.getName());
                countdownThread.setDaemon(true);
                countdownThread.start();
                commands.forEach(c -> {
                    if(c.getCommandTriggerKey().equals(fireButton.getId())) {
                        SerialPortManager.getInstance().write(c.getCommandValue());
                    }
                });
            } else if(countdownTime != null && countdownThread != null && countdownThread.isAlive()) {
                countdownTime.resetCountdown();
                commands.forEach(c -> {
                    if(c.getCommandTriggerKey().equals(fireButton.getId())) {
                        SerialPortManager.getInstance().write(c.getCommandValue());
                    }
                });
                countdownTime.makeCanRun();
            }
        });

    }

    @Override
    public void assignsCommands(Collection<ICommand> commands){
        this.commands.addAll(commands);

        for (ICommand command : commands) {
            var button = buttonHashMap.get(command.getCommandTriggerKey());
            if (button != null) {
                button.setOnAction(handleButtonsClickByCommand(button, command));
                button.setVisible(true);
                if(command.getCommandTriggerKey().contains("state")) {
                    button.setText(command.getCommandDescription());
                }
            } else {
                logger.warn("Trigger not found: {} , it`s maybe correct for fire button!", command.getCommandTriggerKey());
            }
        }

    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            countdownTimer.setText(((CountdownThread) observable).getFormattedTimeResult());
//            if(-10 <= ((CountdownThread) observable).getCountdownTime() && ((CountdownThread) observable).getCountdownTime() <= 0) {
//                commands.forEach(c -> {
//                    if(c.getCommandTriggerKey().equals(fireButton.getId())) {
//                        SerialPortManager.getInstance().write(c.getCommandValue());
//                    }
//                });
//            }
        });
    }

    private void checkReset() {
        if (!(safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive())) {
            countdownTime.resetCountdown();
        }
    }
}
