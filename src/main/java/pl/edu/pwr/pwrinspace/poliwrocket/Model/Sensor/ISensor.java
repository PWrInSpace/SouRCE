package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import java.time.Instant;
import java.util.List;

public interface ISensor {

    double getValue();
    String getDestination();
    String getName();
    List<String> getDestinationControllerNames();
    void setValue(double newValue);
    void setDestination(String destination);
    String getUnit();
    double getMaxRange();
    double getMinRange();
    boolean isBoolean();
    Instant getTimeStamp();
    InterpreterValue getCodeMeaning();
    void setInterpreter(CodeInterpreter interpreter);
    String getInterpreterKey();
    boolean hasInterpreter();
    boolean isHidden();
}
