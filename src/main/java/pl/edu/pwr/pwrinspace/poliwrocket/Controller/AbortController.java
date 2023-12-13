package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.addons.Switch;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;


public class AbortController extends BasicButtonSensorController {

    @FXML
    private Switch safeSwitch1;

    @FXML
    private Switch safeSwitch2;

    @FXML
    protected JFXButton abortButton;

    @Override
    @FXML
    protected void initialize() {
        super.initialize();
        abortButton.setDisable(true);

        safeSwitch1.setOnMouseClicked(actionEvent -> abortButton.setDisable(!(safeSwitch1.isActive() && safeSwitch2.isActive())));

        safeSwitch2.setOnMouseClicked(actionEvent -> abortButton.setDisable(!(safeSwitch1.isActive() && safeSwitch2.isActive())));
    }

    @Override
    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command) {
        return actionEvent -> {
            executorService.execute(() -> {
                if (safeSwitch1.isActive() && safeSwitch2.isActive()) {
                    SerialPortManager.getInstance().write(command);
                    for (Thread thread : Thread.getAllStackTraces().keySet()) {
                        if (thread.getName().equals(ThreadName.COUNTDOWN.getName())) {
                            thread.interrupt();
                        }
                    }
                }
            });
        };
    }


    @Override
    public void invalidated(Observable observable) {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
    }

    @Override
    protected void setUIBySensors() {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
    }

    @Override
    protected void buildVisualizationMap() {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
    }
}
