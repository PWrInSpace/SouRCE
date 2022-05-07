package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public  class InterpretersController extends BasicSensorController {

    @FXML
    private AnchorPane mainPanel;

    private final HashMap<String, Label> labelHashMap = new HashMap<>();
    private final HashMap<String, Label> valueHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        labelHashMap.clear();
        valueHashMap.clear();

        int layoutX = 14;
        int layoutY = 8;
        int labelWidth = 110;
        int valueWidth = 80;
        int height = 18;

        int element = 0;
        for (ISensor sensor : sensors) {
            if(element < 12) {
                if(element != 0 && element % 2 == 0) {
                    layoutY += 26;
                    layoutX -= 185;
                } else if(element % 2 != 0){
                    layoutX += 185;
                }

                Label label = new Label();
                Label value = new Label();

                label.setAlignment(Pos.CENTER);
                label.setLayoutX(layoutX);
                label.setLayoutY(layoutY);
                label.setPrefHeight(height);
                label.setPrefWidth(labelWidth);
                label.setVisible(true);

                value.setLayoutX(layoutX + 116);
                value.setLayoutY(layoutY);
                value.setPrefHeight(height);
                value.setPrefWidth(valueWidth);

                mainPanel.getChildren().add(label);
                mainPanel.getChildren().add(value);

                labelHashMap.put(sensor.getDestination(), label);
                valueHashMap.put(sensor.getDestination(), value);
            }

            element++;
        }

    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            labelHashMap.get(sensor.getDestination()).setText(sensor.getName());

            if(sensor.hasInterpreter()) {
                valueHashMap.get(sensor.getDestination()).setText(sensor.getCodeMeaning().text);
            } else {
                valueHashMap.get(sensor.getDestination()).setText(Double.toString(sensor.getValue()));
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if(observable instanceof ISensor) {
            var sensor = (ISensor)observable;
            var valueLabel =  valueHashMap.get(sensor.getDestination());
            var code = sensor.getCodeMeaning();

            UIThreadManager.getInstance().addNormal(() -> {
                if(sensor.hasInterpreter()) {
                    valueLabel.setText(code.text);
                    valueLabel.setTextFill(UIHelper.resolveUIHintColor(code.UIHint));
                } else {
                    valueLabel.setText(Double.toString(sensor.getValue()));
                }
            });
        }
    }
}
