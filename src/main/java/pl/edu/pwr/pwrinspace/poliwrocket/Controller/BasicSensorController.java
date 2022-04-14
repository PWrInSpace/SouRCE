package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.application.Platform;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.util.Collection;
import java.util.HashSet;

public abstract class BasicSensorController extends BasicController {

    protected HashSet<ISensor> sensors = new HashSet<>();

    public final void injectSensorsModels(Collection<ISensor> sensors) {
        this.sensors.clear();
        this.sensors.addAll(sensors);
        Platform.runLater(this::buildUI);
    }

    public final void buildUI() {
        buildVisualizationMap();
        setUIBySensors();
    }

    protected abstract void setUIBySensors();

    protected abstract void buildVisualizationMap();

}
