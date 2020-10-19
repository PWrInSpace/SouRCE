package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import javafx.beans.InvalidationListener;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordNotification implements INotification {

    private static final Logger logger = LoggerFactory.getLogger(DiscordNotification.class);

    private List<InvalidationListener> observers = new ArrayList<>();

    private JDA jda;

    protected String channel = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    protected NotificationFormatDiscordService notificationFormatDiscordService;

    public DiscordNotification(NotificationFormatDiscordService notificationFormatDiscordService) {
        this.notificationFormatDiscordService = notificationFormatDiscordService;
    }

    public synchronized void setupConnection() {
        if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
            try {
                jda = JDABuilder.createDefault(Configuration.getInstance().DISCORD_TOKEN).build();
                jda.addEventListener(new NotificationDiscordEvent(notificationFormatDiscordService));
            } catch (Exception e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }

    }

    public synchronized void setupCustom() {
        if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
            JDABuilder builder = JDABuilder.createDefault(Configuration.getInstance().DISCORD_TOKEN);

            // Disable parts of the cache
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);

            // Set activity (like "playing Something")
            builder.setActivity(Activity.playing("Flying"));

            try {
                jda = builder.build();
                jda.addEventListener(new NotificationDiscordEvent(notificationFormatDiscordService));
            } catch (Exception e) {
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
    public synchronized void setup() {
        setupCustom();
        if(jda != null){
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }

        notifyObserver();
    }

    @Override
    public boolean isConnected() {
        return !Configuration.getInstance().DISCORD_TOKEN.equals("") && jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }}
