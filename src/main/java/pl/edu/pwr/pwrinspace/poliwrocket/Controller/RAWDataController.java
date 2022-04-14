package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

public class RAWDataController extends BasicSensorController {

    @FXML
    private JFXTextArea rawDataPanel;

    @FXML
    protected void initialize() {
    }

    @Override
    protected void setUIBySensors() {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
    }

    @Override
    protected void buildVisualizationMap() {
        //there is now field to display sensor data
        throw new UnsupportedOperationException();
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
