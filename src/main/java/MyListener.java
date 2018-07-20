import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.*;
import java.util.HashMap;


public class MyListener extends ListenerAdapter {

    private SRTracker srTracker;
    private SRSession srSession;

    MyListener() {
        this.srTracker = new SRTracker();
        this.srSession = new SRSession();
        srTracker.loadSRHistory(parseStorageFile(readFromFile("SRHistory.txt")));
        srSession.loadSessions(parseStorageFile(readFromFile("SRSessions.txt")));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourselves
        Message message = event.getMessage();
        String content = message.getContentRaw();
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    channel.getName(), event.getAuthor(), message.getContentDisplay());
        }
        if (content.equalsIgnoreCase("!ping")) {
            channel.sendMessage("Pong!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }

        if (content.equalsIgnoreCase("!help")) {
            channel.sendMessage("!ping: Check that I'm working \r PM me your BSU email to register as a BSU student (ie ccardinal@bsu.edu) \r Contact @BattlemanMK2 for further help").queue();
        }

        if (content.equalsIgnoreCase("!scotland")) {
            MessageChannel musicChannel = event.getGuild().getTextChannelById("468644546423554068");
            musicChannel.sendMessage("SCOTLAND FOREVER!!!").queue();
        }

        if (channel.getName().equals("sr-tracking")) {
            if(content.equalsIgnoreCase("!StartSession")) {
                String userID = event.getAuthor().getId();
                Integer startingSR = srTracker.getPlayerSR(userID);
                if (startingSR != null) {
                    channel.sendMessage("Starting a session for " + event.getAuthor().getAsMention() + "with a starting SR of " + startingSR).queue();
                    srSession.startSession(userID, startingSR);
                    writeToFile(srSession.getHistory().toString(), "SRSessions.txt");
                } else {
                    channel.sendMessage("Please enter a starting SR first.").queue();
                }
            }
            if(content.equalsIgnoreCase("!CurrentSession")) {
                String userID = event.getAuthor().getId();
                Integer startingSR = srSession.getStartingSR(userID);
                Integer currentSR = srTracker.getPlayerSR(userID);
                channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + startingSR +
                        "\rCurrent SR: " + currentSR + "\rDifference: " + (currentSR - startingSR) + "\r------------------------").queue();
            }
            if(content.equalsIgnoreCase("!EndSession")) {
                String userID = event.getAuthor().getId();
                Integer endingSR = srTracker.getPlayerSR(userID);
                Integer startingSR = srSession.endSession(userID);
                channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + startingSR +
                        "\rEnding SR: " + endingSR + "\rDifference: " + (endingSR - startingSR) + "\r------------------------").queue();
                writeToFile(srSession.getHistory().toString(), "SRSessions.txt");
            }
            String[] input = content.split(" ");
            if (input.length == 1) {
                try {
                    Integer updatedSR = Integer.parseInt(content);
                    HashMap<String, Integer> srHistory = srTracker.updateSR(event.getAuthor().getId(), updatedSR);
                    if (srHistory.get("Difference") > 0) {
                        event.getMessage().addReaction("\uD83D\uDC4D").queue();
                        event.getMessage().addReaction("\uD83D\uDC4C").queue();
                        channel.sendMessage(event.getAuthor().getAsMention() + "\r------------------------\rNew SR: " + srHistory.get("New SR") +
                                "\rPrevious SR: " + srHistory.get("Old SR") + "\rDifference: +" + srHistory.get("Difference") + "\r------------------------").queue();
                        writeToFile(srTracker.getHistory().toString(), "SRHistory.txt");
                    } else if (srHistory.get("Difference") < 0) {
                        event.getMessage().addReaction("\uD83D\uDC4E").queue();
                        event.getMessage().addReaction("\uD83D\uDE22").queue();
                        channel.sendMessage(event.getAuthor().getAsMention() + "\r------------------------\rNew SR: " + srHistory.get("New SR") +
                                "\rPrevious SR: " + srHistory.get("Old SR") + "\rDifference: " + srHistory.get("Difference") + "\r------------------------").queue();
                        writeToFile(srTracker.getHistory().toString(), "SRHistory.txt");
                    } else {
                        event.getMessage().addReaction("\uD83D\uDE10").queue();
                        event.getMessage().addReaction("\uD83E\uDD37").queue();
                        channel.sendMessage(event.getAuthor().getAsMention() + "\r------------------------\rNew SR: " + srHistory.get("New SR") +
                                "\rPrevious SR: " + srHistory.get("Old SR") + "\rDifference: " + srHistory.get("Difference") + "\r------------------------").queue();
                        writeToFile(srTracker.getHistory().toString(), "SRHistory.txt");
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println();
                }
            }
        }

        if (event.getAuthor().getId().equals("230347831335059457")) {
            //Save the SR history to file
            if (content.equals("!savesr")) {
                channel.sendMessage("Saving SR records to file.").queue();
                writeToFile(srTracker.getHistory().toString(), "SRHistory.txt");
            }
            //Load the SR history from file
            if (content.equals("!loadsr")) {
                channel.sendMessage("Loading SR records from file....").queue();
                String srHistoryString = readFromFile("SRHistory.txt");
                if (srHistoryString != null) {
                    srTracker.loadSRHistory(parseStorageFile(srHistoryString));
                    channel.sendMessage("SR records have been loaded.").queue();
                } else {
                    channel.sendMessage("The records file could not be located.").queue();
                }
            }
        }
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

    private void writeToFile(String textToWrite, String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(fileLocation + "/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(textToWrite);

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            FileReader fileReader = new FileReader(fileLocation + "/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int i;
            String fileAsString = "";
            while((i=bufferedReader.read())!=-1){
                fileAsString = fileAsString.concat(String.valueOf((char)i));
            }
            bufferedReader.close();
            fileReader.close();
            return fileAsString;
        } catch (FileNotFoundException e) {
            System.out.println("There is currently no " + fileName + " file");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}