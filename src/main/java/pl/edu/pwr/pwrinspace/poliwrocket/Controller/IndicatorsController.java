package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class IndicatorsController extends BasicSensorController {

    @FXML
    private Indicator dataIndicator1;

    @FXML
    private Label indicatorLabel1;

    @FXML
    private Indicator dataIndicator2;

    @FXML
    private Label indicatorLabel2;

    @FXML
    private Indicator dataIndicator3;

    @FXML
    private Label indicatorLabel3;

    @FXML
    private Indicator dataIndicator4;

    @FXML
    private Label indicatorLabel4;

    @FXML
    private Indicator dataIndicator5;

    @FXML
    private Label indicatorLabel5;

    @FXML
    private Indicator dataIndicator6;

    @FXML
    private Label indicatorLabel6;

    @FXML
    private Indicator dataIndicator7;

    @FXML
    private Label indicatorLabel7;

    HashMap<String, Indicator> visualizationsHashMap = new HashMap<>();
    HashMap<String, Label> labelsHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.INDICATORS_CONTROLLER;

        visualizationsHashMap.put(dataIndicator1.getId(),dataIndicator1);
        visualizationsHashMap.put(dataIndicator2.getId(),dataIndicator2);
        visualizationsHashMap.put(dataIndicator3.getId(),dataIndicator3);
        visualizationsHashMap.put(dataIndicator4.getId(),dataIndicator4);
        visualizationsHashMap.put(dataIndicator5.getId(),dataIndicator5);
        visualizationsHashMap.put(dataIndicator6.getId(),dataIndicator6);
        visualizationsHashMap.put(dataIndicator7.getId(),dataIndicator7);

        labelsHashMap.put(dataIndicator1.getId(),indicatorLabel1);
        labelsHashMap.put(dataIndicator2.getId(),indicatorLabel2);
        labelsHashMap.put(dataIndicator3.getId(),indicatorLabel3);
        labelsHashMap.put(dataIndicator4.getId(),indicatorLabel4);
        labelsHashMap.put(dataIndicator5.getId(),indicatorLabel5);
        labelsHashMap.put(dataIndicator6.getId(),indicatorLabel6);
        labelsHashMap.put(dataIndicator7.getId(),indicatorLabel7);
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var visualization = this.visualizationsHashMap.get(sensor.getDestination());
            if (visualization != null) {
                visualization.setVisible(true);
                var label = this.labelsHashMap.get(visualization.getId());
                label.setText(sensor.getName());
                label.setVisible(true);
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {
                var visualization = visualizationsHashMap.get(sensor.getDestination());
                visualization.setOn(sensor.getValue() == 1.0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
