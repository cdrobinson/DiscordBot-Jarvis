import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

class CommandParser {

    void parseCommand(String content, MessageChannel channel, MessageReceivedEvent event) {
        switch (content.toLowerCase()) {
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
        }
    }
}
