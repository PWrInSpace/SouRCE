package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IAlert;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.TimerSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Skin.TimerSkin;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

public class TimerController extends BasicTilesFXSensorController {

    @FXML
    protected Tile timerTile;

    private TimerSkin timerSkin;

    @FXML
    public void initialize() {
        int countdown = 900;
        timerSkin = new TimerSkin(timerTile, countdown);
        timerTile.setSkin(timerSkin);
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            if (sensor instanceof TimerSensor) {
                timerSkin.setCountdown((int) sensor.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
