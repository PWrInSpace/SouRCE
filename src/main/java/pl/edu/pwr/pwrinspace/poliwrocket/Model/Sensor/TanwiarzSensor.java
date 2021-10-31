package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class TanwiarzSensor extends Sensor {

    @Expose
    private double ratio = 1;

    private boolean isCalibrating = false;

    @Expose
    private int calibrationValue = 1;

    @Expose
    private int calibrationDelayFrames = 7;

    private int calibrationFramesCaught = 0;

    @Expose
    private double initCalibrateValue = 1;

    @Expose
    private double loadedCalibrateValue = 1;

    @Expose
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
