import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;
import java.util.HashMap;

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

        if (content.toLowerCase().contains("wow")) {
            File jerryPic = fileManager.getFile("wow.jpg");
            if (jerryPic != null) {
                channel.sendFile(jerryPic, "wow.jpg").queue();
            }
        }
        if (content.toLowerCase().contains("opinion")) {
            File jerryPic = fileManager.getFile("myOpinion.png");
            if (jerryPic != null) {
                channel.sendFile(jerryPic, "myOpinion.png").queue();
            }
        }
        if (content.toLowerCase().contains("women") || content.toLowerCase().contains("girl") || content.toLowerCase().contains("gorl")) {
            MessageBuilder thotBuilder = new MessageBuilder();
            thotBuilder.setTTS(true);
            thotBuilder.append("If she breathes, she's a thot!");
            channel.sendMessage(thotBuilder.build()).queue();
        }

        if (event.getAuthor().getId().equals("230347831335059457")) {
            //Save the SR history to file
            if (content.contains("!say")) {
                String[] commandString = content.split("!say");
                String whatToSay = commandString[1];
                event.getGuild().getTextChannelById("237059614384848896").sendMessage(whatToSay).queue();
                System.out.printf("You told me to say: %s", whatToSay);
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