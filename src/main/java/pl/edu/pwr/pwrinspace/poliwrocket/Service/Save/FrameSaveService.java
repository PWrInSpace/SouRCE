package pl.edu.pwr.pwrinspace.poliwrocket.Service.Save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.Frame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class FrameSaveService {

    private static final Logger logger = LoggerFactory.getLogger(FrameSaveService.class);
    private final HashMap<String,File> flightDataFiles;
    private final File logFile = new File(Configuration.FLIGHT_DATA_PATH + "Flight_log_" + Configuration.startUpTime.getEpochSecond()  + ".txt");

    public FrameSaveService(Set<String> framesKeys) {
        flightDataFiles = new HashMap<>();
        File directory = new File(Configuration.FLIGHT_DATA_PATH);
        if(!directory.exists()){
            directory.mkdirs();
        }
        framesKeys.forEach(frameKey -> flightDataFiles.putIfAbsent(frameKey,new File(Configuration.FLIGHT_DATA_PATH + Configuration.getFlightDataFileName(frameKey))));
    }

    public void saveFrameToFile(Frame frame) {
        try {
            File file = flightDataFiles.get(frame.getKey());
            if(file == null)
                file = logFile;

            FileWriter fileWriter = new FileWriter(file, true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                output.write(frame.getTimeInstant().toString() + Configuration.getInstance().FRAME_DELIMITER + frame.getFormattedContent());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void writeFileHeader(String key, Iterable<String> headers) {
        StringBuilder header = new StringBuilder();
        header.append("Time").append(Configuration.getInstance().FRAME_DELIMITER).append(key);
        headers.forEach(h -> header.append(Configuration.getInstance().FRAME_DELIMITER).append(h));
        try {
            File file = flightDataFiles.get(key);
            if(file == null){
                logger.error("File with key {} not found.", key);
                return;
            }

            FileWriter fileWriter = new FileWriter(file, true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                output.write(header.toString());
                output.newLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
