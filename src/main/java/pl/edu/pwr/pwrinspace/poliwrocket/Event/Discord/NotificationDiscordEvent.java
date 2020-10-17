package pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import pl.edu.pwr.pwrinspace.poliwrocket.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatDiscordService;

public class NotificationDiscordEvent extends NotificationEvent {

    protected String channel = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    public NotificationDiscordEvent(NotificationFormatDiscordService notificationFormatDiscordService) {
        this.notification = notificationFormatDiscordService;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        if(!event.getAuthor().isBot()) {
            String messageReceived = event.getMessage().getContentRaw();
            var message = notification.getFormattedMessage(messageReceived);
            if(message instanceof EmbedBuilder){
                this.getChannel(event).sendMessage(((EmbedBuilder) message).build()).queue();
            } else if (message instanceof String){
                this.getChannel(event).sendMessage(((String) message)).queue();
            }
        }
    }

    protected TextChannel getChannel(@NotNull GuildMessageReceivedEvent event) {

        if (!channel.equals("")) {
            var channels = event.getGuild().getTextChannelsByName(channel, true);
            if (!channels.isEmpty()) {
                return channels.get(0);
            }
        }
        return event.getChannel();
    }
}
