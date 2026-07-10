package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;


public class FuelingCalculatorController extends BaseSensorController{

    @FXML private TextField sensorTankWeightField;
    @FXML private TextField sensorOxiPressureField;
    @FXML private TextField flowCoefficientField;
    @FXML private TextField ventDurationField;
    @FXML private TextField estimatedVentField;

    @FXML private JFXCheckBox weightOverrideCheck;
    @FXML private JFXCheckBox pressureOverrideCheck;
    @FXML private JFXCheckBox flowOverrideCheck;

    @FXML private JFXButton ventButton;
    @FXML private JFXButton startButton;

    @FXML private TextArea logsArea;
    @FXML private TextArea flowArea;

    @FXML private Label totalFueledLabel;
    @FXML private Label sinceVentLabel;
    @FXML private Label initialMotherWeightLabel;

    protected HashMap<String, TextField> fieldHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        fieldHashMap.clear();

        for(Field declaredField: this.getClass().getDeclaredFields()) {
            if(TextField.class.isAssignableFrom(declaredField.getType())) {
                try{
                    declaredField.setAccessible(true);
                    fieldHashMap.put(declaredField.getName(), (TextField) declaredField.get(this));
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void setUIBySensors() {
        for(ISensor sensor : sensors) {
            logger.info("Binding sensor {} to FuelingCalculatorController with destination: {}",
                    sensor.getName(), sensor.getDestination());
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if(observable instanceof  ISensor) {
            var sensor = (ISensor) observable;
            String destination = sensor.getDestination();

            UIThreadManager.getInstance().addNormal(() -> {
                if(destination.equals("dataGauge3") && weightOverrideCheck.isSelected()) {
                    return;
                }

                if (destination.equals("dataGauge2") && pressureOverrideCheck.isSelected()) {
                    return;
                }

                TextField targetField = fieldHashMap.get(destination);
                if(targetField != null){
                    targetField.setText(String.format(Locale.US, "%.2f", sensor.getValue()));
                }
            });
        }
    }

    @FXML
    public void initialize() {
        buildVisualizationMap();

        sensorTankWeightField.setEditable(false);
        sensorOxiPressureField.setEditable(false);
        flowCoefficientField.setEditable(false);

        weightOverrideCheck.setOnAction(e -> sensorTankWeightField.setEditable(weightOverrideCheck.isSelected()));
        pressureOverrideCheck.setOnAction(e -> sensorOxiPressureField.setEditable(pressureOverrideCheck.isSelected()));
        flowOverrideCheck.setOnAction(e -> flowCoefficientField.setEditable(flowOverrideCheck.isSelected()));

        flowCoefficientField.setText("0.42");
    }



    @FXML
    private void handleStart(ActionEvent event){
        System.out.println("START");
    }

    @FXML
    private void handleVent(ActionEvent event){
        System.out.println("VENT");
    }

}
