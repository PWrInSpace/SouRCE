package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

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

    @FXML
    private Indicator valveIndicator6;

    @FXML
    private Label valveLabel6;

    @FXML
    private Button valveOpenButton6;

    @FXML
    private Button valveCloseButton6;

    @FXML
    private Button valveTmpOpenButton1;

    @FXML
    private Button valveTmpOpenButton2;

    @FXML
    private Button valveTmpOpenButton3;

    @FXML
    private Button valveTmpOpenButton4;

    @FXML
    private Button valveTmpOpenButton5;

    @FXML
    private Button valveTmpOpenButton6;


    private final HashMap<String,Indicator> indicatorHashMap = new HashMap<>();
    private final HashMap<String,Label> labelHashMap = new HashMap<>();
    private final HashMap<String,Button> closeHashMap = new HashMap<>();
    private final HashMap<String,Button> openHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.VALVES_CONTROLLER;

        buttonHashMap.put(valveOpenButton1.getId(),valveOpenButton1);
        buttonHashMap.put(valveOpenButton2.getId(),valveOpenButton2);
        buttonHashMap.put(valveOpenButton3.getId(),valveOpenButton3);
        buttonHashMap.put(valveOpenButton4.getId(),valveOpenButton4);
        buttonHashMap.put(valveOpenButton5.getId(),valveOpenButton5);
        buttonHashMap.put(valveOpenButton6.getId(),valveOpenButton6);

        buttonHashMap.put(valveCloseButton1.getId(),valveCloseButton1);
        buttonHashMap.put(valveCloseButton2.getId(),valveCloseButton2);
        buttonHashMap.put(valveCloseButton3.getId(),valveCloseButton3);
        buttonHashMap.put(valveCloseButton4.getId(),valveCloseButton4);
        buttonHashMap.put(valveCloseButton5.getId(),valveCloseButton5);
        buttonHashMap.put(valveCloseButton6.getId(),valveCloseButton6);

        buttonHashMap.put(valveTmpOpenButton1.getId(),valveTmpOpenButton1);
        buttonHashMap.put(valveTmpOpenButton2.getId(),valveTmpOpenButton2);
        buttonHashMap.put(valveTmpOpenButton3.getId(),valveTmpOpenButton3);
        buttonHashMap.put(valveTmpOpenButton4.getId(),valveTmpOpenButton4);
        buttonHashMap.put(valveTmpOpenButton5.getId(),valveTmpOpenButton5);
        buttonHashMap.put(valveTmpOpenButton6.getId(),valveTmpOpenButton6);

        indicatorHashMap.put(valveIndicator1.getId(),valveIndicator1);
        indicatorHashMap.put(valveIndicator2.getId(),valveIndicator2);
        indicatorHashMap.put(valveIndicator3.getId(),valveIndicator3);
        indicatorHashMap.put(valveIndicator4.getId(),valveIndicator4);
        indicatorHashMap.put(valveIndicator5.getId(),valveIndicator5);
        indicatorHashMap.put(valveIndicator6.getId(),valveIndicator6);

        labelHashMap.put(valveIndicator1.getId(),valveLabel1);
        labelHashMap.put(valveIndicator2.getId(),valveLabel2);
        labelHashMap.put(valveIndicator3.getId(),valveLabel3);
        labelHashMap.put(valveIndicator4.getId(),valveLabel4);
        labelHashMap.put(valveIndicator5.getId(),valveLabel5);
        labelHashMap.put(valveIndicator6.getId(),valveLabel6);

        openHashMap.put(valveIndicator1.getId(),valveOpenButton1);
        openHashMap.put(valveIndicator2.getId(),valveOpenButton2);
        openHashMap.put(valveIndicator3.getId(),valveOpenButton3);
        openHashMap.put(valveIndicator4.getId(),valveOpenButton4);
        openHashMap.put(valveIndicator5.getId(),valveOpenButton5);
        openHashMap.put(valveIndicator6.getId(),valveOpenButton6);

        closeHashMap.put(valveIndicator1.getId(),valveCloseButton1);
        closeHashMap.put(valveIndicator2.getId(),valveCloseButton2);
        closeHashMap.put(valveIndicator3.getId(),valveCloseButton3);
        closeHashMap.put(valveIndicator4.getId(),valveCloseButton4);
        closeHashMap.put(valveIndicator5.getId(),valveCloseButton5);
        closeHashMap.put(valveIndicator6.getId(),valveCloseButton6);
    }

    @Override
    protected void setUIBySensors(){
        for (ISensor s : sensors) {
            var indicator = indicatorHashMap.get(s.getDestination());
            indicator.setVisible(true);

            var label = labelHashMap.get(s.getDestination());
            label.setText(s.getName());
            label.setVisible(true);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {
                if(sensor.getValue() != 1.0 && sensor.getValue() != 0.0){
                    indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Color.RED);
                } else {
                    indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Tile.BLUE);
                }

                if(sensor.getValue() >= 1.0) {
                    indicatorHashMap.get(sensor.getDestination()).setOn(true);
                    closeHashMap.get(sensor.getDestination()).setDefaultButton(true);
                    openHashMap.get(sensor.getDestination()).setDefaultButton(false);
                } else {
                    indicatorHashMap.get(sensor.getDestination()).setOn(false);
                    closeHashMap.get(sensor.getDestination()).setDefaultButton(false);
                    openHashMap.get(sensor.getDestination()).setDefaultButton(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
