package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseCommandsController extends BaseButtonSensorController {

    @FXML
    protected AnchorPane mainPanel;

    protected final HashMap<String, JFXTextField> inputHashMap = new HashMap<>();
    protected int initYLabel = 39;
    protected int initYInput = 35;
    protected int offestY = 40;
    protected boolean showLabel = true;
    protected int labelLayoutX = 14;
    protected int labelPrefHeight = 18;
    protected int labelPrefWidth = 180;
    protected int inputLayoutX = 215;
    protected int inputPrefHeight = 26;
    protected int inputPrefWidth = 70;
    protected String inputText = "X;Y";
    protected int buttonLayoutX = 305;
    protected int buttonPrefHeight = 26;
    protected int buttonPrefWidth = 60;
    protected String buttonText = "Send";

    @Override
    protected void buildVisualizationMap() {

        mainPanel.getChildren().removeIf(node ->
            labelHashMap.containsValue(node)
            || inputHashMap.containsValue(node)
            || buttonHashMap.containsValue(node)
        );

        labelHashMap.clear();
        inputHashMap.clear();
        tileHashMap.clear();
        indicatorHashMap.clear();

        List<ICommand> commandList = this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList());

        int initYLabel = this.initYLabel;
        int initYInput = this.initYInput;
        int offsetY = this.offestY;

        for (ICommand command : commandList) {
            Label label = new Label(command.getCommandDescription());
            label.setVisible(showLabel);

            JFXTextField input = new JFXTextField();
            if (command.getPayload() == null) {
                input.setVisible(false);
            } else {
                input.setText(command.getPayload());
            }
            input.setDisable(command.isFinal());

            JFXButton button = new JFXButton(buttonText);

            label.setLayoutX(labelLayoutX);
            label.setLayoutY(initYLabel);
            label.setPrefHeight(labelPrefHeight);
            label.setPrefWidth(labelPrefWidth);

            input.setLayoutX(inputLayoutX);
            input.setLayoutY(initYInput);
            input.setPrefHeight(inputPrefHeight);
            input.setPrefWidth(inputPrefWidth);
            input.setPromptText(inputText);

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(buttonLayoutX);
            button.setLayoutY(initYInput);
            button.setPrefHeight(buttonPrefHeight);
            button.setPrefWidth(buttonPrefWidth);

            mainPanel.getChildren().add(label);
            mainPanel.getChildren().add(input);
            mainPanel.getChildren().add(button);

            labelHashMap.put(command.getCommandTriggerKey(), label);
            inputHashMap.put(command.getCommandTriggerKey(), input);
            buttonHashMap.put(command.getCommandTriggerKey(), button);

            initYLabel += offsetY;
            initYInput += offsetY;

        }
    }

    @Override
    public void assignsCommands(Collection<ICommand> commands){
        Platform.runLater(this::buildVisualizationMap);
        super.assignsCommands(commands);
    }

    protected void setUIBySensors() {}

    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command){
        return actionEvent -> executorService.execute(() -> {
            System.out.println(button.getId());
            command.setPayload(inputHashMap.get(command.getCommandTriggerKey()).getText());
            SerialPortManager.getInstance().write(command);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        logger.error("Controller has no visualization.");
    }
}
