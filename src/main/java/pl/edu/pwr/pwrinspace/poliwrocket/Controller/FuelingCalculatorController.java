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

    private double currentWeight  = 0.0;
    private double currentPressure  = 0.0;
    private double currentFlowRate  = 0.0;

    private double initialMotherWeight = 0.0;
    private boolean isProcessActive = false;

    private double totalVentLoss = 0.0;
    private double weightAtLastVent = 0.0;

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
                if(destination.equals("dataGauge3")) {
                    if(!weightOverrideCheck.isSelected()){
                        currentWeight = sensor.getValue();
                        sensorTankWeightField.setText(String.format(Locale.US, "%.2f", currentWeight));
                        updateFuelingCalucations();
                    }

                }else if (destination.equals("dataGauge2")) {
                    if(!pressureOverrideCheck.isSelected()){
                        currentPressure = sensor.getValue();
                        sensorOxiPressureField.setText(String.format(Locale.US, "%.2f", currentPressure));
                        calculateFlowRate();
                    }

                }else{
                    TextField targetField = fieldHashMap.get(destination);
                    if(targetField != null){
                        targetField.setText(String.format(Locale.US, "%.2f", sensor.getValue()));
                    }
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

        sensorOxiPressureField.setOnAction(e -> calculateFlowRate());
        flowCoefficientField.setOnAction(e -> calculateFlowRate());
        ventDurationField.setOnAction(e -> calculateFlowRate());

        sensorTankWeightField.setOnAction(e -> updateFuelingCalucations());

        flowCoefficientField.setText("0.42");
        sensorOxiPressureField.setText("0.0");
        sensorTankWeightField.setText("0.0");
        ventDurationField.setText("0.0");
    }

    private void calculateFlowRate(){
        currentPressure = parseDoubleSafely(sensorOxiPressureField.getText(), 0.0);
        double coefficient = parseDoubleSafely(flowCoefficientField.getText(), 0.0);
        double ventTime = parseDoubleSafely(ventDurationField.getText(), 0.0);

        if(currentPressure > 0){
            currentFlowRate = coefficient * Math.sqrt(currentPressure);
        }else{
            currentFlowRate = 0.0;
        }

        double estimatedVentAmount = currentFlowRate * ventTime;
        estimatedVentField.setText(String.format(Locale.US, "%.2f", estimatedVentAmount));

        if (currentFlowRate > 0) {
            flowArea.appendText(String.format(Locale.US, "[FLOW] Rate: %.3f kg/s | Est. Vent (wv): %.2f kg (P: %.1f bar)\n",
                    currentFlowRate, estimatedVentAmount, currentPressure));
        }
    }

    private double parseDoubleSafely(String text, double defaultValue){
        if(text == null || text.trim().isEmpty()){
            return defaultValue;
        }
        try{
            return Double.parseDouble(text.trim().replace(",", "."));
        }catch(NumberFormatException e){
            logger.warn("Fueling Calc: Invalid float format in field: '{}'. Defaulting to {}", text, defaultValue);
            return defaultValue;
        }

    }
    private void updateFuelingCalucations(){
        if(!isProcessActive)return;

        currentWeight = parseDoubleSafely(sensorTankWeightField.getText(), 0.0);

        double totalFueled = (initialMotherWeight - currentWeight) - totalVentLoss;

        if(totalFueled < 0){
            totalFueled = 0.0;
        }

        double sinceLastVent = weightAtLastVent - currentWeight;
        if(sinceLastVent < 0){
            sinceLastVent = 0.0;
        }

        totalFueledLabel.setText(String.format(Locale.US, "%.2f", totalFueled));
        sinceVentLabel.setText(String.format(Locale.US, "%.2f", sinceLastVent));
    }

    @FXML
    private void handleStart(ActionEvent event){
        initialMotherWeight = parseDoubleSafely(sensorTankWeightField.getText(), 0.0);
        weightAtLastVent = initialMotherWeight;

        isProcessActive = true;
        totalVentLoss = 0.0;

        initialMotherWeightLabel.setText(String.format(Locale.US, "%.2f", initialMotherWeight));
        logsArea.appendText(String.format(Locale.US, "[START] Initial weight: %.2f kg. Fueling started.\n", initialMotherWeight));

        updateFuelingCalucations();
    }

    @FXML
    private void handleVent(ActionEvent event){
        if(!isProcessActive){
            logsArea.appendText("[WARN] Cannot vent before starting the process\n");
            return;
        }

        calculateFlowRate();

        double ventTime = parseDoubleSafely(ventDurationField.getText(), 0.0);

        double wv = currentFlowRate * ventTime;

        totalVentLoss += wv;

        weightAtLastVent = parseDoubleSafely(sensorTankWeightField.getText(), 0.0);

        logsArea.appendText(String.format(Locale.US, "[VENT] Venting executed for %.1fs. Lost: %.2f kg\n", ventTime, wv));

        updateFuelingCalucations();
    }

}
