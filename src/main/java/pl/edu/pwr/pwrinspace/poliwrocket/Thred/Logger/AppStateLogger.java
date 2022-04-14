package pl.edu.pwr.pwrinspace.poliwrocket.Thred.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.ISerialPortManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppStateLogger implements InvalidationListener {

    private static final Logger logger = LoggerFactory.getLogger(AppStateLogger.class);

    private final Timer timerRunner = new Timer();
    protected ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final File logFile;

    private AppStateLogger() {
        if (AppStateLogger.Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
        File directory = new File(Configuration.FLIGHT_DATA_PATH);
        if(!directory.exists()){
            directory.mkdirs();
        }
        logFile =  new File(Configuration.FLIGHT_DATA_PATH + "AppLog" + Configuration.getFlightDataFileName("AppLog"));
    }

    private static class Holder {
        private static final AppStateLogger INSTANCE = new AppStateLogger();
    }

    public static AppStateLogger getInstance() {
        return AppStateLogger.Holder.INSTANCE;
    }

    public void start() {
        writeLineToLogFile(getFileHeader());
        timerRunner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (logFile) {
                    writeLineToLogFile(getSensorsLog() + Configuration.getInstance().FRAME_DELIMITER + "-");
                }
            }
        },new Date(new Date().getTime() + 2000),2000);
    }

    private String getSensorsLog() {
        StringBuilder log = new StringBuilder();
        log.append(Instant.now().toString());
        Configuration.getInstance().sensorRepository.getAllBasicSensors()
                .values().forEach(sensor -> log.append(Configuration.getInstance().FRAME_DELIMITER).append(sensor.getValue()));

        return log.toString();
    }

    private String getFileHeader() {
        StringBuilder header = new StringBuilder();
        header.append("Time");
        Configuration.getInstance().sensorRepository.getSensorsKeys()
                .forEach(h -> header.append(Configuration.getInstance().FRAME_DELIMITER).append(h));
        header.append(Configuration.getInstance().FRAME_DELIMITER).append("Action");
        return  header.toString();
    }

    private void writeLineToLogFile(String line) {
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                output.write(line);
                output.newLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISerialPortManager) {
            var lastAction = "\"" + ((ISerialPortManager) observable).getLastSend() + "\"";
            executorService.submit(() -> {
                synchronized (logFile) {
                    writeLineToLogFile(getSensorsLog() + Configuration.getInstance().FRAME_DELIMITER + lastAction);
                }
            });
        }
    }
}
