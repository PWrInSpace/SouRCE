package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;

public class PowerController extends BasicController implements InvalidationListener {

    @FXML
    private Gauge powerGauge4;

    @FXML
    private Gauge powerGauge5;

    @FXML
    private Gauge powerGauge6;

    @FXML
    private Gauge powerGauge3;

    @FXML
    private Gauge powerGauge7;

    @FXML
    private Gauge powerGauge2;

    @FXML
    private Gauge powerGauge1;

    @FXML
    private Label powerLabel7;

    @FXML
    private Label powerLabel6;

    @FXML
    private Label powerLabel5;

    @FXML
    private Label powerLabel4;

    @FXML
    private Label powerLabel3;

    @FXML
    private Label powerLabel2;

    @FXML
    private Label powerLabel1;

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.POWER_CONTROLLER;

        assert powerGauge4 != null : "fx:id=\"powerGauge4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge5 != null : "fx:id=\"powerGauge5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge6 != null : "fx:id=\"powerGauge6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge3 != null : "fx:id=\"powerGauge3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge7 != null : "fx:id=\"powerGauge7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge2 != null : "fx:id=\"powerGauge2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge1 != null : "fx:id=\"powerGauge1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel7 != null : "fx:id=\"powerLabel7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel6 != null : "fx:id=\"powerLabel6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel5 != null : "fx:id=\"powerLabel5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel4 != null : "fx:id=\"powerLabel4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel3 != null : "fx:id=\"powerLabel3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel2 != null : "fx:id=\"powerLabel2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel1 != null : "fx:id=\"powerLabel1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
    }

    @Override
    public void invalidated(Observable observable) {
        //TODO to implement
    }
}
