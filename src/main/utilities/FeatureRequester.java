import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.MessageAction;


class FeatureRequester {

    private String frChannelId;
    private String pinnedMessageId;
    private String featureList;


    FeatureRequester() {
        this.frChannelId = null;
        this.pinnedMessageId = null;
        this.featureList = null;
        loadFromFile();
    }

    private void loadFromFile() {
        FileManager fileManager = new FileManager();
        String fileContents = fileManager.readFromTextFile("FrontLine_featureList.txt");
        if (fileContents != null) {
            String[] featureList = fileContents.split("\n");
            this.frChannelId = featureList[0];
            this.pinnedMessageId = featureList[1];
            this.featureList = featureList[2];
        }
    }


    private void saveFile(String guildName) {
        FileManager fileManager = new FileManager();
        String fileName = guildName + "_featureList.txt";
        String fileContents = this.frChannelId + "\n" +
                this.pinnedMessageId + "\n" +
                this.featureList;
        fileManager.writeToTextFile(fileContents, fileName);
    }

    void addRequest(String request, MessageReceivedEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.pinnedMessageId == null) {
            event.getChannel().sendMessage("A feature list hasn't been setup yet. Please run !rfSetup first").queue();
        } else {
            event.getGuild().getTextChannelById(frChannelId).getMessageById(pinnedMessageId).queue((pinnedMessage) -> {
                stringBuilder.append(pinnedMessage.getContentRaw());
                stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
                stringBuilder.append("⭕");
                stringBuilder.append(request);
                stringBuilder.append("\n");
                stringBuilder.append("```");
                pinnedMessage.editMessage(stringBuilder.toString()).queue();
                this.featureList = stringBuilder.toString();
                saveFile(event.getGuild().getName());
            });
        }
    }

    void denyRequest(MessageReceivedEvent event, Integer requestNumber) {
        StringBuilder updatedMessage = new StringBuilder();
        updatedMessage.append("List of requested features:\n⭕=Received | ✔=Added | ❌=Denied\n");
        event.getChannel().getMessageById(pinnedMessageId).queue((pinnedMessage) -> {
            String cleanRequests = pinnedMessage.getContentRaw().split("```")[1];
            String[] requestList = cleanRequests.split("\n");
            if (requestNumber > requestList.length || requestNumber < 1) {
                event.getChannel().sendMessageFormat("The feature you requested, %s, is not a valid feature number.", requestNumber).queue();
            } else {
                requestList[requestNumber] = requestList[requestNumber].replaceFirst("⭕", "❌");
                updatedMessage.append("```");
                for (String request : requestList) {
                    updatedMessage.append(request);
                    updatedMessage.append("\n");
                }
                updatedMessage.append("```");
                pinnedMessage.editMessage(updatedMessage.toString()).queue();
                this.featureList = updatedMessage.toString();
                saveFile(event.getGuild().getName());
            }
        });
    }

    void approveRequest(MessageReceivedEvent event, Integer requestNumber) {
        StringBuilder updatedMessage = new StringBuilder();
        updatedMessage.append("List of requested features:\n⭕=Received | ✔=Added | ❌=Denied\n");
        event.getChannel().getMessageById(pinnedMessageId).queue((pinnedMessage) -> {
            String cleanRequests = pinnedMessage.getContentRaw().split("```")[1];
            String[] requestList = cleanRequests.split("\n");
            if (requestNumber > requestList.length || requestNumber < 1) {
                event.getChannel().sendMessageFormat("The feature you requested, %s, is not a valid feature number.", requestNumber).queue();
            } else {
                requestList[requestNumber] = requestList[requestNumber].replaceFirst("⭕", "✔");
                updatedMessage.append("```");
                for (String request : requestList) {
                    updatedMessage.append(request);
                    updatedMessage.append("\n");
                }
                updatedMessage.append("```");
                pinnedMessage.editMessage(updatedMessage.toString()).queue();
                this.featureList = updatedMessage.toString();
                saveFile(event.getGuild().getName());
            }
        });
    }

    public void setUp(MessageReceivedEvent event) {
        String setupMessage = "List of requested features:\n⭕=Received | ✔=Added | ❌=Denied\n```\n```";
        event.getChannel().sendMessage(setupMessage).queue((featureListMessage) -> {
            setPinnedMessageId(featureListMessage.getId());
            featureListMessage.pin().queue();
        });
        this.frChannelId = event.getChannel().getId();
        this.featureList = setupMessage;
        saveFile(event.getGuild().getName());
    }

    void setPinnedMessageId(String id) {
        this.pinnedMessageId = id;
    }
}
