package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord.NotificationDiscordEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatDiscordService;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class DiscordNotification implements INotification {

    private static final Logger logger = LoggerFactory.getLogger(DiscordNotification.class);

    private JDA jda;

    protected String channel = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    protected NotificationFormatDiscordService notificationFormatDiscordService;

    public DiscordNotification(NotificationFormatDiscordService notificationFormatDiscordService) {
        this.notificationFormatDiscordService = notificationFormatDiscordService;
    }

    public void setupConnection() {
        if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
            try {
                jda = JDABuilder.createDefault(Configuration.getInstance().DISCORD_TOKEN).build();
                jda.addEventListener(new NotificationDiscordEvent(notificationFormatDiscordService));
            } catch (LoginException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }

    }

    public void setupCustom() {
        if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
            JDABuilder builder = JDABuilder.createDefault(Configuration.getInstance().DISCORD_TOKEN);

            // Disable parts of the cache
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);

            // Set activity (like "playing Something")
            builder.setActivity(Activity.playing("Flying"));

            try {
                jda = builder.build();
                jda.addEventListener(new NotificationDiscordEvent(notificationFormatDiscordService));
            } catch (LoginException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    protected TextChannel getChannel() {

        if (!channel.equals("")) {
            var channels = jda.getTextChannelsByName(channel, true);
            if (!channels.isEmpty()) {
                return channels.get(0);
            }
            channels = jda.getTextChannels();
            if (!channels.isEmpty()) {
                return channels.get(0);
            }
        }
        return null;
    }

    @Override
    public void sendNotification(String messageKey) {
        var message = notificationFormatDiscordService.getFormattedMessage(messageKey);
        if(message instanceof EmbedBuilder){
            getChannel().sendMessage(((EmbedBuilder) message).build()).queue();
        } else if (message instanceof String){
            getChannel().sendMessage(((String) message)).queue();
        }
    }

    @Override
    public void setup() {
        setupCustom();
    }

}
