package pl.edu.pwr.pwrinspace.poliwrocket.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsJsonSaveService;

import java.util.Arrays;

public class ConfigurationProvider {
    
    private Configuration configuration;
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);

    private final ModelAsJsonSaveService modelAsJsonSaveService = new ModelAsJsonSaveService();

    private ConfigurationProvider() throws Exception {
        if (ConfigurationProvider.Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
        loadConfig();
    }

    public void loadConfig() {
        //Read config file
        try {
            //configuration = new Configuration(modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));
        } catch (UnsupportedOperationException e) {
            logger.error("Wrong mapping in controller");
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            logger.error("Bad config file, overwritten by default and loaded");
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(e.toString());
            modelAsJsonSaveService.persistOldFile(new ConfigurationSaveModel());
            // modelAsJsonSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());
            //Configuration.getInstance().setupConfigInstance((ConfigurationSaveModel) modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));
            throw e;
        }
        //--------------
    }

    private static class Holder {
        private static final ConfigurationProvider INSTANCE;

        static {
            try {
                INSTANCE = new ConfigurationProvider();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
