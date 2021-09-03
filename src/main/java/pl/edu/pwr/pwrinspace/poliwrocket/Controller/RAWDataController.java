package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

public class RAWDataController extends BasicSensorController {

    @FXML
    private TextArea rawDataPanel;

    private static final Logger logger = LoggerFactory.getLogger(RAWDataController.class);

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.RAW_DATA_CONTROLLER;
    }

    @Override
    protected void setUIBySensors() {

    }

    @Override
    public void invalidated(Observable observable) {
        try {
            if(observable instanceof IMessageParser) {
                String rawData = Configuration.getInstance().sensorRepository.getSensorKeysAndValues();
                UIThreadManager.getInstance().addImmediateOnOK(() -> {
                    double pos = rawDataPanel.getScrollTop();
                    int anchor = rawDataPanel.getAnchor();
                    int caret = rawDataPanel.getCaretPosition();
                    rawDataPanel.setText(rawData);
                    rawDataPanel.setScrollTop(pos);
                    rawDataPanel.selectRange(anchor, caret);
                });
            } else {
                logger.error("Controller has been notified but there was no action.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
