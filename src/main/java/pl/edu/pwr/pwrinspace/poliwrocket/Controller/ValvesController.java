package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class ValvesController extends BasicButtonSensorController {

    @FXML
    protected Indicator dataIndicator1;

    @FXML
    protected Label indicatorLabel1;

    @FXML
    protected JFXButton valveOpenButton1;

    @FXML
    protected JFXButton valveCloseButton1;

    @FXML
    protected Indicator dataIndicator2;

    @FXML
    protected Label indicatorLabel2;

    @FXML
    protected JFXButton valveOpenButton2;

    @FXML
    protected JFXButton valveCloseButton2;

    @FXML
    protected Indicator dataIndicator3;

    @FXML
    protected Label indicatorLabel3;

    @FXML
    protected JFXButton valveOpenButton3;

    @FXML
    protected JFXButton valveCloseButton3;

    @FXML
    protected Indicator dataIndicator4;

    @FXML
    protected Label indicatorLabel4;

    @FXML
    protected JFXButton valveOpenButton4;

    @FXML
    protected JFXButton valveCloseButton4;

    @FXML
    protected Indicator dataIndicator5;

    @FXML
    protected Label indicatorLabel5;

    @FXML
    protected JFXButton valveOpenButton5;

    @FXML
    protected JFXButton valveCloseButton5;

    @FXML
    protected Indicator dataIndicator6;

    @FXML
    protected Label indicatorLabel6;

    @FXML
    protected JFXButton valveOpenButton6;

    @FXML
    protected JFXButton valveCloseButton6;

    @FXML
    protected JFXButton valveTmpOpenButton1;

    @FXML
    protected JFXButton valveTmpOpenButton2;

    @FXML
    protected JFXButton valveTmpOpenButton3;

    @FXML
    protected JFXButton valveTmpOpenButton4;

    @FXML
    protected JFXButton valveTmpOpenButton5;

    @FXML
    protected JFXButton valveTmpOpenButton6;


    @FXML
    private Label indicatorValueLabel1;

    @FXML
    private Label indicatorValueLabel2;

    @FXML
    private Label indicatorValueLabel3;

    @FXML
    private Label indicatorValueLabel4;

    @FXML
    private Label indicatorValueLabel5;

    @FXML
    private Label indicatorValueLabel6;

    private final HashMap<String,Button> closeHashMap = new HashMap<>();
    private final HashMap<String,Button> openHashMap = new HashMap<>();
    private final HashMap<String,Label> valueLabelHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        super.buildVisualizationMap();

        closeHashMap.clear();
        openHashMap.clear();
        valueLabelHashMap.clear();

        openHashMap.put(dataIndicator1.getId(),valveOpenButton1);
        openHashMap.put(dataIndicator2.getId(),valveOpenButton2);
        openHashMap.put(dataIndicator3.getId(),valveOpenButton3);
        openHashMap.put(dataIndicator4.getId(),valveOpenButton4);
        openHashMap.put(dataIndicator5.getId(),valveOpenButton5);
        openHashMap.put(dataIndicator6.getId(),valveOpenButton6);

        closeHashMap.put(dataIndicator1.getId(),valveCloseButton1);
        closeHashMap.put(dataIndicator2.getId(),valveCloseButton2);
        closeHashMap.put(dataIndicator3.getId(),valveCloseButton3);
        closeHashMap.put(dataIndicator4.getId(),valveCloseButton4);
        closeHashMap.put(dataIndicator5.getId(),valveCloseButton5);
        closeHashMap.put(dataIndicator6.getId(),valveCloseButton6);

        valueLabelHashMap.put(dataIndicator1.getId(),indicatorValueLabel1);
        valueLabelHashMap.put(dataIndicator2.getId(),indicatorValueLabel2);
        valueLabelHashMap.put(dataIndicator3.getId(),indicatorValueLabel3);
        valueLabelHashMap.put(dataIndicator4.getId(),indicatorValueLabel4);
        valueLabelHashMap.put(dataIndicator5.getId(),indicatorValueLabel5);
        valueLabelHashMap.put(dataIndicator6.getId(),indicatorValueLabel6);
    }


    @Override
    protected void setUIBySensors() {
        super.setUIBySensors();
        for (ISensor sensor : sensors) {
            var label = valueLabelHashMap.get(sensor.getDestination());
            label.setVisible(sensor.hasInterpreter());
            //label.setText("");
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

                if(sensor.hasInterpreter()) {
                    valueLabelHashMap.get(sensor.getDestination()).setText(sensor.getCodeMeaning().text);
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
