package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.util.HashMap;

public class ValvesController extends BasicButtonSensorController {

    @FXML
    private Indicator valveIndicator1;

    @FXML
    private Label valveLabel1;

    @FXML
    private Button valveOpenButton1;

    @FXML
    private Button valveCloseButton1;

    @FXML
    private Indicator valveIndicator2;

    @FXML
    private Label valveLabel2;

    @FXML
    private Button valveOpenButton2;

    @FXML
    private Button valveCloseButton2;

    @FXML
    private Indicator valveIndicator3;

    @FXML
    private Label valveLabel3;

    @FXML
    private Button valveOpenButton3;

    @FXML
    private Button valveCloseButton3;

    @FXML
    private Indicator valveIndicator4;

    @FXML
    private Label valveLabel4;

    @FXML
    private Button valveOpenButton4;

    @FXML
    private Button valveCloseButton4;

    @FXML
    private Indicator valveIndicator5;

    @FXML
    private Label valveLabel5;

    @FXML
    private Button valveOpenButton5;

    @FXML
    private Button valveCloseButton5;


    private HashMap<String,Indicator> indicatorHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.VALVES_CONTROLLER;

        assert valveIndicator1 != null : "fx:id=\"valveIndicator1\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveLabel1 != null : "fx:id=\"valveLabel1\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveOpenButton1 != null : "fx:id=\"valveOpenButton1\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveCloseButton1 != null : "fx:id=\"valveCloseButton1\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveIndicator2 != null : "fx:id=\"valveIndicator2\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveLabel2 != null : "fx:id=\"valveLabel2\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveOpenButton2 != null : "fx:id=\"valveOpenButton2\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveCloseButton2 != null : "fx:id=\"valveCloseButton2\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveIndicator3 != null : "fx:id=\"valveIndicator3\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveLabel3 != null : "fx:id=\"valveLabel3\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveOpenButton3 != null : "fx:id=\"valveOpenButton3\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveCloseButton3 != null : "fx:id=\"valveCloseButton3\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveIndicator4 != null : "fx:id=\"valveIndicator4\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveLabel4 != null : "fx:id=\"valveLabel4\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveOpenButton4 != null : "fx:id=\"valveOpenButton4\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveCloseButton4 != null : "fx:id=\"valveCloseButton4\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveIndicator5 != null : "fx:id=\"valveIndicator5\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveLabel5 != null : "fx:id=\"valveLabel5\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveOpenButton5 != null : "fx:id=\"valveOpenButton5\" was not injected: check your FXML file 'ValvesController.fxml'.";
        assert valveCloseButton5 != null : "fx:id=\"valveCloseButton5\" was not injected: check your FXML file 'ValvesController.fxml'.";

        buttonHashMap.put(valveOpenButton1.getId(),valveOpenButton1);
        buttonHashMap.put(valveOpenButton2.getId(),valveOpenButton2);
        buttonHashMap.put(valveOpenButton3.getId(),valveOpenButton3);
        buttonHashMap.put(valveOpenButton4.getId(),valveOpenButton4);
        buttonHashMap.put(valveOpenButton5.getId(),valveOpenButton5);

        buttonHashMap.put(valveCloseButton1.getId(),valveCloseButton1);
        buttonHashMap.put(valveCloseButton2.getId(),valveCloseButton2);
        buttonHashMap.put(valveCloseButton3.getId(),valveCloseButton3);
        buttonHashMap.put(valveCloseButton4.getId(),valveCloseButton4);
        buttonHashMap.put(valveCloseButton5.getId(),valveCloseButton5);

        indicatorHashMap.put(valveIndicator1.getId(),valveIndicator1);
        indicatorHashMap.put(valveIndicator2.getId(),valveIndicator2);
        indicatorHashMap.put(valveIndicator3.getId(),valveIndicator3);
        indicatorHashMap.put(valveIndicator4.getId(),valveIndicator4);
        indicatorHashMap.put(valveIndicator5.getId(),valveIndicator5);
    }

    @Override
    protected void setUIBySensors(){
        for (ISensor s : sensors) {
            var indicator = indicatorHashMap.get(s.getDestination());
            indicator.setVisible(true);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            Platform.runLater(() -> {
                if(sensor.getValue() != 1.0 && sensor.getValue() != 0.0){
                    indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Color.RED);
                } else {
                    indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Tile.BLUE);
                }
                indicatorHashMap.get(sensor.getDestination()).setOn(sensor.getValue() >= 1.0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
