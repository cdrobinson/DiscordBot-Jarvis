import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;
import java.util.List;

class CommandParser {

    private FeatureRequester featureRequester;

    CommandParser() {
        this.featureRequester = new FeatureRequester();
    }

    void parseCommand(JDA jdaApi, String content, MessageReceivedEvent event, SRSession srSession, SRTracker srTracker) {
        String lcContent = content.toLowerCase();
        FileManager fileManager = new FileManager();
        String[] contentString = content.split(" ");
        String command = contentString[0].toLowerCase();
        MessageChannel channel = event.getChannel();

        if (channel.getName().equals(new ConfigManager().getProperty("srTrackingChannelName"))) {
            srTracker.parseCommand(contentString, event, srSession);
        }

        if (channel.getName().equals(new ConfigManager().getProperty("featureRequestChannelName"))) {
            switch (command) {
                case "!rf":
                    featureRequester.addRequest(event.getMessage().getContentDisplay().split("!rf ")[1], event);
                    break;
                case "!rfrepost":
                    featureRequester.repostFeatureList(event);
                    break;
            }
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                switch (command) {
                    case "!rfsetup":
                        featureRequester.setUp(event);
                        event.getMessage().delete().queue();
                        break;
                    case "!rfdeny":
                        featureRequester.denyRequest(event, lcContent.split("!rfdeny ")[1]);
                        break;
                    case "!rfapprove":
                        featureRequester.approveRequest(event, lcContent.split("!rfapprove ")[1]);
                        break;
                }
            }
        }

        switch (command) {
            case "!ping":
                channel.sendMessage("Pong! `" + jdaApi.getPing() + "`").queue();
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
            case "!wow":
                File wowPic = fileManager.getFile("wow.jpg");
                if (wowPic != null) {
                    channel.sendFile(wowPic, "wow.jpg").queue();
                }
                break;
            case "wow":
                for (Message message: event.getChannel().getHistoryBefore(event.getMessageId(), 1).complete().getRetrievedHistory()) {
                    message.addReaction(event.getGuild().getEmoteById("481569620118208512")).queue();
                }
                break;
            case "gruhz":
                channel.sendMessage("Fuck off, Oly").queue();
                break;
            case "!testing":
                channel.sendMessage("Let me check that for you...").queue();
                if (contentString.length > 1) {
                    String srCheck = ProfileReader.getSR(contentString[1]);
                    channel.sendMessage(srCheck).queue();
                }
                break;
            case "!chirpchirp":
                List<Guild> mutualGuilds = event.getMember().getUser().getMutualGuilds();
                for (Guild guild : mutualGuilds) {
                    if (guild.getId().equals("260565533575872512")) {
                        List<Role> userRoles = guild.getMemberById(event.getMember().getUser().getId()).getRoles();
                        for (Role role : userRoles) {
                            if (role.getId().equals("443151138062073866")) {
                                event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("451495511724130305")).queue();
                            }
                        }
                    }
                }
                break;
            case "!vote":
                UserInputManager.createPoll(event);
                break;
        }

        //Multi word commands
        switch (lcContent) {
            case "!no u":
            case "!no you":
                File noYou = fileManager.getFile("noYou.png");
                if (noYou != null) {
                    channel.sendFile(noYou, "noYou.png").queue();
                }
                break;
            case "no u":
            case "no you":
                for (Message message: event.getChannel().getHistoryBefore(event.getMessageId(), 1).complete().getRetrievedHistory()) {
                    message.addReaction(event.getGuild().getEmoteById("481561171653165067")).queue();
                }
                break;
            default:
                break;
        }

        //Inline commands
        if (lcContent.contains("girl") || lcContent.contains("grill") || lcContent.contains("gorl") || lcContent.contains("gurl")) {
            channel.sendMessage("If she breathes, she's a thot!").queue();
        }
        if (lcContent.contains("women")) {
            channel.sendMessage("All women are queens").queue();
        }
        if (lcContent.contains("opinion")) {
            File myOpinion = fileManager.getFile("myOpinion.png");
            if (myOpinion != null) {
                channel.sendFile(myOpinion, "myOpinion.png").queue();
            }
        }
    }
}
