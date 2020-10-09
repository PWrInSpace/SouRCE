package pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.ISensorUI;
import java.util.Collection;
import java.util.HashSet;

public abstract class BasicSensorController extends BasicController implements InvalidationListener {


    protected HashSet<ISensorUI> sensors = new HashSet<>();

    public void injectSensorsModels(Collection<ISensorUI> sensors) {
        this.sensors.addAll(sensors);
        setUIBySensors();
    }

    protected abstract void setUIBySensors();

}
