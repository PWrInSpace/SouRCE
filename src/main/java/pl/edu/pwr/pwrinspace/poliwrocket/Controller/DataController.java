package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IAlert;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.time.Duration;
import java.util.HashMap;

public class DataController extends BasicSensorController {

    private static final int _duration = 30;

    private static final Duration DURATION = Duration.ofSeconds(_duration);

    @FXML
    private Tile dataGauge1;

    @FXML
    private Tile dataGauge2;

    @FXML
    private Tile dataGauge3;

    HashMap<String, Tile> tileHashMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.DATA_CONTROLLER;

        tileHashMap.put(dataGauge1.getId(), dataGauge1);
        tileHashMap.put(dataGauge2.getId(), dataGauge2);
        tileHashMap.put(dataGauge3.getId(), dataGauge3);

        tileHashMap.forEach((s, tile) -> tile.setVisible(false));
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
                if(!sensor.isBoolean()) {
                    UIThreadManager.getInstance().addActiveSensor();
                }
                //tile.setAveragingPeriod(_duration * Configuration.getInstance().FPS);
            } else {
                logger.error("Wrong UI binding - destination not found: {}",sensor.getDestination());
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addNormal(() -> {
                var gauge = tileHashMap.get(sensor.getDestination());
                if(sensor instanceof IAlert) {
                    if(((IAlert)sensor).getAlert()) {
                        gauge.setValueColor(Color.RED);
                    } else {
                        gauge.setValueColor(Color.WHITE);
                    }
                }
                gauge.setValue(sensor.getValue());

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
