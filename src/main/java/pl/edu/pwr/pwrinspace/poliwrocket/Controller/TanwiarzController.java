package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
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
    private Tile dataGauge1;

    @FXML
    private Tile dataGauge2;

    @FXML
    private Tile dataGauge3;

   /* @FXML
    private Tile dataGauge4;

    @FXML
    private Tile dataGauge5;*/

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

   /* @FXML
    private Button button4;

    @FXML
    private Button button5;*/

    @FXML
    private Label ratio1;

    @FXML
    private Label ratio2;

    @FXML
    private Label ratio3;

    HashMap<String, Tile> tileHashMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(TanwiarzController.class);

    protected HashMap<String, String> buttonTileHashMap = new HashMap<>();

    HashMap<String, Label> labelHashMap = new HashMap<>();


    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.TANWIARZ_CONTROLLER;

        tileHashMap.put(dataGauge1.getId(), dataGauge1);
        tileHashMap.put(dataGauge2.getId(), dataGauge2);
        tileHashMap.put(dataGauge3.getId(), dataGauge3);
        /*tileHashMap.put(dataGauge4.getId(), dataGauge4);
        tileHashMap.put(dataGauge5.getId(), dataGauge5);*/

        buttonHashMap.put(button1.getId(), button1);
        buttonHashMap.put(button2.getId(), button2);
        buttonHashMap.put(button3.getId(), button3);
      /*  buttonHashMap.put(button4.getId(), button4);
        buttonHashMap.put(button5.getId(), button5);*/

        buttonTileHashMap.put(button1.getId(), dataGauge1.getId());
        buttonTileHashMap.put(button2.getId(), dataGauge2.getId());
        buttonTileHashMap.put(button3.getId(), dataGauge3.getId());
       /* buttonTileHashMap.put(button4.getId(), dataGauge4.getId());
        buttonTileHashMap.put(button5.getId(), dataGauge5.getId());*/

        labelHashMap.put(dataGauge1.getId(), ratio1);
        labelHashMap.put(dataGauge2.getId(), ratio2);
        labelHashMap.put(dataGauge3.getId(), ratio3);

    }

    @Override
    protected void setUIBySensors() {

        for (ISensor sensor : sensors) {
            var tile = tileHashMap.get(sensor.getDestination());
            if (tile != null) {
                tile.setVisible(true);
                tile.setMaxValue(sensor.getMaxRange());
                tile.setMinValue(sensor.getMinRange());
                tile.setTitle(sensor.getName());
                tile.setUnit(sensor.getUnit());
                tile.setAverageVisible(true);
                tile.setSmoothing(true);
                tile.setTimePeriod(DURATION);
                tile.setAveragingPeriod(_duration);
                if (!sensor.isBoolean()) {
                    UIThreadManager.getInstance().addActiveSensor();
                }
                //tile.setAveragingPeriod(_duration * Configuration.getInstance().FPS);
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
            String tileKey = buttonTileHashMap.get(button.getId());
            var sensors = Configuration.getInstance().sensorRepository.getAllBasicSensors().values().stream().filter((sensor -> sensor.getDestinationControllerNames().contains(controllerNameEnum) && sensor.getDestination().equals(tileKey))).toArray();
            if (sensors.length > 0 && sensors[0] instanceof TanwiarzSensor) {
                var sensor = ((TanwiarzSensor) sensors[0]);
                var weight = Integer.toString(sensor.getCalibrationValue());
                while (weight.length() < 4) {
                    weight = "0" + weight;
                }
                SerialPortManager.getInstance().write(command.getCommandValue() + ";" + weight);
                sensor.startCalibration();
            }
        };
    }
}
