package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;

import java.util.Comparator;
import java.util.stream.Collectors;

public class FillingCommandsController extends BaseCommandsController {

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

        int initYLabel = 39;
        int initYInput = 35;
        int offsetY = 40;

        for (ICommand command : this.commands.stream().sorted(Comparator.comparing(ICommand::getCommandDescription)).collect(Collectors.toList())) {
            Label label = new Label(command.getCommandDescription());

            JFXButton button = new JFXButton("OPEN");

            label.setLayoutX(14);
            label.setLayoutY(initYLabel);
            label.setPrefHeight(18);
            label.setPrefWidth(180);

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(250);
            button.setLayoutY(initYInput);
            button.setPrefHeight(26);
            button.setPrefWidth(60);

            mainPanel.getChildren().add(label);
            mainPanel.getChildren().add(button);

            labelHashMap.put(command.getCommandTriggerKey(), label);
            buttonHashMap.put(command.getCommandTriggerKey(), button);

            initYLabel += offsetY;
            initYInput += offsetY;

        }
    }
}
