package pl.edu.pwr.pwrinspace.poliwrocket;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsJsonSaveService;

public class ConfigProtobufGenerator {

    public static void main(String[] args) {

        try {
            ModelAsJsonSaveService modelAsJsonSaveService = new ModelAsJsonSaveService();

            if(args.length > 0 && args[0].contains("/config/")) {
                Configuration.getInstance().setConfigPath(args[0]);
            }

            Configuration.getInstance().setupConfigInstance(modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));

            modelAsJsonSaveService.saveToFile(ConfigurationSaveModel.protobufBasedConfiguration(Configuration.getInstance()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static ConfigurationSaveModel GetProtobufSaveModel() {
        ConfigurationSaveModel model = new ConfigurationSaveModel();

        return model;
    }
}
