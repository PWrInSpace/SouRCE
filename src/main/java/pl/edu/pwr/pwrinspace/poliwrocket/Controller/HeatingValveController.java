package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CodeInterpreterUIHint;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class HeatingValveController extends BaseButtonSensorController {

    @FXML
    protected Indicator dataIndicator1;
    @FXML
    protected Label indicatorLabel1;
    @FXML
    protected Label indicatorValueLabel1;
    @FXML
    protected JFXButton valveOnButton1;
    @FXML
    protected JFXButton valveOffButton1;

    private final HashMap<String, Button> offHashMap = new HashMap<>();
    private final HashMap<String,Button> onHashMap = new HashMap<>();
    private final HashMap<String,Label> valueLabelHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        super.buildVisualizationMap();

        offHashMap.clear();
        onHashMap.clear();
        valueLabelHashMap.clear();

        onHashMap.put(dataIndicator1.getId(),valveOnButton1);
        offHashMap.put(dataIndicator1.getId(),valveOffButton1);
        valueLabelHashMap.put(dataIndicator1.getId(),indicatorValueLabel1);
    }

    @Override
    protected void setUIBySensors() {
        super.setUIBySensors();
        for (ISensor sensor : sensors) {
            if (sensor == null) continue;
            var label = valueLabelHashMap.get(sensor.getDestination());
            if (label != null) {
                label.setVisible(sensor.hasInterpreter());
                label.setText("STATE");
            }
            var ind = indicatorHashMap.get(sensor.getDestination());
            if (ind == null) continue;
            boolean on = sensor.getValue() == 1.0;
            ind.setOn(on);
            ind.setDotOnColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {

                var ind = indicatorHashMap.get(sensor.getDestination());
                if (ind != null) {
                    ind.setDotOnColor(sensor.hasInterpreter() ? UIHelper.resolveUIHintColor(sensor.getCodeMeaning().UIHint) : Color.DODGERBLUE);
                    ind.setOn(sensor.getValue() == 1.0);
                }

                if (sensor.hasInterpreter()) {
                    var vl = valueLabelHashMap.get(sensor.getDestination());
                    if (vl != null) vl.setText(sensor.getCodeMeaning().text);
                    var isNotClosed = sensor.getCodeMeaning().UIHint != CodeInterpreterUIHint.CLOSE;
                    var offBtn = offHashMap.get(sensor.getDestination());
                    var onBtn = onHashMap.get(sensor.getDestination());
                    if (offBtn != null) offBtn.setDefaultButton(isNotClosed);
                    if (onBtn != null) onBtn.setDefaultButton(!isNotClosed);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
