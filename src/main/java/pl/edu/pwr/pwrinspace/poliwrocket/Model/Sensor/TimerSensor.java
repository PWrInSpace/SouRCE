package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content.ProtobufContent;

import java.util.*;

public class TimerSensor extends Sensor implements InvalidationListener {

    private long abortTime = 300000;
    @Expose
    Sensor timerSensor = new Sensor("Timer");
    private Timer timer;
    private TimerTask timerTask;
    private TimerTask timerTask1;
    private SerialPortManager serialPortManager = SerialPortManager.getInstance();

    public TimerSensor(String name) {
        super(name);
        this.timer = new Timer();
    }

    public TimerSensor(Sensor sensor) {
        super(sensor);
        this.timer = new Timer();
    }

    private void resetTimer(long timeToAbort) {
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                if (timerSensor.getValue() <= 300000) {
                    sendVoidCommand();
                    timer.schedule(timerTask1, 5000);
                }
            }
        };

        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendVoidCommand();
                timer.schedule(timerTask1, 5000);
            }
        };

        long delay = timeToAbort - abortTime;

        timer.schedule(timerTask, delay);
    }

    private void sendVoidCommand() {
        ProtobufContent content = new ProtobufContent("0XFF", "ALL", "MCB");
        ProtobufCommand command = new ProtobufCommand("voidButton", content, "Void", List.of("StartControl"));

        serialPortManager.write(command);
    }

    @Override
    public void invalidated(Observable observable) {

        if (observable instanceof Sensor) {
            Sensor sensor = (Sensor) observable;
            if ("Timer".equals(sensor.getName())) {
                resetTimer((long) sensor.getValue());
            }
        }
    }

    public void setAbortTime(long abortTime) {
        this.abortTime = abortTime;
    }

    public long getAbortTime() {
        return abortTime;
    }

    public void cancelTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }
}
