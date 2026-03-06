package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rule {

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("validationTimes")
    private int validationTimes;

    private int validated = 0;

    public Rule() {
    }

    public Rule(String condition, int validationTimes) {
        this.condition = condition;
        this.validationTimes = validationTimes;
    }

    public String getCondition() {
        return condition;
    }

    public int getValidationTimes() {
        return validationTimes;
    }

    public int getValidated() {
        return validated;
    }

    public void setValidated(int validated) {
        this.validated = validated;
    }
}
