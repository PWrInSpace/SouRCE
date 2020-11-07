package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.javatuples.Triplet;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.util.HashMap;

public class PowerController extends BasicSensorController {

    @FXML
    private Gauge powerGauge4;

    @FXML
    private Gauge powerGauge5;

    @FXML
    private Gauge powerGauge6;

    @FXML
    private Gauge powerGauge3;

    @FXML
    private Gauge powerGauge7;

    @FXML
    private Gauge powerGauge2;

    @FXML
    private Gauge powerGauge1;

    @FXML
    private Label powerLabel7;

    @FXML
    private Label powerLabel6;

    @FXML
    private Label powerLabel5;

    @FXML
    private Label powerLabel4;

    @FXML
    private Label powerLabel3;

    @FXML
    private Label powerLabel2;

    @FXML
    private Label powerLabel1;
    @FXML
    private Label powerValue1;

    @FXML
    private Label powerValue2;

    @FXML
    private Label powerValue3;

    @FXML
    private Label powerValue4;

    @FXML
    private Label powerValue5;

    @FXML
    private Label powerValue6;

    @FXML
    private Label powerValue7;

    private HashMap<String, Triplet<Gauge, Label, Label>> powerHashMap = new HashMap<>();


    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.POWER_CONTROLLER;

        assert powerGauge4 != null : "fx:id=\"powerGauge4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge5 != null : "fx:id=\"powerGauge5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge6 != null : "fx:id=\"powerGauge6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge3 != null : "fx:id=\"powerGauge3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge7 != null : "fx:id=\"powerGauge7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge2 != null : "fx:id=\"powerGauge2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge1 != null : "fx:id=\"powerGauge1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel7 != null : "fx:id=\"powerLabel7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel6 != null : "fx:id=\"powerLabel6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel5 != null : "fx:id=\"powerLabel5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel4 != null : "fx:id=\"powerLabel4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel3 != null : "fx:id=\"powerLabel3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel2 != null : "fx:id=\"powerLabel2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel1 != null : "fx:id=\"powerLabel1\" was not injected: check your FXML file 'FlyControlView.fxml'.";

        powerHashMap.put(powerGauge1.getId(), new Triplet<>(powerGauge1, powerLabel1, powerValue1));
        powerHashMap.put(powerGauge2.getId(), new Triplet<>(powerGauge2, powerLabel2, powerValue2));
        powerHashMap.put(powerGauge3.getId(), new Triplet<>(powerGauge3, powerLabel3, powerValue3));
        powerHashMap.put(powerGauge4.getId(), new Triplet<>(powerGauge4, powerLabel4, powerValue4));
        powerHashMap.put(powerGauge5.getId(), new Triplet<>(powerGauge5, powerLabel5, powerValue5));
        powerHashMap.put(powerGauge6.getId(), new Triplet<>(powerGauge6, powerLabel6, powerValue6));
        powerHashMap.put(powerGauge7.getId(), new Triplet<>(powerGauge7, powerLabel7, powerValue7));

        powerHashMap.forEach((s, tuple) -> {
            tuple.getValue0().setVisible(false);
            tuple.getValue1().setVisible(false);
            tuple.getValue2().setVisible(false);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISensor) {
            var sensor = ((ISensor) observable);
            Platform.runLater(() -> {
                powerHashMap.get(sensor.getDestination()).getValue0().setValue(Math.round((sensor.getValue() - sensor.getMinRange())/(sensor.getMaxRange()-sensor.getMinRange())*1000)/10.0);
                powerHashMap.get(sensor.getDestination()).getValue2().setText((Math.round(sensor.getValue()*100)/100.0) + sensor.getUnit());
            });

        }
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var triplet = powerHashMap.get(sensor.getDestination());
            if (triplet != null) {
                triplet.getValue0().setVisible(true);
//                pair.getValue0().setMaxValue(sensor.getMaxRange());
//                pair.getValue0().setMinValue(sensor.getMinRange());
               System.out.println( triplet.getValue0().getThresholdColor());
                triplet.getValue0().setThresholdColor(Color.BLUE);
                triplet.getValue0().setValueVisible(true);
//                pair.getValue0().setThreshold((sensor.getMinRange()+0.3)/10);
//                System.out.println("tre " + pair.getValue0().getThreshold());
                triplet.getValue0().setThresholdVisible(true);
//                System.out.println("tre " + (sensor.getMinRange()+0.3)/10);
//                System.out.println(pair.getValue0().getMaxValue());
                triplet.getValue0().setUnit(sensor.getUnit());
                triplet.getValue1().setVisible(true);
                triplet.getValue1().setText(sensor.getName());
                triplet.getValue2().setVisible(true);
            } else {
                //TODO log error - nie istnieje tile o takiej nazwie
            }
        }
    }
}
