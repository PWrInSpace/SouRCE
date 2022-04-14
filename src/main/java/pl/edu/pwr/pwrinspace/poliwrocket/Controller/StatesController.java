package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.addons.Indicator;
import eu.hansolo.tilesfx.addons.Switch;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

public class StatesController extends BasicButtonSensorController {

    @FXML
    private Switch safeSwitch1;

    @FXML
    private Switch safeSwitch2;

    @FXML
    private Switch safeSwitch3;

    @FXML
    private Switch safeSwitch4;

    @FXML
    protected JFXButton stateButton1;

    @FXML
    protected JFXButton stateButton2;

    @FXML
    protected JFXButton stateButton3;

    @FXML
    protected JFXButton stateButton4;

    @FXML
    protected Indicator stateIndicator1;

    @FXML
    protected Indicator stateIndicator2;

    @FXML
    protected Indicator stateIndicator3;

    @FXML
    protected Indicator stateIndicator4;

    @FXML
    protected void initialize() {
        super.initialize();

        stateButton1.setDisable(true);
        stateButton2.setDisable(true);
        stateButton3.setDisable(true);
        stateButton4.setDisable(true);

        safeSwitch1.setOnMouseClicked(actionEvent ->
                stateButton1.setDisable(!safeSwitch1.isActive())
        );

        safeSwitch2.setOnMouseClicked(actionEvent ->
                stateButton2.setDisable(!safeSwitch1.isActive() || !safeSwitch2.isActive())
        );
        safeSwitch3.setOnMouseClicked(actionEvent ->
                stateButton3.setDisable(!safeSwitch1.isActive() || !safeSwitch2.isActive() || !safeSwitch3.isActive())
        );
        safeSwitch4.setOnMouseClicked(actionEvent ->
                stateButton4.setDisable(!safeSwitch1.isActive() || !safeSwitch2.isActive() || !safeSwitch3.isActive() || !safeSwitch4.isActive())
        );

    }

    @Override
    protected void setUIBySensors() {
        //on this panel are not customizable UI
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {
                var indicator = indicatorHashMap.get(sensor.getDestination());
                indicator.setOn(sensor.getValue() == 1.0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
