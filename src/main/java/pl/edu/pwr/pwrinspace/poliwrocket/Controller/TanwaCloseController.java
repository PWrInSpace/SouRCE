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

public class TanwaCloseController extends BaseCommandsController {
    @FXML
    private AnchorPane mainPanel;

    @Override
    protected void buildVisualizationMap() {
        mainPanel.getChildren().removeIf(node ->
                buttonHashMap.containsValue(node)
        );

        tileHashMap.clear();
        indicatorHashMap.clear();

        int initY = 35;
        int offsetY = 51;

        for (ICommand command : this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList())) {

            JFXButton button = new JFXButton("CLOSE");

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(5);
            button.setLayoutY(initY);
            button.setPrefHeight(34);
            button.setPrefWidth(72);

            mainPanel.getChildren().add(button);

            buttonHashMap.put(command.getCommandTriggerKey(), button);

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
