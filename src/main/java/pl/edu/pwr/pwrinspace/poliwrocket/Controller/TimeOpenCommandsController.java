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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TimeOpenCommandsController extends BasicButtonSensorController {

    @FXML
    private AnchorPane mainPanel;

    protected final HashMap<String, JFXTextField> inputHashMap = new HashMap<>();

    protected void buildVisualizationMap() {

        mainPanel.getChildren().removeIf(node ->
                labelHashMap.containsValue(node)
                        || inputHashMap.containsValue(node)
                        || buttonHashMap.containsValue(node)
        );

        tileHashMap.clear();
        indicatorHashMap.clear();
        labelHashMap.clear();
        inputHashMap.clear();

        int initXLabel = 15;
        int initXInput = 30;
        int initXButton = 150;
        int LabelY = 5;
        int InputY = 25;
        int ButtonY = 20;
        int offsetX = 230;

        for (ICommand command : this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList())) {
            Label label = new Label(command.getCommandDescription());
            JFXTextField input = new JFXTextField();
            if(command.getPayload() == null) {
                input.setVisible(false);
            } else {
                input.setText(command.getPayload());
            }

            input.setDisable(command.isFinal());

            JFXButton button = new JFXButton("Send");

            label.setLayoutX(initXLabel);
            label.setLayoutY(LabelY);
            label.setPrefHeight(18);
            label.setPrefWidth(180);

            input.setLayoutX(initXInput);
            input.setLayoutY(InputY);
            input.setPrefHeight(26);
            input.setPrefWidth(70);
            input.setPromptText("X;Y");

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(initXButton);
            button.setLayoutY(ButtonY);
            button.setPrefHeight(26);
            button.setPrefWidth(60);

            mainPanel.getChildren().add(label);
            mainPanel.getChildren().add(input);
            mainPanel.getChildren().add(button);

            labelHashMap.put(command.getCommandTriggerKey(), label);
            inputHashMap.put(command.getCommandTriggerKey(), input);
            buttonHashMap.put(command.getCommandTriggerKey(), button);

            initXLabel += offsetX;
            initXInput += offsetX;
            initXButton += offsetX;
        }
    }

    @Override
    public void assignsCommands(Collection<ICommand> commands){
        this.commands.clear();
        this.commands.addAll(commands);
        Platform.runLater(this::buildVisualizationMap);
        super.assignsCommands(commands);
    }

    protected void setUIBySensors() {

    }

    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command){
        return actionEvent -> executorService.execute(() -> {
            command.setPayload(inputHashMap.get(command.getCommandTriggerKey()).getText());
            SerialPortManager.getInstance().write(command);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        logger.error("Controller has no visualization.");
    }
}
