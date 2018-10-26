import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class MainListener extends ListenerAdapter {

    private SRTracker srTracker;
    private SRSession srSession;
    private CommandParser commandParser;
    private JDA jdaApi;
    private AdminCommands adminCommands;
    private ConfigManager cm;

    MainListener(JDA api) {
        this.cm = new ConfigManager();
        this.jdaApi = api;
        this.adminCommands = new AdminCommands();
        this.commandParser = new CommandParser();
        this.srTracker = new SRTracker(jdaApi);
        this.srSession = new SRSession();
        Thread thread = new Thread(new TwitterManager(jdaApi.getGuildById(cm.getProperty("guildID")).getTextChannelsByName(cm.getProperty("twitterOutputChannelName"), true).get(0)));
        thread.start();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getProperty("guildID"))) return;
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] contentList = content.split(" ");
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    channel.getName(), event.getAuthor(), message.getContentRaw());
        }

        if (channel.getName().equals(new ConfigManager().getProperty("srTrackingChannelName"))) {
            srTracker.parseCommand(contentList, event, srSession);
        }

        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            adminCommands.parseCommand(jdaApi, content, event);
        }
        commandParser.parseCommand(jdaApi, content, event, srSession, srTracker);
    }


    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event) {

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getGuild().getId().equals(cm.getProperty("guildID"))) return;
        if (event.getReactionEmote().isEmote()) {
            Emote reactedEmote = event.getReactionEmote().getEmote();
            event.getChannel().getMessageById(event.getMessageId()).queue((message) -> message.addReaction(reactedEmote).queue());
        } else {
            String reactedEmoji = event.getReactionEmote().getName();
            event.getChannel().getMessageById(event.getMessageId()).queue((message) -> message.addReaction(reactedEmoji).queue());
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (!event.getGuild().getId().equals(cm.getProperty("guildID"))) return;
        event.getReaction().removeReaction().queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals(cm.getProperty("guildID"))) return;
        if (event.getUser().isBot()) return;
        //event.getAuthor().openPrivateChannel(.queue((userPM) -> userPM.sendMessage("Message").queue());
        String welcomeMessage = "Welcome to the Frontline! Here are a list of my commands:\r" + HelpMessageBuilder.getHelpMessage();
        event.getMember().getUser().openPrivateChannel().queue((userPM) -> userPM.sendMessage(welcomeMessage).queue());
        List<Guild> mutualGuilds = event.getUser().getMutualGuilds();
        for (Guild guild : mutualGuilds) {
            if (guild.getId().equals("260565533575872512")) {
                List<Role> userRoles = guild.getMemberById(event.getUser().getId()).getRoles();
                for (Role role : userRoles) {
                    if (role.getId().equals("443151138062073866")) {
                        event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("451495511724130305")).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}