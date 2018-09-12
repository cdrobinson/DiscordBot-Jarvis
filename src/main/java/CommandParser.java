import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;
import java.util.List;

class CommandParser {

    private FeatureRequester featureRequester;

    CommandParser(JDA jdaApi) {
        this.featureRequester = new FeatureRequester(jdaApi.getGuildById("237059614384848896").getTextChannelsByName("feature-request", true).get(0));
    }

    void parseCommand(JDA jdaApi, String content, MessageReceivedEvent event, SRSession srSession, SRTracker srTracker) {
        String lcContent = content.toLowerCase();
        FileManager fileManager = new FileManager();
        String authorID = event.getAuthor().getId();
        String[] contentString = content.split(" ");
        String command = contentString[0].toLowerCase();
        MessageChannel channel = event.getChannel();
        SRReporter srReporter = new SRReporter();

        if (channel.getName().equals("sr-tracking")) {
            srTracker.parseSrUpdate(content, srTracker, event, fileManager);
        }
        if (channel.getName().equals("feature-request")) {
            if (command.equals("!rf")) {
                featureRequester.addRequest(content.split("!rf ").toString());
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
            case "!leaderboard":
                channel.sendMessage(srTracker.getLeaderboard(event.getGuild())).queue();
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
            case "!sr":
                String lookUpID;
                String tester = contentString[1].substring(2, 3);
                if (tester.equals("!")) {
                    lookUpID = contentString[1].substring(3, contentString[1].length()-1);
                } else {
                    lookUpID = contentString[1].substring(2, contentString[1].length()-1);
                }
                Integer lookUpSR = srTracker.getPlayerSR(lookUpID);
                String lookUpName = event.getGuild().getMemberById(lookUpID).getEffectiveName();
                if (lookUpSR != null) {
                    Integer authorSR = srTracker.getPlayerSR(authorID);
                    Integer difference = authorSR - lookUpSR;
                    channel.sendMessage(srReporter.build(lookUpName, "SR Report", lookUpName + "'s", lookUpSR,
                            "Your", authorSR, difference)).queue();
                } else {
                    channel.sendMessage(lookUpName + " thinks they are too good for me to track their SR").queue();
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
            case "!session":
                Integer currentSR = srTracker.getPlayerSR(authorID);
                Integer storedSR = srSession.getStoredSR(authorID);
                if (contentString.length == 1) {
                    channel.sendMessage("The session command you entered was invalid. Your options are [start, current, end].").queue();
                    break;
                }
                switch (contentString[1]) {
                    case "start":
                        if (currentSR != null) {
                            Boolean started = srSession.startSession(authorID, currentSR);
                            if (started) {
                                channel.sendMessage("Starting a session for " + event.getAuthor().getAsMention() + " with a starting SR of " + currentSR).queue();
                            } else {
                                channel.sendMessageFormat("There is already a session for %s with a starting SR of %s", event.getAuthor().getAsMention(), storedSR).queue();
                            }
                        } else {
                            channel.sendMessage("Please enter a starting SR first.").queue();
                        }
                        break;
                    case "current":
                        if (currentSR != null) {
                            if (srSession.isSessionRunning(authorID)) {
                                channel.sendMessage(srReporter.build(event.getAuthor().getAsMention(), "Session Details", "Starting", storedSR,
                                        "Current", currentSR, (currentSR - storedSR))).queue();
                            } else {
                                channel.sendMessage("You don't have a session going right now. Type \"!session start\" to begin one.").queue();
                            }
                        } else {
                            channel.sendMessage("Please enter a starting SR first.").queue();
                        }
                        break;
                    case "end":
                        if (currentSR != null) {
                            if (srSession.isSessionRunning(authorID)) {
                                channel.sendMessage(srReporter.build(event.getAuthor().getAsMention(), "Session Details", "Starting", storedSR,
                                        "Ending", currentSR, (currentSR - storedSR))).queue();
                                srSession.endSession(authorID);
                            } else {
                                channel.sendMessage("You don't have a session going right now. Type \"!session start\" to begin one.").queue();
                            }
                        } else {
                            channel.sendMessage("Please enter a starting SR first.").queue();
                        }
                        break;
                    default:
                        channel.sendMessage("The session command you entered was invalid. Your options are [start, current, end].").queue();
                        break;
                }
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
