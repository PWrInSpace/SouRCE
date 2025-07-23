package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import java.util.HashMap;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.javatuples.Triplet;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

public class PowerController
extends BasicSensorController {
    @FXML
    private Gauge tanwaBattery;
    @FXML
    protected Label tanwaBatteryLabel;
    @FXML
    protected Label tanwaBatteryValue;
    private double maxBatteryValue = -1.0;
    private final HashMap<String, Triplet<Gauge, Label, Label>> powerHashMap = new HashMap();

    @Override
    protected void buildVisualizationMap() {
        this.powerHashMap.put(this.tanwaBattery.getId(), new Triplet<Gauge, Label, Label>(this.tanwaBattery, this.tanwaBatteryLabel, this.tanwaBatteryValue));
        this.powerHashMap.forEach((s, tuple) -> {
            ((Gauge)tuple.getValue0()).setVisible(false);
            ((Label)tuple.getValue1()).setVisible(false);
            ((Label)tuple.getValue2()).setVisible(false);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISensor) {
            ISensor sensor = (ISensor)((Object)observable);
            UIThreadManager.getInstance().addNormal(() -> {
                if (this.maxBatteryValue == -1.0) {
                    this.maxBatteryValue = sensor.getValue() > 17.5 ? 22.0 : 17.5;
                }
                this.powerHashMap.get(sensor.getDestination()).getValue0().setValue(100.0 * sensor.getValue() / this.maxBatteryValue);
                this.powerHashMap.get(sensor.getDestination()).getValue2().setText(sensor.getValue() + sensor.getUnit());
            });
        }
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : this.sensors) {
            Triplet<Gauge, Label, Label> triplet = this.powerHashMap.get(sensor.getDestination());
            if (triplet != null) {
                triplet.getValue0().setVisible(true);
                triplet.getValue0().setThresholdColor(Color.BLUE);
                triplet.getValue0().setValueVisible(true);
                triplet.getValue0().setThresholdVisible(true);
                triplet.getValue0().setUnit(sensor.getUnit());
                triplet.getValue1().setVisible(true);
                triplet.getValue1().setText(sensor.getName());
                triplet.getValue2().setVisible(true);
                continue;
            }
            logger.error("Wrong UI binding - destination not found: {}", (Object)sensor.getDestination());
        }
    }
}
