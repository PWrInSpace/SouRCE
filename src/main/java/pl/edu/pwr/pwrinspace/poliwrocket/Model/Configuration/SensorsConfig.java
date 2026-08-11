package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.google.gson.annotations.Expose;
import org.javatuples.KeyValue;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BaseController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BaseSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.*;

import java.util.*;

public class SensorsConfig extends BaseSaveModel {

    @Expose
    public SensorRepository sensorRepository = new SensorRepository();

    public SensorsConfig() {
        super(Configuration.CONFIG_PATH, "SensorConfig.json");
    }


    public SensorRepository processAndGetRepository(MessageParserEnum parserType,
                                                    Map<String, List<String>> framePattern,
                                                    InterpreterRepository interpreterRepository) {
        if (sensorRepository == null) {
            sensorRepository = new SensorRepository();
        }

        addSensorsToRepository(parserType, framePattern);
        validateFrameAndRepository(framePattern);

        setupSensorsAsListeners();

        setupSensorsInterpreters(interpreterRepository);

        return sensorRepository;
    }

    public void assignSensorsToControllers(Collection<BaseController> controllersList) {
        if (controllersList == null || sensorRepository == null) {
            return;
        }

        for (BaseController controller : controllersList) {
            String controllerName = controller.getControllerName();
            List<ISensor> assignedSensors = new ArrayList<>();

            if (sensorRepository.getGpsSensor() != null &&
                    sensorRepository.getGpsSensor().getDestinationControllerNames().contains(controllerName)) {
                sensorRepository.getGpsSensor().addListener(controller);
            }

            if (sensorRepository.getGyroSensor() != null &&
                    sensorRepository.getGyroSensor().getDestinationControllerNames().contains(controllerName)) {
                sensorRepository.getGyroSensor().addListener(controller);
            }

            sensorRepository.getAllBasicSensors().values().forEach(sensor -> {
                if (sensor.getDestinationControllerNames().contains(controllerName) &&
                        sensor.getDestination() != null && !sensor.getDestination().isEmpty()) {

                    sensor.addListener(controller);
                    assignedSensors.add(sensor);
                }
            });

            if (!assignedSensors.isEmpty() && controller instanceof BaseSensorController sensorController) {
                sensorController.injectSensorsModels(assignedSensors);
            }
        }
    }

    private void addSensorsToRepository(MessageParserEnum parserType, Map<String, List<String>> framePattern) {
        if (parserType == MessageParserEnum.PROTOBUF || framePattern.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGpsSensor().getLatitude().getName()))) {
            this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLatitude());
        }
        if (parserType == MessageParserEnum.PROTOBUF || framePattern.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGpsSensor().getLongitude().getName()))) {
            this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLongitude());
        }
        if (parserType == MessageParserEnum.PROTOBUF || framePattern.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGyroSensor().getAxis_x().getName()))) {
            this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_x());
        }
        if (parserType == MessageParserEnum.PROTOBUF || framePattern.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGyroSensor().getAxis_y().getName()))) {
            this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_y());
        }
        if (parserType == MessageParserEnum.PROTOBUF || framePattern.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGyroSensor().getAxis_z().getName()))) {
            this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_z());
        }

        var basicSensors = this.sensorRepository.getAllBasicSensors().values().toArray();

        Arrays.stream(basicSensors).filter(s -> s instanceof FillingLevelSensor).forEach(s -> {
            var sensor = (FillingLevelSensor) s;
            this.sensorRepository.addSensor(sensor.getHallSensor1());
            this.sensorRepository.addSensor(sensor.getHallSensor2());
            this.sensorRepository.addSensor(sensor.getHallSensor3());
            this.sensorRepository.addSensor(sensor.getHallSensor4());
            this.sensorRepository.addSensor(sensor.getHallSensor5());
        });

        Arrays.stream(basicSensors).filter(ISensorsWrapper.class::isInstance).forEach(s -> {
            for (Sensor innerSensor : ((ISensorsWrapper) s).getSensors()) {
                if (!innerSensor.getName().isEmpty())
                    this.sensorRepository.addSensor(innerSensor);
            }
        });

        Arrays.stream(basicSensors).filter(CompositeBitSensor.class::isInstance).forEach(s -> {
            List<KeyValue<String, Sensor>> sensorList = new LinkedList<>();
            var composite = ((CompositeBitSensor) s);
            this.sensorRepository.getAllBasicSensors().forEach((k, v) -> {
                if (Arrays.asList(composite.getSensorsKeys()).contains(k)) {
                    sensorList.add(new KeyValue<>(k, v));
                }
            });
            try {
                composite.injectSensors(sensorList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void validateFrameAndRepository(Map<String, List<String>> framePattern) {
        if (framePattern == null) return;
        framePattern.forEach((frameKey, pattern) -> {
            pattern.forEach(key -> {
                try {
                    sensorRepository.getSensorByName(key);
                } catch (NullPointerException e) {
                    sensorRepository.addSensor(new Sensor(key));
                }
            });
        });
    }

    private void setupSensorsAsListeners() {
        Arrays.stream(this.sensorRepository.getAllBasicSensors().values().toArray())
                .filter(IFieldsObserver.class::isInstance)
                .forEach(s -> ((IFieldsObserver) s).observeFields());

        if (this.sensorRepository.getGyroSensor() != null) {
            this.sensorRepository.getGyroSensor().observeFields();
        }
        if (this.sensorRepository.getGpsSensor() != null) {
            this.sensorRepository.getGpsSensor().observeFields();
        }
    }

    private void setupSensorsInterpreters(InterpreterRepository interpreterRepository) {
        if (interpreterRepository == null) return;
        this.sensorRepository.getAllBasicSensors().forEach((s, sensor) -> {
            if (sensor.getInterpreterKey() != null && !sensor.getInterpreterKey().isEmpty()) {
                sensor.setInterpreter(interpreterRepository.getInterpreter(sensor.getInterpreterKey()));
            }
        });
    }
}