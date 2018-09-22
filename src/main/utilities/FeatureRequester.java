import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(featureList[2]);
            for (int i = 3; i < featureList.length; i++) {
                stringBuilder.append("\n");
                stringBuilder.append(featureList[i]);
            }
            this.featureList = stringBuilder.toString();
        }
    }

    private void saveFile() {
        FileManager fileManager = new FileManager();
        String fileName = "FrontLine_featureList.txt";
        String fileContents = this.frChannelId + "\n" +
                this.pinnedMessageId + "\n" +
                this.featureList;
        fileManager.writeToTextFile(fileContents, fileName);
    }

    void repostFeatureList(MessageReceivedEvent event) {
        event.getGuild().getTextChannelById(frChannelId).getMessageById(pinnedMessageId).queue((pinnedMessage) -> {
            pinnedMessage.unpin().queue();
            pinnedMessage.delete().queue();
        });
        event.getGuild().getTextChannelById(this.frChannelId).sendMessage(this.featureList).queue((newMessage) -> {
            this.pinnedMessageId = newMessage.getId();
            newMessage.pin().queue();
            saveFile();
        });
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
                saveFile();
            });
        }
    }

    void denyRequest(MessageReceivedEvent event, String requestString) {
        Integer requestNumber;
        try {
            requestNumber = Integer.valueOf(requestString);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("You idiot, that's not a number.").queue();
            return;
        }
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
                saveFile();
            }
        });
    }

    void approveRequest(MessageReceivedEvent event, String requestString) {
        Integer requestNumber;
        try {
            requestNumber = Integer.valueOf(requestString);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("You idiot, that's not a number.").queue();
            return;
        }
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
                saveFile();
            }
        });
    }

    public void setUp(MessageReceivedEvent event) {
        String setupMessage = "List of requested features:\n⭕=Received | ✔=Added | ❌=Denied\n```\n```";
        event.getChannel().sendMessage(setupMessage).queue((featureListMessage) -> {
            this.pinnedMessageId = featureListMessage.getId();
            featureListMessage.pin().queue();
            this.frChannelId = event.getChannel().getId();
            this.featureList = setupMessage;
            saveFile();
        });
    }
}
