package pl.edu.pwr.pwrinspace.poliwrocket.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

import java.util.Arrays;

public class ConfigurationProvider {

    // todo zdecydować czy poprawić tu singleton
    private Configuration configuration;
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);

    private final ModelAsYamlService modelAsYamlService = new ModelAsYamlService();

    private ConfigurationProvider() throws Exception {
        if (ConfigurationProvider.Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
        loadConfig();
    }

    public void loadConfig() {
        //Read config file
        try {
//            configuration = new Configuration(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), false));
        } catch (UnsupportedOperationException e) {
            logger.error("Wrong mapping in controller");
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            logger.error("Bad config file, overwritten by default and loaded");
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(e.toString());
            modelAsYamlService.persistOldFile(new ConfigurationSaveModel());
//            modelAsYamlService.saveToFile(ConfigurationSaveModel.defaultConfiguration(), false);
//            Configuration.getInstance().setupConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), false));
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
