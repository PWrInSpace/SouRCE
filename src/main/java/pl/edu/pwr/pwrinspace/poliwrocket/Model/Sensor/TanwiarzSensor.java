package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class TanwiarzSensor extends TareSensor {

    private double ratio = 1;

    private boolean isCalibrating = false;

    @Expose
    private int calibrationValue = 1;

    @Expose
    private int calibrationDelayFrames = 7;

    private int calibrationFramesCaught = 0;

    private double initCalibrateValue = 1;

    private double loadedCalibrateValue = 1;

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

    @Override
    public double getValue() {
        return (value - getTareValue()) / ratio;
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
