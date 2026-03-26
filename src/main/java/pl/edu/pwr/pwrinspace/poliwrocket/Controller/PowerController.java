package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.javatuples.Quartet;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class PowerController extends BaseSensorController {
    @FXML
    private Gauge powerGauge1;
    @FXML
    private Gauge powerGauge2;
    @FXML
    private Gauge powerGauge3;
    @FXML
    private Gauge powerGauge4;
    @FXML
    private Gauge powerGauge5;
    @FXML
    private Gauge powerGauge6;
    @FXML
    private Gauge powerGauge7;
    @FXML
    private Gauge powerGauge8;

    @FXML
    protected Label powerLabel1;
    @FXML
    protected Label powerLabel2;
    @FXML
    protected Label powerLabel3;
    @FXML
    protected Label powerLabel4;
    @FXML
    protected Label powerLabel5;
    @FXML
    protected Label powerLabel6;
    @FXML
    protected Label powerLabel7;
    @FXML
    protected Label powerLabel8;

    @FXML
    protected Label powerValue1;
    @FXML
    protected Label powerValue2;
    @FXML
    protected Label powerValue3;
    @FXML
    protected Label powerValue4;
    @FXML
    protected Label powerValue5;
    @FXML
    protected Label powerValue6;
    @FXML
    protected Label powerValue7;
    @FXML
    protected Label powerValue8;

    @FXML
    protected Label powerConsumption1;
    @FXML
    protected Label powerConsumption2;
    @FXML
    protected Label powerConsumption3;
    @FXML
    protected Label powerConsumption4;
    @FXML
    protected Label powerConsumption5;
    @FXML
    protected Label powerConsumption6;
    @FXML
    protected Label powerConsumption7;
    @FXML
    protected Label powerConsumption8;

    private final HashMap<String, Quartet<Gauge, Label, Label, Label>> powerHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        powerHashMap.put(powerGauge1.getId(), new Quartet<>(powerGauge1, powerLabel1, powerValue1, powerConsumption1));
        powerHashMap.put(powerGauge2.getId(), new Quartet<>(powerGauge2, powerLabel2, powerValue2, powerConsumption2));
        powerHashMap.put(powerGauge3.getId(), new Quartet<>(powerGauge3, powerLabel3, powerValue3, powerConsumption3));
        powerHashMap.put(powerGauge4.getId(), new Quartet<>(powerGauge4, powerLabel4, powerValue4, powerConsumption4));
        powerHashMap.put(powerGauge5.getId(), new Quartet<>(powerGauge5, powerLabel5, powerValue5, powerConsumption5));
        powerHashMap.put(powerGauge6.getId(), new Quartet<>(powerGauge6, powerLabel6, powerValue6, powerConsumption6));
        powerHashMap.put(powerGauge7.getId(), new Quartet<>(powerGauge7, powerLabel7, powerValue7, powerConsumption7));
        powerHashMap.put(powerGauge8.getId(), new Quartet<>(powerGauge8, powerLabel8, powerValue8, powerConsumption8));

        powerHashMap.forEach((s, tuple) -> {
            tuple.getValue0().setVisible(false);
            tuple.getValue1().setVisible(false);
            tuple.getValue2().setVisible(false);
            tuple.getValue3().setVisible(false);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISensor) {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addNormal(() -> {
                powerHashMap.get(sensor.getDestination()).getValue0().setValue(Math.round((sensor.getValue() - sensor.getMinRange())/(sensor.getMaxRange()-sensor.getMinRange())*1000)/10.0);
                powerHashMap.get(sensor.getDestination()).getValue2().setText((Math.round(sensor.getValue()*100)/100.0) + sensor.getUnit());
//                powerHashMap.get(sensor.getDestination()).getValue3().setText((Math.round(sensor.getValue()*100)/100.0) + sensor.getUnit());
            });
        }
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var triplet = powerHashMap.get(sensor.getDestination());
            if (triplet != null) {
                triplet.getValue0().setVisible(true);
                triplet.getValue0().setThresholdColor(Color.BLUE);
                triplet.getValue0().setValueVisible(true);
                triplet.getValue0().setThresholdVisible(true);
                triplet.getValue0().setUnit(sensor.getUnit());
                triplet.getValue1().setVisible(true);
                triplet.getValue1().setText(sensor.getName());
                triplet.getValue2().setVisible(true);
                triplet.getValue3().setVisible(true);
            } else {
                logger.error("Wrong UI binding - destination not found: {}",sensor.getDestination());
            }
        }
    }
}
