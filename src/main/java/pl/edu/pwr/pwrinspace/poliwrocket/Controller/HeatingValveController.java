package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CodeInterpreterUIHint;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.Collection;
import java.util.HashMap;

public class HeatingValveController extends BasicButtonSensorController  {

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
            var label = valueLabelHashMap.get(sensor.getDestination());
            label.setVisible(sensor.hasInterpreter());
            label.setText("STATE");
            indicatorHashMap.get(sensor.getDestination()).setOn(true);
            indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void assignsCommands(Collection<ICommand> commands){
        super.assignsCommands(commands);
        Platform.runLater(() -> {
            for (ICommand command : commands) {
                var button = buttonHashMap.get(command.getCommandTriggerKey());
                if (button != null){
                    if(!command.getCommandDescription().isBlank()) {
                        button.setText(command.getCommandDescription());
                    }
                } else {
                    logger.warn("Trigger not found: {} , it`s maybe correct for fire button!", command.getCommandTriggerKey());
                }
            }
        });
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {

                indicatorHashMap.get(sensor.getDestination()).setDotOnColor(sensor.hasInterpreter() ? UIHelper.resolveUIHintColor(sensor.getCodeMeaning().UIHint) : Color.HOTPINK);
                indicatorHashMap.get(sensor.getDestination()).setOn(true);

                if(sensor.hasInterpreter()) {
                    valueLabelHashMap.get(sensor.getDestination()).setText(sensor.getCodeMeaning().text);
                    var isNotClosed = sensor.getCodeMeaning().UIHint != CodeInterpreterUIHint.CLOSE;
                    offHashMap.get(sensor.getDestination()).setDefaultButton(isNotClosed);
                    onHashMap.get(sensor.getDestination()).setDefaultButton(!isNotClosed);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
