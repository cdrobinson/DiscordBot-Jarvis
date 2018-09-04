import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.HashMap;
import java.util.List;

public class MyListener extends ListenerAdapter {

    private FileManager fileManager;
    private SRTracker srTracker;
    private SRSession srSession;
    private CommandParser commandParser;
    private JDA jdaApi;
    private TwitterManager twitterManager;

    MyListener(JDA api) {
        this.commandParser = new CommandParser();
        this.fileManager = new FileManager();
        this.srTracker = new SRTracker();
        this.srSession = new SRSession();
        this.twitterManager = new TwitterManager();
        this.jdaApi = api;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    channel.getName(), event.getAuthor(), message.getContentRaw());
        }
        // We don't want to respond to other bot accounts, including ourselves
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        commandParser.parseCommand(jdaApi, content, event, srSession, srTracker);

        if (channel.getName().equals("sr-tracking")) {
            String[] input = content.split(" ");
            if (input.length == 1 && isInteger(content)) {
                Integer updatedSR = Integer.parseInt(content);
                HashMap<String, Integer> srHistory = srTracker.updateSR(event.getAuthor().getId(), updatedSR);
                String authorAsMention = event.getAuthor().getAsMention();
                SRReporter srReporter = new SRReporter();
                channel.sendMessage(srReporter.build(authorAsMention, "SR", "New", srHistory.get("New SR"),
                        "Previous", srHistory.get("Old SR"), srHistory.get("Difference"))).queue();
                if (srHistory.get("Difference") > 0) {
                    event.getMessage().addReaction("\uD83D\uDC4D").queue();
                    event.getMessage().addReaction("\uD83D\uDC4C").queue();
                } else if (srHistory.get("Difference") < 0) {
                    event.getMessage().addReaction("\uD83D\uDC4E").queue();
                    event.getMessage().addReaction("\uD83D\uDE22").queue();
                } else {
                    event.getMessage().addReaction("\uD83D\uDE10").queue();
                    event.getMessage().addReaction("\uD83E\uDD37").queue();
                }
                fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
            }
        }
        if (event.getAuthor().getId().equals("230347831335059457")) {
            //Save the SR history to file
            if (content.contains("!say")) {
                String[] commandString = content.split("!say");
                String whatToSay = commandString[1];
                event.getGuild().getTextChannelById("237059614384848896").sendMessage(whatToSay).queue();
                System.out.printf("You told me to say: %s", whatToSay);
            }
            if (content.contains("!saveSR")) {
                fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
                channel.sendMessage("SR Tracker has been saved to the server.").queue();
            }
            if (content.contains("!loadSR")) {
                srTracker.loadSRHistory(fileManager.parseStorageFile(fileManager.readFromTextFile("SRHistory.txt")));
                channel.sendMessage("SR Tracker has been loaded from the server.").queue();
            }
        }
        if (content.contains("!tweet")) {
            String[] tweetContents = content.split("!tweet");
            String tweetLink = this.twitterManager.createTweet(tweetContents[1]);
            if (tweetLink == null) {
                channel.sendMessage("There was an error posting the tweet").queue();
            } else {
                channel.sendMessage("The tweet was posted successfully. Here is the link: \r" + tweetLink).queue();
            }
        }
    }


    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event) {

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getGuild().getId().equals("237059614384848896")) return;
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
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        event.getReaction().removeReaction().queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals("237059614384848896")) return;
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

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}