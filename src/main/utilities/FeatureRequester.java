import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

class FeatureRequester {

    private final TextChannel frChannel;
    private Message pinnedMessage;

    FeatureRequester(TextChannel featureRequestChannel) {
        this.pinnedMessage = null;
        this.frChannel = featureRequestChannel;
    }

    void addRequest(String request) {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.pinnedMessage == null) {
            stringBuilder.append("```");
            stringBuilder.append(request);
            stringBuilder.append("```");
            frChannel.sendMessage(stringBuilder.toString()).queue();
        } else {
            stringBuilder.append(this.pinnedMessage.getContentRaw());
            stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
            stringBuilder.append(request);
            stringBuilder.append("```");
            this.pinnedMessage.editMessage(stringBuilder.toString()).queue();
        }
    }
}
