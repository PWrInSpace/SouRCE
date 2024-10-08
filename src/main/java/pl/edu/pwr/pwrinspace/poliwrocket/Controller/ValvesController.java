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
    protected JFXButton valveTmpOpenButton3;

    @FXML
    private Label indicatorValueLabel1;

    @FXML
    private Label indicatorValueLabel2;

    @FXML
    private Label indicatorValueLabel3;

    @FXML
    private Label indicatorValueLabel4;

    @FXML
    private Label commandLabel1;

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

        closeHashMap.put(dataIndicator1.getId(),valveCloseButton1);
        closeHashMap.put(dataIndicator2.getId(),valveCloseButton2);
        closeHashMap.put(dataIndicator3.getId(),valveCloseButton3);
        closeHashMap.put(dataIndicator4.getId(),valveCloseButton4);

        valueLabelHashMap.put(dataIndicator1.getId(),indicatorValueLabel1);
        valueLabelHashMap.put(dataIndicator2.getId(),indicatorValueLabel2);
        valueLabelHashMap.put(dataIndicator3.getId(),indicatorValueLabel3);
        valueLabelHashMap.put(dataIndicator4.getId(),indicatorValueLabel4);
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
                    closeHashMap.get(sensor.getDestination()).setDefaultButton(isNotClosed);
                    openHashMap.get(sensor.getDestination()).setDefaultButton(!isNotClosed);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

