package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.addons.Switch;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.CountdownThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;

import java.util.Collection;

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
    protected JFXButton qucikDistonectButton;

    @FXML
    protected JFXButton armingButton1;

    @FXML
    protected JFXButton armingButton2;

    @FXML
    protected JFXButton fireButton;

    @FXML
    private Switch safeSwitch5;

    @FXML
    protected Label countdownTimer;

    @FXML
    protected JFXButton stateButton1;

    @FXML
    protected JFXButton stateButton2;

    @FXML
    protected JFXButton stateButton3;

    @FXML
    protected JFXButton stateButton4;

    @FXML
    protected JFXButton stateButton5;

    @FXML
    protected JFXButton stateButton6;

    @FXML
    protected JFXButton stateButton7;

    private Thread countdownThread;

    private CountdownThread countdownTime;

    @FXML
    protected void initialize() {
        super.initialize();
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

        safeSwitch1.setOnMouseClicked(actionEvent -> {
            executorService.execute(() -> {
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
        });

        safeSwitch2.setOnMouseClicked(actionEvent -> {
            executorService.execute(() -> {
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
        });

        safeSwitch3.setOnMouseClicked(actionEvent -> {
            executorService.execute(() -> {
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
        });
        safeSwitch4.setOnMouseClicked(actionEvent -> {
            executorService.execute(() -> {
                if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive()) {
                    fireButton.setDisable(false);
                } else {
                    fireButton.setDisable(true);
                    safeSwitch5.setActive(false);
                }
                checkReset();
            });
        });
        safeSwitch5.setOnMouseClicked(actionEvent -> {
            executorService.execute(() -> {
                if (safeSwitch1.isActive() && safeSwitch2.isActive() && safeSwitch3.isActive() && safeSwitch4.isActive() && safeSwitch5.isActive()) {
                    fireButton.setDisable(false);
                } else {
                    fireButton.setDisable(true);
                    safeSwitch4.setActive(false);
                }
                checkReset();
            });
        });

        fireButton.setOnMouseClicked(mouseEvent -> {
            executorService.execute(() -> {
                if (countdownTime != null && (countdownThread == null || !countdownThread.isAlive())) {
                    countdownThread = new Thread(countdownTime, ThreadName.COUNTDOWN.getName());
                    countdownThread.setDaemon(true);
                    countdownThread.start();
                    commands.forEach(c -> {
                        if (c.getCommandTriggerKey().equals(fireButton.getId())) {
                            SerialPortManager.getInstance().write(c.getCommandValue());
                        }
                    });
                } else if (countdownTime != null && countdownThread != null && countdownThread.isAlive()) {
                    countdownTime.resetCountdown();
                    commands.forEach(c -> {
                        if (c.getCommandTriggerKey().equals(fireButton.getId())) {
                            SerialPortManager.getInstance().write(c.getCommandValue());
                        }
                    });
                    countdownTime.makeCanRun();
                }
            });
        });

    }

    @Override
    public void assignsCommands(Collection<ICommand> commands){
        Platform.runLater(() -> {
            this.commands.addAll(commands);

            for (ICommand command : commands) {
                if(!command.getCommandTriggerKey().equals(fireButton.getId())) {
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
        });
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
            /*safeSwitch2.setDisable(true);
            safeSwitch3.setDisable(true);
            safeSwitch4.setDisable(true);
            safeSwitch5.setDisable(true);*/
            countdownTime.resetCountdown();
        }
    }

    @Override
    protected void setUIBySensors() {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
    }
}
