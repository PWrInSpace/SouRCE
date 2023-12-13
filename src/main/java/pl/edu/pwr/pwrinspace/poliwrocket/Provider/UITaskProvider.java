package pl.edu.pwr.pwrinspace.poliwrocket.Provider;

import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.InterfaceUpdateTask;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIRunnable;

public class UITaskProvider {

    private UIRunnable scheduledTasks = new UIRunnable();

    private UITaskProvider() {
        if (UITaskProvider.Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    private static class Holder {
        private static final UITaskProvider INSTANCE = new UITaskProvider();
    }

    public static UITaskProvider getInstance() {
        return UITaskProvider.Holder.INSTANCE;
    }

    public void registerTask(InterfaceUpdateTask task) {
        scheduledTasks.addTask(task);
    }

    public void runTasks() {
        scheduledTasks.run();
    }
}
