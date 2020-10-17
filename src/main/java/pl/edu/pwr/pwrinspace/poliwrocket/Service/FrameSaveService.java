package pl.edu.pwr.pwrinspace.poliwrocket.Service;

import com.google.gson.Gson;
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
    private File flightDataFile = new File(Configuration.FLIGHT_DATA_PATH + Configuration.FLIGHT_DATA_FILE_NAME);

    public void saveFrameToFile(Frame frame) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(flightDataFile,true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                try {
                    output.write(frame.getContent() + Configuration.getInstance().FRAME_DELIMITER + frame.getTimeInstant().toString());
                    output.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            fileWriter.write(frame.getContent()+Configuration.getInstance().FRAME_DELIMITER+frame.getTimeInstant().toString());
//            fileWriter.write("dupa\n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try (FileWriter fileWriter = new FileWriter(flightDataFile)) {
//            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
//                bufferedWriter.write(frame.getContent()+Configuration.getInstance().FRAME_DELIMITER+frame.getTimeInstant().toString());
//                bufferedWriter.newLine();
//            }
////            fileWriter.append(frame.getContent()).append(Configuration.getInstance().FRAME_DELIMITER).append(frame.getTimeInstant().toString());
////            fileWriter.append(System.lineSeparator());
////            fileWriter.flush();
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
    }
}
