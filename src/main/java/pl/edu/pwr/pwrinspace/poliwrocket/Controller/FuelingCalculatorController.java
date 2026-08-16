package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;


public class FuelingCalculatorController extends BaseSensorController{

    @FXML private TextField sensorTankWeightField;
    @FXML private TextField sensorOxiPressureField;
    @FXML private TextField flowCoefficientField;
    @FXML private TextField ventDurationField;

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
    @FXML private Label estimatedVentLabel;


    private double currentWeight  = 0.0;
    private double currentPressure  = 0.0;
    private double currentFlowRate  = 0.0;

    private double initialMotherWeight = 0.0;
    private boolean isProcessActive = false;

    private double totalVentLoss = 0.0;
    private double weightAtLastVent = 0.0;
    private double lastLoggedFlowRate = -1.0;
    private double lastValidFueledAmount = 0.0;
    private double lastValidSinceVentAmount = 0.0;

    private static final double K = 1.28;
    private static final double R = 188.91;
    private static final double C_D = 0.7;
    private static final double VENT_DIAMETER = 0.0014;
    private static final double A_T = (Math.PI * Math.pow(VENT_DIAMETER, 2)) / 4.0;

    private static final double INITIAL_TEMP = 293.15; //20

    private boolean isOxiFillOpen = false;

    @Override
    protected void buildVisualizationMap() {}

    @Override
    protected void setUIBySensors() {}

    @Override
    public void invalidated(Observable observable) {
        if(observable instanceof  ISensor) {
            var sensor = (ISensor) observable;
            String destination = sensor.getDestination();

            UIThreadManager.getInstance().addNormal(() -> {
                if(destination.equals("dataGauge9")) {
                    if(!weightOverrideCheck.isSelected()){
                        currentWeight = sensor.getValue() / 10.0;
                        sensorTankWeightField.setText(String.format(Locale.US, "%.3f", currentWeight));
                        updateFuelingCalucations();
                    }

                }else if (destination.equals("dataGauge8")) {
                    if(!pressureOverrideCheck.isSelected()){
                        currentPressure = sensor.getValue();
                        sensorOxiPressureField.setText(String.format(Locale.US, "%.3f", currentPressure));
                        calculateFlowRate();
                    }

                }else if (destination.equals("zdataIndicator1")){
                    isOxiFillOpen = sensor.getValue() > 0.5;
                    updateFuelingCalucations();
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

        sensorTankWeightField.setOnAction(e -> {
            if(weightOverrideCheck.isSelected()){
                double currentWeight = parseDoubleSafely(sensorTankWeightField.getText(), 0.0);
                logsArea.appendText(String.format(Locale.US, "[OVERRIDE] Mother bottle weight set to: %.3f kg\n", currentWeight));
            }
            updateFuelingCalucations();
        });

        flowCoefficientField.setText("0.42");
        sensorOxiPressureField.setText("0.0");
        sensorTankWeightField.setText("0.0");
        ventDurationField.setText("0.0");
    }

    private void calculateFlowRate(){
        double pBar = parseDoubleSafely(sensorOxiPressureField.getText(), 0.0);
        double p = pBar * 100000.0;

        if(p > 0 && !flowOverrideCheck.isSelected()){
            double term1 = K / (R * INITIAL_TEMP);
            double term2 = Math.pow((2.0 / (K + 1.0)), ((K + 1.0 )/ (K -1.0)));
            double sqrtPart = Math.sqrt(term1 * term2);

            currentFlowRate = C_D * A_T * p *sqrtPart;

            flowCoefficientField.setText(String.format(Locale.US, "%.3f", currentFlowRate));
        }else{
            currentFlowRate = parseDoubleSafely(flowCoefficientField.getText(), 0.0);
        }

        double coefficient = parseDoubleSafely(flowCoefficientField.getText(), 0.0);
        double ventTime = parseDoubleSafely(ventDurationField.getText(), 0.0);
        double estimatedVentAmount = coefficient * ventTime;

        currentFlowRate = coefficient;

        estimatedVentLabel.setText(String.format(Locale.US, "%.3f", estimatedVentAmount));

        if (currentFlowRate > 0 && Math.abs(currentFlowRate - lastLoggedFlowRate) > 0.001) {
            flowArea.appendText(String.format(Locale.US, "[FLOW] Rate: %.3f kg/s | Est. Vent (wv): %.3f kg\n",
                    currentFlowRate, estimatedVentAmount));
            lastLoggedFlowRate = currentFlowRate;
        }
    }

    private void updateFuelingCalucations(){
        if(!isProcessActive)return;

        currentWeight = parseDoubleSafely(sensorTankWeightField.getText(), 0.0);

        if(isOxiFillOpen || weightOverrideCheck.isSelected()){
            double totalFueled = (initialMotherWeight - currentWeight) - totalVentLoss;

            if(totalFueled < 0){
                totalFueled = 0.0;
            }
            lastValidFueledAmount = totalFueled;

            double sinceLastVent = weightAtLastVent - currentWeight;
            if(sinceLastVent < 0){
                sinceLastVent = 0.0;
            }

            lastValidSinceVentAmount = sinceLastVent;
        }

        totalFueledLabel.setText(String.format(Locale.US, "%.3f", lastValidFueledAmount));




        sinceVentLabel.setText(String.format(Locale.US, "%.3f", lastValidSinceVentAmount));
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

    @FXML
    private void handleStart(ActionEvent event){
        initialMotherWeight = parseDoubleSafely(sensorTankWeightField.getText(), 0.0);
        weightAtLastVent = initialMotherWeight;

        isProcessActive = true;
        totalVentLoss = 0.0;
        lastValidFueledAmount = 0.0;

        initialMotherWeightLabel.setText(String.format(Locale.US, "%.3f", initialMotherWeight));
        logsArea.appendText(String.format(Locale.US, "[START] Initial weight: %.3f kg. Fueling started.\n", initialMotherWeight));

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

        logsArea.appendText(String.format(Locale.US, "[VENT] Venting executed for %.1fs. Lost: %.3f kg\n", ventTime, wv));

        updateFuelingCalucations();
    }

}
