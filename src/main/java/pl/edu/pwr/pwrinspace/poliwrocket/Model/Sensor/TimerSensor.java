package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;

import java.util.*;

public class TimerSensor extends Sensor {

    private static final Logger logger = LoggerFactory.getLogger(TimerSensor.class);

    @Expose
    private int abortTime;

    private Timer timer;

    private TimerTask mainTask;

    private TimerTask controlTask;

    @Expose
    private Command<ProtobufCommand> command;

    @Expose
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
        logger.info("timer destination: " + getDestination());
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
