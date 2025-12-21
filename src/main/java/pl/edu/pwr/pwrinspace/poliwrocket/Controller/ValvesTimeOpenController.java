package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValvesTimeOpenController extends BaseCommandsController {

//    private final List<String> commandOrder = Arrays.asList(
//            "TANWA N2O DEPR OPEN TIME",
//            "TANWA N2O FILL OPEN TIME",
//            "N2O VENT OPEN TIME",
//            "N2O MAIN OPEN TIME"
//    );

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

        Map<String, ICommand> byKey = this.commands.stream()
            .collect(Collectors.toMap(ICommand::getCommandTriggerKey, c -> c, (a, b) -> a, java.util.LinkedHashMap::new));

        List<ICommand> orderedCommands = new ArrayList<>();

//        for (String key : commandOrder) {
//            ICommand c = byKey.remove(key);
//            if (c != null) orderedCommands.add(c);
//        }

        orderedCommands.addAll(byKey.values());

        for (ICommand command : orderedCommands) {
            JFXTextField input = new JFXTextField();
            if(command.getPayload() == null) {
                input.setVisible(false);
            } else {
                input.setText(command.getPayload());
            }

            input.setDisable(command.isFinal());

            JFXButton button = new JFXButton(command.getCommandDescription());
//          JFXButton button = new JFXButton("OPEN");
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
