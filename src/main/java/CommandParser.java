import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

class CommandParser {

    void parseCommand(String content, MessageReceivedEvent event, SRSession srSession, SRTracker srTracker) {
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
                channel.sendMessage("SCOTLAND FOREVER!!!").queue();
                break;
            case "!cherrybomb":
                channel.sendMessage("ch-ch-ch CHERRY BOMB!").queue();
                channel.sendMessage(":cherries::cherries::cherries::cherries::cherries::cherries:").queue();
                break;
            case "!damnitjerry":
                channel.sendFile(fileManager.getFile("jerryPic.jpg")).queue();
                break;
            case "!noice":
                channel.sendFile(fileManager.getFile("noice.jpg")).queue();
                break;
            case "!sr":
                String lookUpID = contentString[1].substring(3, contentString[1].length()-1);
                Integer lookUpSR = srTracker.getPlayerSR(lookUpID);
                Integer authorSR = srTracker.getPlayerSR(authorID);
                Integer difference = authorSR - lookUpSR;
                if (difference > 0) {
                    channel.sendMessage(contentString[1] + "'s SR is currently " + lookUpSR + " which is " + difference + " less than yours.").queue();
                } else if (difference < 0) {
                    channel.sendMessage(contentString[1] + "'s SR is currently " + lookUpSR + " which is " + difference + " more than yours.").queue();
                } else {
                    channel.sendMessage(contentString[1] + "'s SR is currently " + lookUpSR + " which is the same as yours").queue();
                }
                break;
            case "!session":
                Integer startingSR = srTracker.getPlayerSR(authorID);
                switch (contentString[1]) {
                    case "start":
                        if (startingSR != null) {
                            channel.sendMessage("Starting a session for " + event.getAuthor().getAsMention() + "with a starting SR of " + startingSR).queue();
                            srSession.startSession(authorID, startingSR);
                            fileManager.writeToTextFile(srSession.getHistory().toString(), "SRSessions.txt");
                        } else {
                            channel.sendMessage("Please enter a starting SR first.").queue();
                        }
                        break;
                    case "current":
                        Integer currentSR = srTracker.getPlayerSR(authorID);
                        if (startingSR != null) {
                            channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + startingSR +
                                    "\rCurrent SR: " + currentSR + "\rDifference: " + (currentSR - startingSR) + "\r------------------------").queue();
                        } else {
                            channel.sendMessage("You don't have a session going right now. Type \"!startSession\" to begin one.").queue();
                        }
                        break;
                    case "end":
                        Integer endingSR = srTracker.getPlayerSR(authorID);
                        if (startingSR != null) {
                            channel.sendMessage(event.getAuthor().getAsMention() + "'s Session Details\r------------------------\rStarting SR: " + startingSR +
                                    "\rEnding SR: " + endingSR + "\rDifference: " + (endingSR - startingSR) + "\r------------------------").queue();
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
