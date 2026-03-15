package pl.edu.pwr.pwrinspace.poliwrocket.Service.Save;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BaseController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorDestination;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class ModelAsYamlService {
    private static final Logger logger = LoggerFactory.getLogger(ModelAsYamlService.class);
    private final ObjectMapper mapper = YAMLMapper.builder().enable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, YAMLGenerator.Feature.INDENT_ARRAYS).disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).addModule(new ParameterNamesModule()).build();

    public ModelAsYamlService() {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

        Reflections sensorReflections = new Reflections("pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor");
        Reflections commandReflections = new Reflections("pl.edu.pwr.pwrinspace.poliwrocket.Model.Command");
        Set<Class<?>> sensorSubtypes = sensorReflections.getTypesAnnotatedWith(JsonTypeName.class);
        Set<Class<?>> commandSubtypes = commandReflections.getTypesAnnotatedWith(JsonTypeName.class);
        for (Class<?> sensorSubtype : sensorSubtypes) {
            JsonTypeName annotation = sensorSubtype.getAnnotation(JsonTypeName.class);
            if (annotation != null) {
                String sensorTypeName = annotation.value();
                mapper.registerSubtypes(new NamedType(sensorSubtype, sensorTypeName));
                mapper.registerSubtypes(new NamedType(sensorSubtype, sensorTypeName.replace("!", "")));
            }
        }
        for (Class<?> commandSubtype : commandSubtypes) {
            JsonTypeName annotation = commandSubtype.getAnnotation(JsonTypeName.class);
            if (annotation != null) {
                String commandTypeName = annotation.value();
                mapper.registerSubtypes(new NamedType(commandSubtype, commandTypeName));
                mapper.registerSubtypes(new NamedType(commandSubtype, commandTypeName.replace("!", "")));
            }
        }
    }

    // temp:
    // true - zapisuje konfiguracje z do temp
    // false - zapisuje konfiguracje do config
    public void saveToFile(BaseSaveModel configuration, boolean temp) {
        try {
            String configContent = mapper.writeValueAsString(configuration);

            Path configPath = Paths.get(configuration.getPath() + configuration.getFileName());
            if (temp) configPath = Paths.get(configuration.getPath(), configuration.getTempFileName());

            File configFile = new File(configPath.toString());

            FileWriter configWriter = new FileWriter(configFile);
            configWriter.write(configContent);
            configWriter.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void persistOldFile(BaseSaveModel config) {
        File dir = new File(config.getPath());
        if(!dir.exists()) {
            dir.mkdir();
        }
        File configFile = new File(config.getPath() + config.getFileName());
        File copy = new File(config.getPath()  + config.getPersistPrefix() + config.getFileName());
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

    // temp:
    // true — czyta plik z temp
    // false — ładuje plik config, a następnie nadpisuje nim temp i czyta plik temp
    public <T extends BaseSaveModel> T readFromFile(T config, boolean temp) throws Exception {
        if (!temp) {
            Path configPath = Paths.get(config.getPath() + config.getFileName());
            Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
            Files.copy(configPath, configTempPath, StandardCopyOption.REPLACE_EXISTING);
        }

        File configFile = new File(config.getPath() + config.getTempFileName());

        try {
            config = (T) mapper.readValue(configFile, config.getClass()); // todo tym trzeba się zająć
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return config;
    }

    public <T extends BaseSaveModel> void removeTempConfig(T config) {
        File configTempFile = new File(config.getPath() + config.getTempFileName());
        if (configTempFile.delete()) System.out.println("Removed temp file");
        else System.out.println("Temp file not found");
    }

    public void addCommandToFile(ConfigurationSaveModel config, Command<?> command) {
        Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
        try {
            Configuration.getInstance().commandsList.add(command);
            config = ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance());
            mapper.writeValue(new File(configTempPath.toString()), config);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void addCommandToController(ConfigurationSaveModel config, BaseController controller, Command<?> command) {
        Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
        try {
            command.addDestinationControllerName(controller.getControllerName());
            config = ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance());
            mapper.writeValue(new File(configTempPath.toString()), config);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public void addSensorToController(ConfigurationSaveModel config, Sensor sensor, SensorDestination sensorDestination) {
        Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
        try {
            sensor.addSensorDestination(sensorDestination);
            config = ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance());
            mapper.writeValue(new File(configTempPath.toString()), config);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void overrideConfig(BaseSaveModel config) {
        Path configPath = Paths.get(config.getPath() + config.getFileName());
        Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
        try {
            Files.copy(configTempPath, configPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
        }
    }
}