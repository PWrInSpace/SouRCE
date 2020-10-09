package pl.edu.pwr.pwrinspace.poliwrocket.Service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.ConfigurationSaveModel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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

    public ConfigurationSaveModel readFromFile() {
        File configFile = new File(Configuration.CONFIG_PATH + Configuration.CONFIG_FILE_NAME);
        ConfigurationSaveModel config = null;

        try (JsonReader reader = new JsonReader(new FileReader(configFile))) {
            config = new Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, ConfigurationSaveModel.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return config;
    }
}
