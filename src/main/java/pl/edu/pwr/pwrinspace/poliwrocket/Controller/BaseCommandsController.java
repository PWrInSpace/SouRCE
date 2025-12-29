package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CodeInterpreterUIHint;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.*;
import java.util.stream.Collectors;

// from now this controller will work similar to normal BaseButtonSensorController
// differences
// it's dynamic - you don't have to define its fields
// it supports buttons with payload

public abstract class BaseCommandsController extends BaseButtonSensorController {

    @FXML
    protected AnchorPane mainPanel;

    protected final TreeMap<String, ISensor> sensorTreeMap = new TreeMap<>();
    protected final ArrayList<String> sensorArray = new ArrayList<>();
    protected final HashMap<String, JFXTextField> inputHashMap = new HashMap<>();
    protected final HashMap<String, Button> openHashMap = new HashMap<>();
    protected final HashMap<String, Button> closeHashMap = new HashMap<>();
    protected final HashMap<String, Button> openTimeHashMap = new HashMap<>();


    protected int offestY = 40;
    protected int initY = 35;

    // label
    protected boolean showLabel = true;
    protected int labelLayoutX = 15;
    protected int labelPrefHeight = 18;
    protected int labelPrefWidth = 180;

    // indicator
    protected int indicatorLayoutX = 195;
    protected int indicatorPrefHeight = 42;
    protected int indicatorPrefWidth = 42;

    // open button
    protected int openButtonLayoutX = 210;
    protected int openButtonPrefHeight = 26;
    protected int openButtonPrefWidth = 60;

    // close button
    protected int closeButtonLayoutX = 270;
    protected int closeButtonPrefHeight = 26;
    protected int closeButtonPrefWidth = 60;

    // input
    protected int inputLayoutX = 330;
    protected int inputPrefHeight = 26;
    protected int inputPrefWidth = 70;
    protected String inputText = "X;Y";

    // open time button
    protected int openTimeButtonLayoutX = 400;
    protected int openTimeButtonPrefHeight = 26;
    protected int openTimeButtonPrefWidth = 60;


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
        sensorTreeMap.clear();
        openHashMap.clear();
        closeHashMap.clear();
        openTimeHashMap.clear();

        for (ISensor sensor : sensors) {
            sensorTreeMap.put(sensor.getDestination(), sensor);
            sensorArray.add(sensor.getName().toLowerCase().replaceAll(" ", ""));
            indicatorHashMap.put(sensor.getDestination(), new Indicator());
        }

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
                openTimeHashMap.put(commandTriggerKey, button);
                buttonHashMap.put(command.getCommandTriggerKey(), button);
                var input = new JFXTextField();
                inputHashMap.put(commandTriggerKey, input);
                inputHashMap.put(command.getCommandTriggerKey(), input);
//                if (command.getPayload() != null) inputHashMap.get(commandTriggerKey).setText(command.getPayload());
            }

            // todo find way to generate dynamic destination
            if (!sensorArray.contains(commandTriggerKey)) {
                var sensor = new Sensor(commandTriggerKey);
                sensorTreeMap.put("zzz", sensor);
                sensorArray.add(commandTriggerKey);
            }
        }

        int initY = this.initY;
        int offsetY = this.offestY;

        for (ISensor sensor : sensorTreeMap.values()) {
            Label label = new Label(sensor.getName());
            label.setVisible(showLabel);
            var indicator = indicatorHashMap.get(sensor.getDestination());
            var openButton = openHashMap.get(sensor.getName().toLowerCase().replaceAll(" ", ""));
            var closeButton = closeHashMap.get(sensor.getName().toLowerCase().replaceAll(" ", ""));
            var input = inputHashMap.get(sensor.getName().toLowerCase().replaceAll(" ", ""));
            var timeOpenButton = openTimeHashMap.get(sensor.getName().toLowerCase().replaceAll(" ", ""));

            label.setLayoutY(initY);
            label.setLayoutX(labelLayoutX);
            label.setPrefHeight(labelPrefHeight);
            label.setPrefWidth(labelPrefWidth);
            labelHashMap.put(sensor.getDestination(), label);
            mainPanel.getChildren().add(label);

            if (indicator != null) {
                indicator.setLayoutY(initY);
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

            if (timeOpenButton != null) {
                timeOpenButton.setLayoutY(initY);
                timeOpenButton.setLayoutX(openTimeButtonLayoutX);
                timeOpenButton.setPrefHeight(openTimeButtonPrefHeight);
                timeOpenButton.setPrefWidth(openTimeButtonPrefWidth);
                mainPanel.getChildren().add(timeOpenButton);
            }

            initY += offsetY;
        }
    }

    @Override
    public void assignsCommands(Collection<ICommand> commands){
        Platform.runLater(this::buildVisualizationMap);
        super.assignsCommands(commands);
    }

    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command){
        if (!command.isFinal()) {
            return actionEvent -> executorService.execute(() -> {
                command.setPayload(inputHashMap.get(command.getCommandTriggerKey()).getText());
                SerialPortManager.getInstance().write(command);
            });
        }
        else {
            return actionEvent -> executorService.execute(() -> {
                SerialPortManager.getInstance().write(command);
            });
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediateOnOK(() -> {
                var ind = indicatorHashMap.get(sensor.getDestination());

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
