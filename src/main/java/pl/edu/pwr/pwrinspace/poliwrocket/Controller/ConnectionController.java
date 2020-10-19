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
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.NotificationThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;

import java.util.List;

public class ConnectionController extends BasicButtonSensorController {

    @FXML
    private ComboBox<String> serialPorts;

    @FXML
    private Button connectionButton;

    @FXML
    private TextField baudRate;

    @FXML
    private Label connectionStatus;

    @FXML
    private ComboBox<String> notifications;

    @FXML
    private Button sendNotification;

    @FXML
    private Label notificationStatus;

    @FXML
    private Button threadButton;

    @FXML
    private Label threadStatus;

    private ObservableList<String> availableSerialPorts = FXCollections.observableArrayList();

    private ObservableList<String> availableNotifications = FXCollections.observableArrayList();

    private NotificationSendService notificationSendService;

    private NotificationThread notificationThreadRunnable;

    private Thread notificationThread;

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.CONNECTION_CONTROLLER;

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

        sendNotification.setOnMouseClicked(mouseEvent -> {
            if (notificationSendService != null) {
                notificationSendService.sendNotification(notifications.getValue());
            } else {
                sendNotification.setDisable(true);
                notificationStatus.setText("Service error");
            }
        });

        threadButton.setOnMouseClicked(mouseEvent -> {
            if (notificationThreadRunnable != null && (notificationThread == null || !notificationThread.isAlive())) {
                notificationThread = new Thread(notificationThreadRunnable, ThreadName.DISCORD_NOTIFICATION.getName());
                notificationThread.start();
                threadStatus.setText("Running");
            } else if (notificationThread != null && notificationThread.isAlive()) {
                notificationThread.interrupt();
                threadStatus.setText("Interrupted");
            }
        });
    }

    public void injectNotification(NotificationSendService notificationSendService, List<String> notificationsList, NotificationThread notificationThreadRunnable) {
        this.notificationSendService = notificationSendService;
        this.availableNotifications.clear();
        this.availableNotifications.addAll(notificationsList);
        notifications.setItems(availableNotifications);
        if (!notificationsList.isEmpty()) {
            notifications.setValue(notificationsList.get(0));
        }
        this.notificationThreadRunnable = notificationThreadRunnable;
    }

    private void serialSetup() {
        availableSerialPorts.clear();
        availableSerialPorts.addAll(NRSerialPort.getAvailableSerialPorts());
        serialPorts.setItems(availableSerialPorts);
        serialPorts.setValue(!availableSerialPorts.isEmpty() ? availableSerialPorts.get(0) : "No ports available");
    }

    @Override
    protected void setUIBySensors() {
        //TODO implement for signal
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
        } else if (observable instanceof INotification) {
            boolean status = ((INotification) observable).isConnected();
            sendNotification.setDisable(!status);
            notifications.setDisable(!status);
            threadButton.setDisable(!status);
            notificationStatus.setText(status ? "Connected" : "Not connected");
            threadStatus.setText(status ? "Not running" : "Not enabled");
        }
    }
}
