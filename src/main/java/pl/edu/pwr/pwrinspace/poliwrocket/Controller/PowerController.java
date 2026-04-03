package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

public class PowerController extends BaseSensorController {
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
    protected Label powerTemperature1;
    @FXML
    protected Label powerTemperature2;
    @FXML
    protected Label powerTemperature3;
    @FXML
    protected Label powerTemperature4;
    @FXML
    protected Label powerTemperature5;
    @FXML
    protected Label powerTemperature6;
    @FXML
    protected Label powerTemperature7;
    @FXML
    protected Label powerTemperature8;

    @FXML
    protected Label powerVoltage1;
    @FXML
    protected Label powerVoltage2;
    @FXML
    protected Label powerVoltage3;
    @FXML
    protected Label powerVoltage4;
    @FXML
    protected Label powerVoltage5;
    @FXML
    protected Label powerVoltage6;
    @FXML
    protected Label powerVoltage7;
    @FXML
    protected Label powerVoltage8;

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

    protected HashMap<String, Label> labelHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        labelHashMap.clear();

        var fields = getAllFields(new LinkedList<>(), this.getClass());

        for (Field declaredField : fields) {
            try {
                if (declaredField.getType().isAssignableFrom(Label.class)) {
                    ((Label) declaredField.get(this)).setVisible(false);
                    labelHashMap.put(declaredField.getName(), (Label) declaredField.get(this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISensor) {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addNormal(() -> labelHashMap.get(sensor.getDestination()).setText(Math.round(sensor.getValue() * 100.0) / 100.0 + " " + sensor.getUnit()));
        }
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            if (sensor.getDestination().startsWith("powerVoltage")) {
                var title = labelHashMap.get(sensor.getDestination().replace("Voltage", "Label"));
                title.setVisible(true);
                title.setText(sensor.getName());
            }
            var label = labelHashMap.get(sensor.getDestination());
            if (label != null) {
                label.setVisible(true);
            } else {
                logger.error("Wrong UI binding - destination not found: {}", sensor.getDestination());
            }
        }
    }
}
