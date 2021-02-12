package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.google.gson.annotations.Expose;

public class Rule {
    @Expose
    private String condition;

    @Expose
    private int validationTimes;

    private int validated = 0;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getValidationTimes() {
        return validationTimes;
    }

    public void setValidationTimes(int validationTimes) {
        this.validationTimes = validationTimes;
    }

    public int getValidated() {
        return validated;
    }

    public void setValidated(int validated) {
        this.validated = validated;
    }
}
