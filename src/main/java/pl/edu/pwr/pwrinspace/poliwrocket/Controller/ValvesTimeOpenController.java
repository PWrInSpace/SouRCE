package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

public class ValvesTimeOpenController extends BaseCommandsController {

    public ValvesTimeOpenController() {
        this.initYInput = 38;
        this.offestY = 51;
        this.showLabel = false;
        this.inputLayoutX = 20;
        this.inputPrefHeight = 26;
        this.inputPrefWidth = 70;
        this.inputText = "X;Y";
        this.buttonLayoutX = 90;
        this.buttonPrefHeight = 26;
        this.buttonPrefWidth = 60;
        this.buttonText = "OPEN";
    }
}
