package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.CommandType;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content.ProtobufContent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewCommandComponentController extends BaseNewComponentController {
    protected BaseCommandsController parentController;

    @FXML
    protected ToggleGroup commandType;
    @FXML
    protected JFXToggleButton protobufType;
    @FXML
    protected JFXToggleButton standardType;
    @FXML
    protected AnchorPane protobufCommandDetails;
    @FXML
    protected AnchorPane standardCommandDetails;
    @FXML
    protected JFXComboBox<String> protobufDeviceComboBox;
    @FXML
    protected JFXComboBox<String> protobufSystemComboBox;
    @FXML
    protected JFXTextArea protobufCommandTextArea;
    @FXML
    protected JFXToggleButton isFinalToggleButton;
    @FXML
    protected ToggleGroup protobufCommandFormat;
    @FXML
    protected JFXToggleButton protobufHexFormat;
    @FXML
    protected JFXToggleButton protobufDecimalFormat;
    @FXML
    protected JFXTextArea triggerTextArea;
    @FXML
    protected JFXTextArea descriptionTextArea;
    @FXML
    protected JFXTextArea payloadTextArea;
    @FXML
    protected JFXButton addCommandButton;

    @FXML
    public void initialize() {
        protobufDeviceComboBox.getItems().addAll(Configuration.getInstance().protobufDeviceRepository.getDeviceSet());
        protobufDeviceComboBox.getSelectionModel().selectFirst();
        protobufSystemComboBox.getItems().addAll(Configuration.getInstance().protobufSystemRepository.getSystemSet());
        protobufSystemComboBox.getSelectionModel().selectFirst();

        handleCommandTypeChange();

        protobufType.addEventFilter(MouseEvent.MOUSE_PRESSED,event -> {
            if (protobufType.isSelected()) event.consume();
        });
        standardType.addEventFilter(MouseEvent.MOUSE_PRESSED,event -> {
            if (standardType.isSelected()) event.consume();
        });
        protobufHexFormat.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (protobufHexFormat.isSelected()) event.consume();
        });
        protobufDecimalFormat.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (protobufDecimalFormat.isSelected()) event.consume();
        });

        protobufCommandTextArea.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getText();
            if (protobufCommandFormat.getSelectedToggle() == protobufHexFormat && newText.matches("[0-9a-fA-F]*") && change.getControlNewText().length() <= 2) return change;
            else if (protobufCommandFormat.getSelectedToggle() == protobufDecimalFormat && newText.matches("[0-9]*") && change.getControlNewText().length() <= 3) {
                if (!change.getControlNewText().isEmpty() && Integer.parseInt(change.getControlNewText()) > 255) {
                    protobufCommandTextArea.setText("255");
                    return null;
                }
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void handleCommandTypeChange() {
        if (commandType.getSelectedToggle() == protobufType) {
            System.out.println("protobufType");
            if (parentController != null) System.out.println(parentController.getControllerName());
            standardCommandDetails.setVisible(false);
            standardCommandDetails.setMouseTransparent(true);
            protobufCommandDetails.setVisible(true);
            protobufCommandDetails.setMouseTransparent(false);
        } else if (commandType.getSelectedToggle() == standardType) {
            System.out.println("standardType");
            protobufCommandDetails.setVisible(false);
            protobufCommandDetails.setMouseTransparent(true);
            standardCommandDetails.setVisible(true);
            standardCommandDetails.setMouseTransparent(false);
        }
    }

    @FXML
    private void handleCommandFormatChange() {
        if (commandType.getSelectedToggle() == protobufType) {
            if (protobufCommandFormat.getSelectedToggle() == protobufHexFormat && protobufCommandTextArea.getText().matches("[0-9]+")) {
                 int inputDecimal = Integer.parseInt(protobufCommandTextArea.getText());
                 String inputHex = Integer.toHexString(inputDecimal).toUpperCase();
                 protobufCommandTextArea.setText(inputHex);
            }
            else if (protobufCommandFormat.getSelectedToggle() == protobufDecimalFormat && protobufCommandTextArea.getText().matches("[0-9a-fA-F]+")) {
                int inputHex = Integer.parseInt(protobufCommandTextArea.getText(), 16);
                String inputDecimal = Integer.toString(inputHex);
                protobufCommandTextArea.setText(inputDecimal);
            }
        }
    }

    @FXML
    private void addProtobufCommand() {
        ModelAsYamlService modelAsYamlService = new ModelAsYamlService();
        try {
            ProtobufCommand command = createProtobufCommand();
            modelAsYamlService.addCommandToFile(new ConfigurationSaveModel(), command);

            Configuration.getInstance().reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));

            ((Stage) addCommandButton.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void setParentController(BaseCommandsController parentController) {
        this.parentController = parentController;
    }

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
        if (payload == null || payload.isEmpty()) payload = null;

        List<String> destinationControllerNames = new ArrayList<>(Collections.singletonList(parentController.getControllerName()));
        return ProtobufCommand.createProtobufCommand(content, isFinal, trigger, description, payload, CommandType.INPUT_COMMAND, destinationControllerNames);
    }
}
