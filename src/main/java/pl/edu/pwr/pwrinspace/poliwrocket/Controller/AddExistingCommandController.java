package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.StandardCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;

public class AddExistingCommandController extends BaseNewComponentController {
    BaseCommandsController parentController;

    @FXML
    private JFXListView<Command> commandListView;
    @FXML
    private JFXComboBox<String> commandTypeFilter;
    @FXML
    private JFXComboBox<String> deviceFilter;
    @FXML
    private JFXComboBox<String> systemFilter;
    @FXML
    private JFXTextArea descriptionFilter;
    @FXML
    private JFXTextArea triggerFilter;
    @FXML
    private JFXTextArea commandFilter;
    @FXML
    private JFXToggleButton isFinalFilter;

    FilteredList<Command> filteredCommandList;

    @FXML
    public void initialize() {
        commandListView.setCellFactory(cell -> new ListCell<>() {
            @Override
            protected void updateItem(Command item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                }
                else {
                    String listViewString = item.getListViewString();
                    setText(listViewString);
                }
            }
        });

        commandTypeFilter.getItems().addAll("none", "PROTOBUF", "STANDARD");
        commandTypeFilter.getSelectionModel().selectFirst();

        deviceFilter.getItems().add("none");
        deviceFilter.getItems().addAll(Configuration.getInstance().protobufDeviceRepository.getDeviceSet());
        deviceFilter.getSelectionModel().selectFirst();

        systemFilter.getItems().add("none");
        systemFilter.getItems().addAll(Configuration.getInstance().protobufSystemRepository.getSystemSet());
        systemFilter.getSelectionModel().selectFirst();

        filteredCommandList = new FilteredList<>(FXCollections.observableList(Configuration.getInstance().commandsList));
        commandListView.setItems(filteredCommandList);
        updateFilters();

        //ustawianie listeners
        commandTypeFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        deviceFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        systemFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        descriptionFilter.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        triggerFilter.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        commandFilter.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        isFinalFilter.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    public void setParentController(BaseCommandsController parentController) {
        this.parentController = parentController;
    }

    private void updateFilters() {
        filteredCommandList.setPredicate(command -> {
            if (command instanceof ProtobufCommand) {
                ProtobufCommand protobufCommand = (ProtobufCommand) command;
                return (commandTypeFilter.getSelectionModel().getSelectedItem().equals("none") || commandTypeFilter.getSelectionModel().getSelectedItem().equals("PROTOBUF")) &&
                        (deviceFilter.getSelectionModel().getSelectedItem().equals("none") || deviceFilter.getSelectionModel().getSelectedItem().equals(protobufCommand.getValue().getDevice())) &&
                        (systemFilter.getSelectionModel().getSelectedItem().equals("none") || systemFilter.getSelectionModel().getSelectedItem().equals(protobufCommand.getValue().getSystem())) &&
                        (descriptionFilter.getText().isEmpty() || command.getCommandDescription().contains(descriptionFilter.getText())) &&
                        (triggerFilter.getText().isEmpty() || command.getCommandTriggerKey().contains(triggerFilter.getText()) &&
                                (commandFilter.getText().isEmpty() || protobufCommand.getValue().getCommand().contains(commandFilter.getText())));
            } else if (command instanceof StandardCommand) {
                return (commandTypeFilter.getSelectionModel().getSelectedItem().equals("none") || commandTypeFilter.getSelectionModel().getSelectedItem().equals("STANDARD")) &&
                        deviceFilter.getSelectionModel().getSelectedItem().equals("none") &&
                        systemFilter.getSelectionModel().getSelectedItem().equals("none") &&
                        (descriptionFilter.getText().isEmpty() || command.getCommandDescription().contains(descriptionFilter.getText())) &&
                        (triggerFilter.getText().isEmpty() || command.getCommandTriggerKey().contains(triggerFilter.getText())) &&
                        (commandFilter.getText().isEmpty() || command.getCommandValueAsString().contains(commandFilter.getText()));
            }
            return false;
        });
    }
}
