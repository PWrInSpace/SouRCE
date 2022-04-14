package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.Tile;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.TanwiarzSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.time.Duration;
import java.util.HashMap;

public class TanwiarzController extends BasicButtonSensorController {

    private static final int _duration = 30;

    private static final Duration DURATION = Duration.ofSeconds(_duration);

    @FXML
    protected Tile dataGauge1;

    @FXML
    protected Tile dataGauge2;

    @FXML
    protected Tile dataGauge3;

    @FXML
    protected JFXButton button1;

    @FXML
    protected JFXButton button2;

    @FXML
    protected JFXButton button3;

    @FXML
    protected Label ratio1;

    @FXML
    protected Label ratio2;

    @FXML
    protected Label ratio3;

    protected HashMap<String, String> buttonTileHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        super.buildVisualizationMap();

        buttonTileHashMap.put(button1.getId(), dataGauge1.getId());
        buttonTileHashMap.put(button2.getId(), dataGauge2.getId());
        buttonTileHashMap.put(button3.getId(), dataGauge3.getId());

        labelHashMap.put(dataGauge1.getId(), ratio1);
        labelHashMap.put(dataGauge2.getId(), ratio2);
        labelHashMap.put(dataGauge3.getId(), ratio3);
    }

    @Override
    protected void setUIBySensors() {
        super.setUIBySensors();
        for (ISensor sensor : sensors) {
            var label = labelHashMap.get(sensor.getDestination());
            if (label != null) {
                label.setText(sensor.getName());
                label.setVisible(true);
            } else {
                logger.error("Wrong UI binding - destination not found: {}", sensor.getDestination());
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((TanwiarzSensor) observable);
            UIThreadManager.getInstance().addNormal(() -> {
                tileHashMap.get(sensor.getDestination()).setValue(sensor.getValue());
                labelHashMap.get(sensor.getDestination()).setText(Double.toString(sensor.getRatio()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command) {
        return actionEvent -> {
            executorService.execute(() -> {
                String tileKey = buttonTileHashMap.get(button.getId());
                var sensors = Configuration.getInstance().sensorRepository.getAllBasicSensors().values().stream().filter((sensor -> sensor.getDestinationControllerNames().contains(getControllerName()) && sensor.getDestination().equals(tileKey))).toArray();
                if (sensors.length > 0 && sensors[0] instanceof TanwiarzSensor) {
                    var sensor = ((TanwiarzSensor) sensors[0]);
                    var weight = Integer.toString(sensor.getCalibrationValue());
                    while (weight.length() < 4) {
                        weight = "0" + weight;
                    }
                    SerialPortManager.getInstance().write(command.getCommandValue() + ";" + weight);
                    sensor.startCalibration();
                }
            });
        };
    }
}
