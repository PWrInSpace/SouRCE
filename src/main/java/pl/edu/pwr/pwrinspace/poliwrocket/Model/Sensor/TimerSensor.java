package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;

import java.util.Timer;
import java.util.TimerTask;

@JsonTypeName("TimerSensor")
public class TimerSensor extends Sensor {

    @JsonProperty("abortTime")
    private int abortTime;

    private Timer timer;

    private TimerTask mainTask;

    private TimerTask controlTask;

    @JsonProperty("command")
    private Command<ProtobufCommand> command;

    @JsonProperty("controlTime")
    private int controlTime;

    private void resetTimer(long timeToAbort) {

        cancelTimer();
        timer = new Timer();

        controlTask = new TimerTask() {
            @Override
            public void run() {
                if (getValue() <= abortTime) {
                    sendVoidCommand();
                }
            }
        };

        mainTask = new TimerTask() {
            @Override
            public void run() {
                if (getValue() <= abortTime) {
                    sendVoidCommand();
                    timer.schedule(controlTask, controlTime);
                }
            }
        };

        long delay = Math.max(timeToAbort - abortTime, 100);
        timer.schedule(mainTask, delay);
    }

    private void sendVoidCommand() {
        SerialPortManager.getInstance().write(this.command);
    }

    @Override
    public void notifyObserver() {
        super.notifyObserver();
        resetTimer((long) this.getValue());
    }

    public void setAbortTime(int abortTime) {
        this.abortTime = abortTime;
    }

    public long getAbortTime() {
        return abortTime;
    }

    public void cancelTimer() {
        if (mainTask != null) {
            mainTask.cancel();
        }

        if (controlTask != null) {
            controlTask.cancel();
        }

        if (timer != null) {
            timer.cancel();
        }
    }
}
