import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Map;

public class FeatureRequester implements Runnable {

    private MessageReceivedEvent event;
    private GS_FeatureRequestManager gs_featureRequestManager;
    private static final String headerMessage = "List of requested features:\n"; //⭕=Received | ✔=Added | ❌=Denied\n```\n";

    FeatureRequester(MessageReceivedEvent passedEvent) {
        this.event = passedEvent;
        this.gs_featureRequestManager = new GS_FeatureRequestManager();
    }

    @Override
    public void run() {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] contentList = content.split(" ");
        parseCommand(contentList, content);
    }

    private void parseCommand(String[] contentList, String content) {
        switch (contentList[0].toLowerCase()) {
            case "!rf":
                if (contentList.length > 1) {
                    addRequest(content.split("!rf ")[1]);
                } else {
                    event.getChannel().sendMessage("You forgot to add your request after the command, idiot!").queue();
                }
                break;
            case "!rfrepost":
                repostFeatureList();
                break;
            case "!rfsetup":
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    setUp();
                } else {
                    event.getChannel().sendMessage("You don't have permission to run that command").queue();
                }
                break;
            case "!rfapprove":
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    if (contentList.length > 1) {
                        approveRequest(contentList[1]);
                    } else {
                        event.getChannel().sendMessage("You forgot to to put which request to approve, come on now...").queue();
                    }
                } else {
                    event.getChannel().sendMessage("You don't have permission to run that command").queue();
                }
                break;
            case "!rfdeny":
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    if (contentList.length > 1) {
                        denyRequest(contentList[1]);
                    } else {
                        event.getChannel().sendMessage("You forgot to to put which request to deny, come on now...").queue();
                    }
                } else {
                    event.getChannel().sendMessage("You don't have permission to run that command").queue();
                }
                break;
            default:
                break;
        }
    }

    private void repostFeatureList() {
        Map<String, String> postDetails = gs_featureRequestManager.getPostDetails();
        event.getGuild().getTextChannelsByName(postDetails.get("Channel"), true).get(0).getMessageById(postDetails.get("ID")).queue((pinnedMessage) -> {
            pinnedMessage.unpin().queue();
            pinnedMessage.delete().queue();
        });
        final String frChannelName = postDetails.get("Channel");
        event.getGuild().getTextChannelsByName(frChannelName, true).get(0).sendMessage(getFormattedFeatureList()).queue((newMessage) -> {
            gs_featureRequestManager.updatePostDetails(newMessage.getId(), frChannelName);
            newMessage.pin().queue();
        });

    }

    private void addRequest(String request) {
        Map<String, String> postDetails = gs_featureRequestManager.getPostDetails();
        if (postDetails == null) {
            event.getChannel().sendMessage("A feature list hasn't been setup yet. Please run !rfSetup first").queue();
        } else {
            gs_featureRequestManager.addRequest(event.getAuthor().getId(),
                    event.getMember().getEffectiveName(),
                    request);
            event.getGuild().getTextChannelsByName(postDetails.get("Channel"), true).get(0)
                    .getMessageById(postDetails.get("ID")).queue((pinnedMessage) ->
                    pinnedMessage.editMessage(getFormattedFeatureList()).queue());
        }
    }

    private String getFormattedFeatureList(){
        List<FR_Request> frRequests = gs_featureRequestManager.getAllFeatureRequests();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(headerMessage);
        stringBuilder.append("```");
        for (FR_Request frRequest : frRequests) {
            stringBuilder.append(frRequests.indexOf(frRequest));
            stringBuilder.append(" | ");
            stringBuilder.append(frRequest.getApprovalStatus());
            stringBuilder.append(" | ");
            stringBuilder.append(event.getGuild().getMemberById(frRequest.getRequesterID()).getEffectiveName());
            stringBuilder.append(" | ");
            stringBuilder.append(frRequest.getRequest());
            stringBuilder.append("\n");
        }
        stringBuilder.append("```");
        return stringBuilder.toString();
    }

    private void setUp() {
        final String frChannelName = new ConfigManager().getProperty("featureRequestChannelName");
        event.getChannel().sendMessage(getFormattedFeatureList()).queue((featureListMessage) -> {
            gs_featureRequestManager.updatePostDetails(featureListMessage.getId(), frChannelName);
            featureListMessage.pin().queue();
        });

    }

    private void updatePost(){
        event.getChannel().getMessageById(gs_featureRequestManager.getPostDetails().get("ID")).queue((pinnedMessage) -> pinnedMessage.editMessage(getFormattedFeatureList()).queue());
    }

    private void denyRequest(String commandContent) {
        int position;
        try {
            position = Integer.parseInt(commandContent);
            position = position + 2;
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("You idiot, that's not a number.").queue();
            return;
        }
        List<List<Object>> values = gs_featureRequestManager.getRequestValues(Integer.toString(position));
        values.get(0).set(0, "Denied");
        gs_featureRequestManager.setRequestValues(Integer.toString(position), values);
        updatePost();
    }

    private void approveRequest(String commandContent) {
        int position;
        try {
            position = Integer.parseInt(commandContent);
            position = position + 2;
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("You idiot, that's not a number.").queue();
            return;
        }
        List<List<Object>> values = gs_featureRequestManager.getRequestValues(Integer.toString(position));
        values.get(0).set(0, "Approved");
        gs_featureRequestManager.setRequestValues(Integer.toString(position), values);
        updatePost();
    }
}
