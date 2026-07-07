package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


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

    @FXML
    private void handleStart(ActionEvent event){
        System.out.println("START");
    }

    @FXML
    private void handleVent(ActionEvent event){
        System.out.println("VENT");
    }

    @Override
    public void invalidated(Observable observable) {

    }

    @Override
    protected void buildVisualizationMap() {

    }

    @Override
    protected void setUIBySensors() {

    }
}
