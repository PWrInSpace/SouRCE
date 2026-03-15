package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.CommandType;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content.ProtobufContent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewButtonSensorCommandComponentController extends NewCommandComponentController {
    @FXML
    private JFXComboBox<CommandType> commandTypeComboBox;

    @FXML
    public void initialize() {
        super.initialize();

        commandTypeComboBox.getItems().addAll(CommandType.values());
        commandTypeComboBox.getSelectionModel().selectFirst();
    }

    @Override
    protected ProtobufCommand createProtobufCommand() throws InvalidParameterException {
        String device = protobufDeviceComboBox.getSelectionModel().getSelectedItem();
        String system = protobufSystemComboBox.getSelectionModel().getSelectedItem();
        if (device == null || system == null) throw new InvalidParameterException("Device or system not specified");

        String command = protobufCommandTextArea.getText();
        if (command == null || command.isEmpty()) throw new InvalidParameterException("Invalid command");
        if (protobufCommandFormat.getSelectedToggle() == protobufDecimalFormat) command = Integer.toHexString(Integer.parseInt(command, 16));
        command = command.toUpperCase();
        if (command.length() == 1) command = "0" + command;
        command = "0x" + command;

        ProtobufContent content = ProtobufContent.createProtobufContent(device, system, command);
        boolean isFinal = isFinalToggleButton.isSelected();

        String trigger = triggerTextArea.getText();
        if (trigger == null || trigger.isEmpty()) throw new InvalidParameterException("Invalid trigger");

        String description = descriptionTextArea.getText();
        if (description == null || description.isEmpty()) throw new InvalidParameterException("Invalid description");

        String payload = payloadTextArea.getText();
        if (payload == null || payload.isEmpty()) payload = "";

        CommandType commandType = commandTypeComboBox.getSelectionModel().getSelectedItem();
        if (commandType == null) throw new InvalidParameterException("Invalid command type");

        List<String> destinationControllerNames = new ArrayList<>(Collections.singletonList(parentController.getControllerName()));
        return ProtobufCommand.createProtobufCommand(content, isFinal, trigger, description, payload, commandType, destinationControllerNames);
    }
}
