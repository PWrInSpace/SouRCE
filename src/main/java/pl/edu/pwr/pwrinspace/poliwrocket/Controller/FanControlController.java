package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.Observable;
import javafx.fxml.FXML;

public class FanControlController extends BaseController{
    @FXML private JFXButton fanOnButton;
    @FXML private JFXButton fanOffButton;

    @FXML
    private void handleFanOnButtonClick() {
        // Handle fan on button click
        System.out.println("Fan turned ON");
    }

    @FXML
    private void handleFanOffButtonClick() {
        // Handle fan off button click
        System.out.println("Fan turned OFF");
    }

    @Override
    public void invalidated(Observable observable) {

    }
}
