package pl.edu.pwr.pwrinspace.poliwrocket.Provider;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class DetachedWindowsProvider {

    private static final ObservableList<TabPane> activeTabPanes = FXCollections.observableArrayList();

    public static void addActiveTabPane(TabPane tabPane) {
        activeTabPanes.add(tabPane);
    }

    public static void removeActiveTabPane(TabPane tabPane) {
        activeTabPanes.remove(tabPane);
    }

    public static ObservableList<TabPane> getActiveTabPanes() {
        return activeTabPanes;
    }

    public static void transferTabPane(Tab tab, TabPane targetTabPane) {
        if (tab == null || targetTabPane == null) return;

        TabPane sourceTabPane = tab.getTabPane();
        if(sourceTabPane == targetTabPane) return;

        int targetIndex = -1;
        if(tab.getUserData() instanceof Integer){
            targetIndex = (int) tab.getUserData();
        }

        sourceTabPane.getTabs().remove(tab);

        if(targetIndex >= 0 && targetIndex < targetTabPane.getTabs().size()) {
            targetTabPane.getTabs().add(targetIndex, tab);
        }else{
            targetTabPane.getTabs().add(tab);
        }
        targetTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        if(sourceTabPane.getTabs().isEmpty()){
            Stage stage = (Stage) sourceTabPane.getScene().getWindow();
            if(!stage.getTitle().equals("SouRCE")){
                removeActiveTabPane(sourceTabPane);
                stage.close();
            }
        }

        Platform.runLater(() -> {
            targetTabPane.getSelectionModel().select(tab);

            if (targetTabPane.getScene() != null && targetTabPane.getScene().getWindow() instanceof Stage) {
                Stage targetStage = (Stage) targetTabPane.getScene().getWindow();
                targetStage.toFront();
                targetStage.requestFocus();
            }
        });
    }
}
