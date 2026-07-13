package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;

public class SensorsConfig extends BaseSaveModel {
    @Expose public SensorRepository sensorRepository = new SensorRepository();

    public SensorsConfig() {
        super(Configuration.CONFIG_PATH, "SensorConfig.json");
    }
}