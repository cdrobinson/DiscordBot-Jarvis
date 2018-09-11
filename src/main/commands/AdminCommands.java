import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.List;

class AdminCommands {

    void parseCommand(String content, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if (content.contains("!say")) {
            String[] commandString = content.split("!say");
            String whatToSay = commandString[1];
            event.getGuild().getTextChannelsByName("General", true).get(0).sendMessage(whatToSay).queue();
            System.out.printf("You told me to say this in #general chat: %s", whatToSay);
        }
        if (content.contains("!perms")) {
            List<Permission> authorPerms = event.getMember().getPermissions();
            StringBuilder permList = new StringBuilder();
            permList.append("```");
            for (Permission permission : authorPerms) {
                permList.append(permission.getName());
                permList.append("\r");
            }
            permList.append("```");
            channel.sendMessage(permList.toString()).queue();
        }
    }
}
