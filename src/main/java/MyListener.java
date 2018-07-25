import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
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
        commandParser.parseCommand(content, event, srSession, srTracker, jdaApi);

        if (channel.getName().equals("sr-tracking")) {
            String[] input = content.split(" ");
            if (input.length == 1 && isInteger(content)) {
                Integer updatedSR = Integer.parseInt(content);
                HashMap<String, Integer> srHistory = srTracker.updateSR(event.getAuthor().getId(), updatedSR);
                String authorAsMention = event.getAuthor().getAsMention();
                if (srHistory.get("Difference") > 0) {
                    event.getMessage().addReaction("\uD83D\uDC4D").queue();
                    event.getMessage().addReaction("\uD83D\uDC4C").queue();
                    channel.sendMessage(authorAsMention + buildSRReport(srHistory.get("New SR"), srHistory.get("Old SR"), srHistory.get("Difference"), "+")).queue();
                    fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
                } else if (srHistory.get("Difference") < 0) {
                    event.getMessage().addReaction("\uD83D\uDC4E").queue();
                    event.getMessage().addReaction("\uD83D\uDE22").queue();
                    channel.sendMessage(authorAsMention + buildSRReport(srHistory.get("New SR"), srHistory.get("Old SR"), srHistory.get("Difference"), "")).queue();
                    fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
                } else {
                    event.getMessage().addReaction("\uD83D\uDE10").queue();
                    event.getMessage().addReaction("\uD83E\uDD37").queue();
                    channel.sendMessage(authorAsMention + buildSRReport(srHistory.get("New SR"), srHistory.get("Old SR"), srHistory.get("Difference"), "")).queue();
                    fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
                }
            }
        }

        if (event.getAuthor().getId().equals("230347831335059457")) {
            //Save the SR history to file
            //TODO create command that allows me to message people or channels as the bot
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

    private String buildSRReport(Integer newSR, Integer oldSR, Integer difference, String differencePrefix) {
        return "\r------------------------\rNew SR: " + newSR + "\rPrevious SR: " + oldSR + "\rDifference: "+ differencePrefix + difference + "\r------------------------";
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}