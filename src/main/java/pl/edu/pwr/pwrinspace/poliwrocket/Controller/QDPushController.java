package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;

import java.util.Comparator;
import java.util.stream.Collectors;

public class QDPushController extends BaseCommandsController {
    @FXML
    private AnchorPane mainPanel;

    @Override
    protected void buildVisualizationMap() {
        mainPanel.getChildren().removeIf(node ->
                labelHashMap.containsValue(node)
                        || buttonHashMap.containsValue(node)
        );

        tileHashMap.clear();
        indicatorHashMap.clear();
        labelHashMap.clear();

        int initYLabel = 20;
        int initY = 10;
        int offsetY = 51;

        for (ICommand command : this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList())) {
            Label label = new Label(command.getCommandDescription());

            JFXButton button = new JFXButton("PUSH");

            label.setLayoutX(14);
            label.setLayoutY(initYLabel);
            label.setPrefHeight(18);
            label.setPrefWidth(180);

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(180);
            button.setLayoutY(initY);
            button.setPrefHeight(34);
            button.setPrefWidth(72);

            mainPanel.getChildren().add(label);
            mainPanel.getChildren().add(button);

            labelHashMap.put(command.getCommandTriggerKey(), label);
            buttonHashMap.put(command.getCommandTriggerKey(), button);

            initYLabel += offsetY;
            initY += offsetY;

        }
    }

    @Override
    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command){
        return actionEvent -> executorService.execute(() -> {
            SerialPortManager.getInstance().write(command);
        });
    }
}
