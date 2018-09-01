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

    MyListener(JDA api) {
        this.commandParser = new CommandParser();
        this.fileManager = new FileManager();
        this.srTracker = new SRTracker();
        this.srSession = new SRSession();
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
        if (content.contains("!vote")) {
            String[] parameters = content.split("!vote ")[1].split(", ");
            if (parameters.length < 12) {
                String voteMessage = buildVoteMessage(parameters);
                event.getMessage().delete().queue();
                channel.sendMessage(voteMessage).queue((postedVote) -> {
                    for (int i=0; i < parameters.length; i++) {
                        postedVote.addReaction(integerToEmoji(i)).queue();
                    }
                });
            } else {
                channel.sendMessage("I currently cannot handle more than 10 voting options").queue();
            }
        }
    }

    private String buildVoteMessage(String[] parameters) {
        StringBuilder voteMessage = new StringBuilder();
        voteMessage.append("Please select the option you would like to vote for. \r");
        for (int i=0; i < parameters.length; i++) {
            voteMessage.append(":");
            voteMessage.append(integerToWord(i));
            voteMessage.append(":");
            voteMessage.append(" ");
            voteMessage.append(parameters[i]);
            voteMessage.append("\r");
        }
        return voteMessage.toString();
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

    private String integerToWord(Integer number) {
        String word = null;
        switch (number) {
            case 0:
                word = "zero";
                break;
            case 1:
                word = "one";
                break;
            case 2:
                word = "two";
                break;
            case 3:
                word = "three";
                break;
            case 4:
                word = "four";
                break;
            case 5:
                word = "five";
                break;
            case 6:
                word = "six";
                break;
            case 7:
                word = "seven";
                break;
            case 8:
                word = "eight";
                break;
            case 9:
                word = "nine";
                break;
            default:
                break;
        }
        return word;
    }

    private String integerToEmoji(Integer number) {
        String word = null;
        switch (number) {
            case 0:
                word = "0⃣";
                break;
            case 1:
                word = "1⃣";
                break;
            case 2:
                word = "2⃣";
                break;
            case 3:
                word = "3⃣";
                break;
            case 4:
                word = "4⃣";
                break;
            case 5:
                word = "5⃣";
                break;
            case 6:
                word = "6⃣";
                break;
            case 7:
                word = "7⃣";
                break;
            case 8:
                word = "8⃣";
                break;
            case 9:
                word = "9⃣";
                break;
            default:
                break;
        }
        return word;
    }
}