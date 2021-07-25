package pl.edu.pwr.pwrinspace.poliwrocket.Thred;

import pl.edu.pwr.pwrinspace.poliwrocket.Event.UIUpdateEventEmitter;

import java.util.Timer;
import java.util.TimerTask;

//TODO events are not used yet
public class SchedulerThread {

    private final Timer timer = new Timer();

    private UIUpdateEventEmitter uiUpdateEventEmitter;

    public SchedulerThread(UIUpdateEventEmitter uIUpdateEventEmitter) {
        this.uiUpdateEventEmitter = uIUpdateEventEmitter;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                uiUpdateEventEmitter.emit();
            }
        }, 4000, 1000);
    }

}
