package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;

public interface ISettingsSensor {
    Double getDefaultValue();
    String getInputKey();
    Command getCommand();
}
