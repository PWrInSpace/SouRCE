package pl.edu.pwr.pwrinspace.poliwrocket.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Frame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FrameSaveService {

    private static final Logger logger = LoggerFactory.getLogger(FrameSaveService.class);
    private final File flightDataFile = new File(Configuration.FLIGHT_DATA_PATH + Configuration.FLIGHT_DATA_FILE_NAME);

    public void saveFrameToFile(Frame frame) {
        try {
            FileWriter fileWriter = new FileWriter(flightDataFile, true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                output.write(frame.getContent() + Configuration.getInstance().FRAME_DELIMITER + frame.getTimeInstant().toString());
                output.newLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
