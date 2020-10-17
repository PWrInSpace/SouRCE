package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import gnu.io.NRSerialPort;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPortManager;

public class ConnectionController extends BasicButtonSensorController {

    private ControllerNameEnum controllerNameEnum = ControllerNameEnum.CONNECTION_CONTROLLER;

    @FXML
    private ComboBox<String> serialPorts;

    @FXML
    private Button connectionButton;

    @FXML
    private TextField baudRate;

    @FXML
    private Label connectionStatus;

    private ObservableList<String> availableSerialPorts = FXCollections.observableArrayList();


    @FXML
    void initialize() {

        serialSetup();

        serialPorts.setOnMouseClicked(mouseEvent -> {
            serialSetup();
        });

        connectionButton.setOnMouseClicked(mouseEvent -> {
            if (availableSerialPorts.isEmpty()) {
                connectionStatus.setText("Error: no ports");
            } else {
                if (!SerialPortManager.getInstance().isPortOpen) {
                    connectionStatus.setText("Connecting");
                    baudRate.setDisable(true);
                    serialPorts.setDisable(true);
                    SerialPortManager.getInstance().initialize(serialPorts.getValue(), Integer.parseInt(baudRate.getText()));
                } else {
                    connectionStatus.setText("Disconnecting");
                    SerialPortManager.getInstance().close();
                }
            }
        });
    }

    private void serialSetup() {
        availableSerialPorts.clear();
        availableSerialPorts.addAll(NRSerialPort.getAvailableSerialPorts());
        serialPorts.setItems(availableSerialPorts);
        serialPorts.setValue(!availableSerialPorts.isEmpty() ? availableSerialPorts.get(0) : "No ports available");
    }

    @Override
    protected void setUIBySensors() {

    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof SerialPortManager) {
            if (((SerialPortManager) observable).isPortOpen) {
                connectionStatus.setText("Connected");
                baudRate.setDisable(true);
                serialPorts.setDisable(true);
            } else {
                if (connectionStatus.getText().equals("Connecting")) {
                    connectionStatus.setText("Failed");
                } else {
                    connectionStatus.setText("Disconnected");
                }
                baudRate.setDisable(false);
                serialPorts.setDisable(false);
            }
        }
    }

    @Override
    public ControllerNameEnum getControllerNameEnum() {
        return controllerNameEnum;
    }
}
