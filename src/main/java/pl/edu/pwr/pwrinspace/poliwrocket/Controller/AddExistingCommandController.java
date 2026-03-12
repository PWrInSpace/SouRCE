package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.StandardCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

import java.security.InvalidParameterException;

public class AddExistingCommandController extends BaseNewComponentController {
    BaseCommandsController parentController;

    @FXML
    private JFXListView<Command<?>> commandListView;
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
    @FXML
    private JFXButton addExistingCommandButton;

    FilteredList<Command<?>> filteredCommandList;

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
        updateFilters();
    }

    private void updateFilters() {
        String type = commandTypeFilter.getSelectionModel().getSelectedItem();
        String device = deviceFilter.getSelectionModel().getSelectedItem();
        String system = systemFilter.getSelectionModel().getSelectedItem();
        String description = descriptionFilter.getText().toUpperCase();
        String trigger = triggerFilter.getText().toUpperCase();
        String commandValue = commandFilter.getText().toUpperCase();

        filteredCommandList.setPredicate(command -> {
            if (parentController != null) {
                for (Object controllerName : command.getDestinationControllerNames()) {
                    if (controllerName.equals(parentController.getControllerName())) return false;
                }
            }
            if (!textFilter(trigger, command.getCommandTriggerKey().toUpperCase())) return false;
            if (!textFilter(description, command.getCommandDescription().toUpperCase())) return false;

            if (command instanceof ProtobufCommand) {
                ProtobufCommand protobufCommand = (ProtobufCommand) command;
                if (!selectionFilter(type, "PROTOBUF")) return false;
                if (!selectionFilter(device, protobufCommand.getValue().getDevice())) return false;
                if (!selectionFilter(system, protobufCommand.getValue().getSystem())) return false;
                return textFilter(commandValue, protobufCommand.getValue().getCommand());
            } else if (command instanceof StandardCommand) {
                StandardCommand standardCommand = (StandardCommand) command;
                if (!selectionFilter(type, "STANDARD")) return false;
                if (!device.equals("none")) return false;
                if (!system.equals("none")) return false;
                return textFilter(commandValue, standardCommand.getValue());
            }
            return true;
        });
    }

    private boolean textFilter(String filter, String text) {
        if (filter.isEmpty()) return true;
        return text.contains(filter);
    }

    private boolean selectionFilter(String filter, String selection) {
        if (filter.equals("none")) return true;
        return selection.equals(filter);
    }

    private Command<?> getSelectedCommand() throws InvalidParameterException {
        if (commandListView.getSelectionModel().getSelectedItem() != null) {
            return commandListView.getSelectionModel().getSelectedItem();
        } else throw new InvalidParameterException("Command not selected");
    }

    @FXML
    public void addExistingCommand() {
        try {
            var command = getSelectedCommand();
            ModelAsYamlService modelAsYamlService = new ModelAsYamlService();
            modelAsYamlService.addCommandToController(new ConfigurationSaveModel(), parentController, command);

            Configuration.getInstance().reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));

            ((Stage) addExistingCommandButton.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
        }
    }
}
