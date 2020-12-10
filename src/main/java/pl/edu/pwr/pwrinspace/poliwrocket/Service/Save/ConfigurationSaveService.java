package pl.edu.pwr.pwrinspace.poliwrocket.Service.Save;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;

import java.io.*;

public class ConfigurationSaveService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationSaveService.class);

    public void saveToFile(ConfigurationSaveModel configuration) {
        String configContent = new Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(configuration);
        File configFile = new File(Configuration.CONFIG_PATH + Configuration.CONFIG_FILE_NAME);

        try (FileWriter configWriter = new FileWriter(configFile)) {
            configWriter.write(configContent);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void persistOldConfig() {
        File configFile = new File(Configuration.CONFIG_PATH + Configuration.CONFIG_FILE_NAME);
        File copy = new File(Configuration.CONFIG_PATH + "BAD_" + Configuration.CONFIG_FILE_NAME);
        try (FileInputStream fis = new FileInputStream(configFile);
           FileOutputStream fos = new FileOutputStream(copy)) {
            int len;
            byte[] buffer = new byte[4096];
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public ConfigurationSaveModel readFromFile() throws Exception {
        File configFile = new File(Configuration.CONFIG_PATH + Configuration.CONFIG_FILE_NAME);
        ConfigurationSaveModel config = null;

        try (JsonReader reader = new JsonReader(new FileReader(configFile))) {
            config = new Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, ConfigurationSaveModel.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return config;
    }
}
