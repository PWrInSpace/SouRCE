package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;

public class NewCommandComponentController extends BaseNewComponentController {
    BaseCommandsController parentController;

    @FXML
    private ToggleGroup commandType;
    @FXML
    private ToggleButton protobufType;
    @FXML
    private ToggleButton standardType;
    @FXML
    private AnchorPane protobufCommandDetails;
    @FXML
    private AnchorPane standardCommandDetails;
    @FXML
    private ComboBox<String> protobufDeviceComboBox;
    @FXML
    private ComboBox<String> protobufSystemComboBox;
    @FXML
    private TextArea protobufCommandTextArea;
    @FXML
    private ToggleButton protobufCommandIsFinalToggleButton;
    @FXML
    private ToggleGroup protobufCommandFormat;
    @FXML
    private ToggleButton protobufHexFormat;
    @FXML
    private ToggleButton protobufDecimalFormat;
    @FXML
    private TextArea protobufTriggerTextArea;
    @FXML
    private TextArea protobudDescriptionTextArea;

    @FXML
    public void initialize() {
        protobufDeviceComboBox.getItems().addAll("ALL", "OBC", "TANWA");
        protobufDeviceComboBox.getSelectionModel().selectFirst();
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
            if (protobufCommandFormat.getSelectedToggle() == protobufHexFormat && newText.matches("[0-9a-fA-F]*")) return change;
            else if (protobufCommandFormat.getSelectedToggle() == protobufDecimalFormat && newText.matches("[0-9]*")) return change;
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
        //debugging
        ProtobufCommand protobufCommand = new ProtobufCommand();
    }

    public void setParentController(BaseCommandsController parentController) {
        this.parentController = parentController;
    }
}
