package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ValvesTimeOpenController extends BaseCommandsController {

    private final java.util.List<String> commandOrder = java.util.Arrays.asList(
            "TANWA N2O DEPR OPEN TIME",
            "TANWA N2O FILL OPEN TIME"
    );

    @Override
    protected void buildVisualizationMap() {

        mainPanel.getChildren().removeIf(node ->
                labelHashMap.containsValue(node)
                        || inputHashMap.containsValue(node)
                        || buttonHashMap.containsValue(node)
        );

        tileHashMap.clear();
        indicatorHashMap.clear();
        inputHashMap.clear();

        int initYInput = 38;
        int offsetY = 51;

        java.util.Map<String, ICommand> byKey = this.commands.stream()
                .collect(Collectors.toMap(ICommand::getCommandTriggerKey, c -> c, (a, b) -> a, java.util.LinkedHashMap::new));

        java.util.List<ICommand> orderedCommands = new java.util.ArrayList<>();

        for (String key : commandOrder) {
            ICommand c = byKey.remove(key);
            if (c != null) orderedCommands.add(c);
        }

        orderedCommands.addAll(byKey.values().stream()
                .sorted(Comparator.comparing(ICommand::getCommandDescription))
                .collect(Collectors.toList()));

        for (ICommand command : orderedCommands) {
            Label label = new Label(command.getCommandDescription());
            JFXTextField input = new JFXTextField();
            if(command.getPayload() == null) {
                input.setVisible(false);
            } else {
                input.setText(command.getPayload());
            }

            input.setDisable(command.isFinal());

//            JFXButton button = new JFXButton(command.getCommandDescription());
            JFXButton button = new JFXButton("OPEN");
            input.setLayoutX(20);
            input.setLayoutY(initYInput);
            input.setPrefHeight(26);
            input.setPrefWidth(70);
            input.setPromptText("X;Y");

            button.setId(command.getCommandTriggerKey());
            button.setLayoutX(90);
            button.setLayoutY(initYInput);
            button.setPrefHeight(26);
            button.setPrefWidth(60);

            mainPanel.getChildren().add(input);
            mainPanel.getChildren().add(button);

            inputHashMap.put(command.getCommandTriggerKey(), input);
            buttonHashMap.put(command.getCommandTriggerKey(), button);

            initYInput += offsetY;
        }
    }
}
