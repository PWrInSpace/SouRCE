package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("!TanwiarzSensor")
public class TanwiarzSensor extends Sensor {

    @JsonProperty("ratio")
    private double ratio = 1;

    private boolean isCalibrating = false;

    @JsonProperty("calibrationValue")
    private int calibrationValue = 1;

    @JsonProperty("calibrationDelayFrames")
    private int calibrationDelayFrames = 7;

    private int calibrationFramesCaught = 0;

    @JsonProperty("initCalibrateValue")
    private double initCalibrateValue = 1;

    @JsonProperty("loadedCalibrateValue")
    private double loadedCalibrateValue = 1;

    @JsonProperty("tareValue")
    private double tareValue = 0;

    public double getTareValue() {
        return tareValue;
    }

    public void setTareValue(double newValue) {
        tareValue = newValue;
    }

    public double getRatio() {
        return ratio;
    }
    @Override
    public void setValue(double newValue) {
        super.setValue(newValue);
        if(isCalibrating) {
            calibrationFramesCaught++;
            if(calibrationFramesCaught >= calibrationDelayFrames) {
                isCalibrating = false;
                saveLoadedValue();
                saveRatio();
            }
        }

    }

    private void saveRatio() {
        ratio = (loadedCalibrateValue - initCalibrateValue) / calibrationValue;
    }

    public double getValue() {
        return (value - tareValue) / ratio;
    }

    public void saveInitValue() {
        initCalibrateValue = this.value;
    }

    public void saveLoadedValue() {
        loadedCalibrateValue = this.value;
    }

    public void startCalibration() {
        saveInitValue();
        isCalibrating = true;
        calibrationFramesCaught = 0;
        setTareValue(initCalibrateValue);
    }

    public int getCalibrationValue(){
        return calibrationValue;
    }

}
