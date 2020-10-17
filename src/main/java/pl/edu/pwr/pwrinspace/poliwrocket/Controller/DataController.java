package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.time.Duration;
import java.util.HashMap;

public class DataController extends BasicSensorController {

    private static Duration DURATION = Duration.ofSeconds(10);

    @FXML
    private Tile dataGauge1;

    @FXML
    private Tile dataGauge2;

    @FXML
    private Tile dataGauge3;

    @FXML
    private Tile dataGauge4;

    @FXML
    private Tile dataGauge5;

    @FXML
    private Tile dataGauge6;

    @FXML
    private Tile dataGauge7;

    @FXML
    private Tile dataGauge8;

    HashMap<String, Tile> tileHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.DATA_CONTROLLER;

        tileHashMap.put(dataGauge1.getId(), dataGauge1);
        tileHashMap.put(dataGauge2.getId(), dataGauge2);
        tileHashMap.put(dataGauge3.getId(), dataGauge3);
        tileHashMap.put(dataGauge4.getId(), dataGauge4);
        tileHashMap.put(dataGauge5.getId(), dataGauge5);
        tileHashMap.put(dataGauge6.getId(), dataGauge6);
        tileHashMap.put(dataGauge7.getId(), dataGauge7);
        tileHashMap.put(dataGauge8.getId(), dataGauge8);
        dataGauge1.setVisible(false);
        dataGauge2.setVisible(false);
        dataGauge3.setVisible(false);
        dataGauge4.setVisible(false);
        dataGauge5.setVisible(false);
        dataGauge6.setVisible(false);
        dataGauge7.setVisible(false);
        dataGauge8.setVisible(false);
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
            } else {
                //TODO log error - nie istnieje tile o takiej nazwie
            }
        }
        if (!dataGauge2.visibleProperty().get()) {
            dataGauge1.setPrefWidth(dataGauge1.getPrefWidth() * 2 + 24);
        }
        if (!dataGauge4.visibleProperty().get()) {
            dataGauge3.setPrefWidth(dataGauge3.getPrefWidth() * 2 + 24);

        }
        if (!dataGauge6.visibleProperty().get()) {
            dataGauge5.setPrefWidth(dataGauge5.getPrefWidth() * 2 + 24);

        }
        if (!dataGauge8.visibleProperty().get()) {
            dataGauge7.setPrefWidth(dataGauge7.getPrefWidth() * 2 + 24);

        }

    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            Platform.runLater(() -> tileHashMap.get(sensor.getDestination()).setValue(sensor.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
