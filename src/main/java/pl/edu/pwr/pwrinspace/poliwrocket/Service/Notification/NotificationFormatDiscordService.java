package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import pl.edu.pwr.pwrinspace.poliwrocket.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SensorRepository;

import java.awt.*;

public class NotificationFormatDiscordService extends NotificationFormatService {

    protected String channel = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    public NotificationFormatDiscordService(SensorRepository sensorRepository) {
        super(sensorRepository);
    }

    public Object getFormattedMessage(String messageKey) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (messageKey.equalsIgnoreCase("Data")) {
            embedBuilder.setTitle("Current data").setColor(Color.GRAY);
            sensorRepository.getAllBasicSensors().forEach((s, sensor) -> {
                embedBuilder.addField(sensor.getName(), sensor.getValue() + sensor.getUnit(), true);
            });
            return embedBuilder;

        } else if (messageKey.equalsIgnoreCase("Max")) {
            embedBuilder.setTitle("Max values").setColor(Color.RED);
            sensorRepository.getAllBasicSensors().forEach((s, sensor) -> {
                embedBuilder.addField(sensor.getName(), sensor.getMaxValue() + sensor.getUnit(), true);
            });
            return embedBuilder;

        } else if (messageKey.equalsIgnoreCase("Position")) {
            var latitude = sensorRepository.getGpsSensor().getPosition().get(GPSSensor.LATITUDE_KEY);
            var longitude = sensorRepository.getGpsSensor().getPosition().get(GPSSensor.LONGITUDE_KEY);

            embedBuilder.setTitle("Current position").setColor(Color.BLUE)
                    .addField("Latitude", latitude.toString(), true)
                    .addField("Longitude", longitude.toString(), true)
                    .addField("Map", googleMap + latitude.toString() +
                            "," + longitude.toString(), false);

            return  embedBuilder;
        } else if (messageKey.equalsIgnoreCase("Map")) {
            System.out.println(sensorRepository.getGpsSensor().getPosition());
            var latitude = sensorRepository.getGpsSensor().getPosition().get(GPSSensor.LATITUDE_KEY);
            var longitude = sensorRepository.getGpsSensor().getPosition().get(GPSSensor.LONGITUDE_KEY);
            return (googleMap + latitude.toString() + "," + longitude.toString());
        }

        embedBuilder.addField("Error", "Request not recognized", false);
        return embedBuilder;
    }
}
