package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CodeInterpreterUIHint;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

// from now this controller will work similar to normal BaseButtonSensorController
// differences
// it's dynamic - you don't have to define its fields
// it supports buttons with payload

public abstract class BaseButtonSensorCommandsController extends BaseCommandsController {

    @FXML
    protected AnchorPane mainPanel;

    protected final ArrayList<String> moduleArray = new ArrayList<>();
    protected final HashMap<String, ISensor> sensorHashMap = new HashMap<>();
    protected final HashMap<String, JFXTextField> inputHashMap = new HashMap<>();
    protected final HashMap<String, Button> openHashMap = new HashMap<>();
    protected final HashMap<String, Button> closeHashMap = new HashMap<>();
    protected final HashMap<String, Button> commandHashMap = new HashMap<>();

    // indicator
    protected int initYIndicator = 30;
    protected int indicatorLayoutX = 115;
    protected int indicatorPrefHeight = 42;
    protected int indicatorPrefWidth = 42;

    // open button
    protected int openButtonLayoutX = 167;
    protected int openButtonPrefHeight = 26;
    protected int openButtonPrefWidth = 60;

    // close button
    protected int closeButtonLayoutX = 237;
    protected int closeButtonPrefHeight = 26;
    protected int closeButtonPrefWidth = 60;

    public BaseButtonSensorCommandsController() {
        this.initY = 38;
        this.initYLabel = 42;
        this.offestY = 45;
        this.labelPrefWidth = 90;
        this.inputLayoutX = 307;
        this.commandButtonLayoutX = 387;
    }

    @Override
    protected void buildVisualizationMap() {

        mainPanel.getChildren().removeIf(node ->
                labelHashMap.containsValue(node)
                        || inputHashMap.containsValue(node)
                        || buttonHashMap.containsValue(node)
                        || indicatorHashMap.containsValue(node)
        );

        tileHashMap.clear();
        indicatorHashMap.clear();
        labelHashMap.clear();
        inputHashMap.clear();
        buttonHashMap.clear();
        sensorHashMap.clear();
        openHashMap.clear();
        closeHashMap.clear();
        commandHashMap.clear();
        moduleArray.clear();

        for (ISensor sensor : sensors) {
            String sensorName = sensor.getName().toLowerCase().replaceAll(" ", "");
            sensorHashMap.put(sensorName, sensor);
            indicatorHashMap.put(sensorName, new Indicator());
            moduleArray.add(sensorName);
        }
        moduleArray.sort((String sensorName1, String sensorName2) -> {
            var sensor1 = sensorHashMap.get(sensorName1);
            var sensor2 = sensorHashMap.get(sensorName2);
            return sensor1.getDestination().compareTo(sensor2.getDestination());
        });

        List<ICommand> commandList = this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList());

        for (ICommand command : commandList) {
            String commandTriggerKey = command.getCommandTriggerKey();

            if (commandTriggerKey.endsWith("Open")) {
                commandTriggerKey = commandTriggerKey.toLowerCase().replace("open", "");
                var button = new JFXButton(command.getCommandDescription());
                openHashMap.put(commandTriggerKey, button);
                buttonHashMap.put(command.getCommandTriggerKey(), button);
            }
            else if (commandTriggerKey.endsWith("Close")) {
                commandTriggerKey = commandTriggerKey.toLowerCase().replace("close", "");
                var button = new JFXButton(command.getCommandDescription());
                closeHashMap.put(commandTriggerKey, button);
                buttonHashMap.put(command.getCommandTriggerKey(), button);
            }
            else if (commandTriggerKey.endsWith("Command")) {
                commandTriggerKey = commandTriggerKey.toLowerCase().replace("command", "");
                var button = new JFXButton(command.getCommandDescription());
                commandHashMap.put(commandTriggerKey, button);
                buttonHashMap.put(command.getCommandTriggerKey(), button);
                var input = new JFXTextField();
                if (command.getPayload() == null) input.setVisible(false);
                else input.setText(command.getPayload());
                input.setDisable(command.isFinal());
                inputHashMap.put(commandTriggerKey, input);
                inputHashMap.put(command.getCommandTriggerKey(), input);
            }

            if (!moduleArray.contains(commandTriggerKey)) moduleArray.add(commandTriggerKey);
        }

        int initY = this.initY;
        int initYLabel = this.initYLabel;
        int initYIndicator = this.initYIndicator;
        int offsetY = this.offestY;

        for (String module : moduleArray) {
            var sensor = sensorHashMap.get(module);
            var indicator = indicatorHashMap.get(module);
            var openButton = openHashMap.get(module);
            var closeButton = closeHashMap.get(module);
            var input = inputHashMap.get(module);
            var commandButton = commandHashMap.get(module);

            if (sensor != null) {
                var label = new Label(sensor.getName());
                label.setVisible(showLabel);
                label.setLayoutY(initYLabel);
                label.setLayoutX(labelLayoutX);
                label.setPrefHeight(labelPrefHeight);
                label.setPrefWidth(labelPrefWidth);
                labelHashMap.put(module, label);
                mainPanel.getChildren().add(label);
            }
            // temporary
            else if (commandButton != null) {
                var label = new Label(commandButton.getText());
                label.setVisible(showLabel);
                label.setLayoutY(initYLabel);
                label.setLayoutX(labelLayoutX);
                label.setPrefHeight(labelPrefHeight);
                label.setPrefWidth(labelPrefWidth);
                labelHashMap.put(module, label);
                mainPanel.getChildren().add(label);
                commandButton.setText(commandButtonText);
            }

            if (indicator != null) {
                indicator.setLayoutY(initYIndicator);
                indicator.setLayoutX(indicatorLayoutX);
                indicator.setPrefHeight(indicatorPrefHeight);
                indicator.setPrefWidth(indicatorPrefWidth);
                mainPanel.getChildren().add(indicator);
            }

            if (openButton != null) {
                openButton.setLayoutY(initY);
                openButton.setLayoutX(openButtonLayoutX);
                openButton.setPrefHeight(openButtonPrefHeight);
                openButton.setPrefWidth(openButtonPrefWidth);
                mainPanel.getChildren().add(openButton);
            }

            if (closeButton != null) {
                closeButton.setLayoutY(initY);
                closeButton.setLayoutX(closeButtonLayoutX);
                closeButton.setPrefHeight(closeButtonPrefHeight);
                closeButton.setPrefWidth(closeButtonPrefWidth);
                mainPanel.getChildren().add(closeButton);
            }

            if (input != null) {
                input.setLayoutY(initY);
                input.setLayoutX(inputLayoutX);
                input.setPrefHeight(inputPrefHeight);
                input.setPrefWidth(inputPrefWidth);
                input.setPromptText(inputText);
                mainPanel.getChildren().add(input);
            }

            if (commandButton != null) {
                commandButton.setLayoutY(initY);
                commandButton.setLayoutX(commandButtonLayoutX);
                commandButton.setPrefHeight(commandButtonPrefHeight);
                commandButton.setPrefWidth(commandButtonPrefWidth);
                mainPanel.getChildren().add(commandButton);
            }

            initY += offsetY;
            initYLabel += offsetY;
            initYIndicator += offsetY;
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {
                var ind = indicatorHashMap.get(sensor.getName().toLowerCase().replaceAll(" ", ""));

                if (ind != null) {
                    ind.setDotOnColor(sensor.hasInterpreter() ? UIHelper.resolveUIHintColor(sensor.getCodeMeaning().UIHint) : Color.DODGERBLUE);
                    ind.setOn(sensor.getValue() == 1.0);
                }

                if (sensor.hasInterpreter()) {
                    boolean isNotClosed = sensor.getCodeMeaning().UIHint != CodeInterpreterUIHint.CLOSE;
                    var closeBtn = closeHashMap.get(sensor.getDestination());
                    var openBtn = openHashMap.get(sensor.getDestination());
                    if (closeBtn != null) closeBtn.setDefaultButton(isNotClosed);
                    if (openBtn != null) openBtn.setDefaultButton(!isNotClosed);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
