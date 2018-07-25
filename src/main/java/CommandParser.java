import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;

class CommandParser {

    void parseCommand(String content, MessageReceivedEvent event, SRSession srSession, SRTracker srTracker, JDA jdaApi) {
        FileManager fileManager = new FileManager();

        String authorID = event.getAuthor().getId();
        String[] contentString = content.split(" ");
        String command = contentString[0].toLowerCase();
        MessageChannel channel = event.getChannel();

        switch (command) {
            case "!ping":
                channel.sendMessage("Pong!").queue();
                break;
            case "!bing":
                channel.sendMessage("Bong!").queue();
                break;
            case "!help":
                channel.sendMessage(HelpMessageBuilder.getHelpMessage()).queue();
                break;
            case "!scotland":
                MessageBuilder scotlandBuilder = new MessageBuilder();
                scotlandBuilder.setTTS(true);
                scotlandBuilder.append("SCOTLAND FOREVER!!!");
                channel.sendMessage(scotlandBuilder.build()).queue();
                break;
            case "!allwomen":
                MessageBuilder thotBuilder = new MessageBuilder();
                thotBuilder.setTTS(true);
                thotBuilder.append("If she breathes, she's a thot!");
                channel.sendMessage("All women are queens!").queue();
                channel.sendMessage(thotBuilder.build()).queue();
                break;
            case "!cherrybomb":
                channel.sendMessage("ch-ch-ch CHERRY BOMB!").queue();
                channel.sendMessage(":cherries::cherries::cherries::cherries::cherries::cherries:").queue();
                break;
            case "!damnitjerry":
                File jerryPic = fileManager.getFile("jerryPic.jpg");
                if (jerryPic != null) {
                    channel.sendFile(jerryPic, "jerry.jpg").queue();
                }
                break;
            case "!noice":
                File noice = fileManager.getFile("noice.jpg");
                if (noice != null) {
                    channel.sendFile(noice, "noice.jpg").queue();
                    MessageBuilder noiceBuilder = new MessageBuilder();
                    noiceBuilder.setTTS(true);
                    noiceBuilder.append("noice");
                    channel.sendMessage(noiceBuilder.build()).queue();
                }
                break;
            case "!leaderboard":
                channel.sendMessage(srTracker.getLeaderboard(event.getGuild())).queue();
                break;
            case "!sr":
                String lookUpID;
                String tester = contentString[1].substring(2, 3);
                if (tester.equals("!")) {
                    lookUpID = contentString[1].substring(3, contentString[1].length()-1);
                } else {
                    lookUpID = contentString[1].substring(2, contentString[1].length()-1);
                }
                Integer lookUpSR = srTracker.getPlayerSR(lookUpID);
                if (lookUpSR != null) {
                    Integer authorSR = srTracker.getPlayerSR(authorID);
                    Integer difference = authorSR - lookUpSR;
                    if (difference > 0) {
                        channel.sendMessage(contentString[1] + "'s SR is currently " + lookUpSR + " which is -" + difference + " less than yours.").queue();
                    } else if (difference < 0) {
                        channel.sendMessage(contentString[1] + "'s SR is currently " + lookUpSR + " which is +" + (lookUpSR - authorSR) + " more than yours.").queue();
                    } else {
                        channel.sendMessage(contentString[1] + "'s SR is currently " + lookUpSR + " which is the same as yours").queue();
                    }
                } else {
                    channel.sendMessage(contentString[1] + " thinks they are too good for me to track their SR").queue();
                }
                break;
            case "!session":
                Integer currentSR = srTracker.getPlayerSR(authorID);
                Integer storedSR = srSession.getStoredSR(authorID);
                switch (contentString[1]) {
                    case "start":
                        if (currentSR != null) {
                            channel.sendMessage("Starting a session for " + event.getAuthor().getAsMention() + "with a starting SR of " + currentSR).queue();
                            srSession.startSession(authorID, currentSR);
                            fileManager.writeToTextFile(srSession.getHistory().toString(), "SRSessions.txt");
                        } else {
                            channel.sendMessage("Please enter a starting SR first.").queue();
                        }
                        break;
                    case "current":
                        if (currentSR != null) {
                            channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + storedSR +
                                    "\rCurrent SR: " + currentSR + "\rDifference: " + (currentSR - storedSR) + "\r------------------------").queue();
                        } else {
                            channel.sendMessage("You don't have a session going right now. Type \"!startSession\" to begin one.").queue();
                        }
                        break;
                    case "end":
                        if (currentSR != null) {
                            channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + storedSR +
                                    "\rEnding SR: " + currentSR + "\rDifference: " + (currentSR - storedSR) + "\r------------------------").queue();
                            srSession.endSession(authorID);
                            fileManager.writeToTextFile(srSession.getHistory().toString(), "SRSessions.txt");
                        } else {
                            channel.sendMessage("You don't have a session going right now. Type \"!startSession\" to begin one.").queue();
                        }
                        break;
                    default:
                        channel.sendMessage("The session command you entered was invalid. Your options are [start, current, end].").queue();
                        break;
                }
                break;
        }
    }
}
