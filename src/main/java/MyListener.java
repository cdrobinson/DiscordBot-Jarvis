import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.HashMap;


public class MyListener extends ListenerAdapter {

    private FileManager fileManager;
    private SRTracker srTracker;
    private SRSession srSession;
    private CommandParser commandParser;

    MyListener() {
        this.commandParser = new CommandParser();
        this.fileManager = new FileManager();
        this.srTracker = new SRTracker();
        this.srSession = new SRSession();
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
        commandParser.parseCommand(content, channel, event);

        if (channel.getName().equals("sr-tracking")) {

            if(content.equalsIgnoreCase("!StartSession")) {
                String userID = event.getAuthor().getId();
                Integer startingSR = srTracker.getPlayerSR(userID);
                if (startingSR != null) {
                    channel.sendMessage("Starting a session for " + event.getAuthor().getAsMention() + "with a starting SR of " + startingSR).queue();
                    srSession.startSession(userID, startingSR);
                    fileManager.writeToTextFile(srSession.getHistory().toString(), "SRSessions.txt");
                } else {
                    channel.sendMessage("Please enter a starting SR first.").queue();
                }
            }
            if(content.equalsIgnoreCase("!CurrentSession")) {
                String userID = event.getAuthor().getId();
                Integer startingSR = srSession.getStartingSR(userID);
                Integer currentSR = srTracker.getPlayerSR(userID);
                if (startingSR != null) {
                    channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + startingSR +
                            "\rCurrent SR: " + currentSR + "\rDifference: " + (currentSR - startingSR) + "\r------------------------").queue();
                } else {
                    channel.sendMessage("You don't have a session going right now. Type \"!startSession\" to begin one.").queue();
                }
            }
            if(content.equalsIgnoreCase("!EndSession")) {
                String userID = event.getAuthor().getId();
                Integer endingSR = srTracker.getPlayerSR(userID);
                Integer startingSR = srSession.endSession(userID);
                if (startingSR != null) {
                    channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + startingSR +
                            "\rEnding SR: " + endingSR + "\rDifference: " + (endingSR - startingSR) + "\r------------------------").queue();
                    fileManager.writeToTextFile(srSession.getHistory().toString(), "SRSessions.txt");
                } else {
                    channel.sendMessage("You don't have a session going right now. Type \"!startSession\" to begin one.").queue();
                }
            }

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
            if (content.equals("!savesr")) {
                channel.sendMessage("Saving SR records to file.").queue();
                fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
            }
            //Load the SR history from file
            if (content.equals("!loadsr")) {
                channel.sendMessage("Loading SR records from file....").queue();
                String srHistoryString = fileManager.readFromTextFile("SRHistory.txt");
                if (srHistoryString != null) {
                    srTracker.loadSRHistory(parseStorageFile(srHistoryString));
                    channel.sendMessage("SR records have been loaded.").queue();
                } else {
                    channel.sendMessage("The records file could not be located.").queue();
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

    private String buildSRReport(Integer newSR, Integer oldSR, Integer difference, String differencePrefix) {
        return "\r------------------------\rNew SR: " + newSR + "\rPrevious SR: " + oldSR + "\rDifference: "+ differencePrefix + difference + "\r------------------------";
    }

    private HashMap<String, Integer> parseStorageFile(String fileContent) {
        if (fileContent != null) {
            HashMap<String, Integer> parsedContent = new HashMap<>();
            if(fileContent.length() > 2) {
                fileContent = fileContent.substring(1, fileContent.length() - 1);
                String[] contentAsList = fileContent.split(", ");

                for (String listEntry : contentAsList) {
                    String[] userInfo = listEntry.split("=");
                    parsedContent.put(userInfo[0], Integer.valueOf(userInfo[1]));
                }
            }
            return parsedContent;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}