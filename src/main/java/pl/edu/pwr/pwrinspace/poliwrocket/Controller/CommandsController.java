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

public class CommandsController extends BasicButtonSensorController {

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

        int initYLabel = 39;
        int initYInput = 35;
        int offsetY = 40;

        for (ICommand command : this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList())) {
            Label label = new Label(command.getCommandDescription());
            JFXTextField input = new JFXTextField();
            JFXButton button = new JFXButton("Send");

            label.setLayoutX(14);
            label.setLayoutY(initYLabel);
            label.setPrefHeight(18);
            label.setPrefWidth(180);

            input.setLayoutX(215);
            input.setLayoutY(initYInput);
            input.setPrefHeight(26);
            input.setPrefWidth(70);
            input.setPromptText("X;Y");

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(305);
            button.setLayoutY(initYInput);
            button.setPrefHeight(26);
            button.setPrefWidth(60);

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
