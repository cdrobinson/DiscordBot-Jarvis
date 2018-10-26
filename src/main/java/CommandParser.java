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

    void parseCommand(JDA jdaApi, String contentString, MessageReceivedEvent event, SRSession srSession, SRTracker srTracker) {
        String lcContent = contentString.toLowerCase();
        FileManager fileManager = new FileManager();
        String[] contentList = contentString.split(" ");
        String command = contentList[0].toLowerCase();
        MessageChannel channel = event.getChannel();

        if (channel.getName().equals(new ConfigManager().getProperty("featureRequestChannelName"))) {
            if ("!rf".equals(command)) {
                featureRequester.addRequest(event.getMessage().getContentDisplay().split("!rf ")[1], event);
            } else if ("!rfrepost".equals(command)) {
                featureRequester.repostFeatureList(event);
            }
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if ("!rfsetup".equals(command)) {
                    featureRequester.setUp(event);
                    event.getMessage().delete().queue();
                } else if ("!rfdeny".equals(command)) {
                    featureRequester.denyRequest(event, lcContent.split("!rfdeny ")[1]);
                } else if ("!rfapprove".equals(command)) {
                    featureRequester.approveRequest(event, lcContent.split("!rfapprove ")[1]);
                }
            }
        }

        if ("!ping".equals(command)) {
            channel.sendMessage("Pong! `" + jdaApi.getPing() + "`").queue();
        } else if ("!bing".equals(command)) {
            channel.sendMessage("Bong!").queue();
        } else if ("!help".equals(command)) {
            channel.sendMessage(HelpMessageBuilder.getHelpMessage()).queue();
        } else if ("!scotland".equals(command)) {
            MessageBuilder scotlandBuilder = new MessageBuilder();
            scotlandBuilder.setTTS(true);
            scotlandBuilder.append("SCOTLAND FOREVER!!!");
            channel.sendMessage(scotlandBuilder.build()).queue();
        } else if ("!allwomen".equals(command)) {
            MessageBuilder thotBuilder = new MessageBuilder();
            thotBuilder.setTTS(true);
            thotBuilder.append("If she breathes, she's a thot!");
            channel.sendMessage("All women are queens!").queue();
            channel.sendMessage(thotBuilder.build()).queue();
        } else if ("!cherrybomb".equals(command)) {
            channel.sendMessage("ch-ch-ch CHERRY BOMB!").queue();
            channel.sendMessage(":cherries::cherries::cherries::cherries::cherries::cherries:").queue();
        } else if ("!damnitjerry".equals(command)) {
            File jerryPic = fileManager.getFile("jerryPic.jpg");
            if (jerryPic != null) {
                channel.sendFile(jerryPic, "jerry.jpg").queue();
            }
        } else if ("!noice".equals(command)) {
            File noice = fileManager.getFile("noice.jpg");
            if (noice != null) {
                channel.sendFile(noice, "noice.jpg").queue();
                MessageBuilder noiceBuilder = new MessageBuilder();
                noiceBuilder.setTTS(true);
                noiceBuilder.append("noice");
                channel.sendMessage(noiceBuilder.build()).queue();
            }
        } else if ("!wow".equals(command)) {
            File wowPic = fileManager.getFile("wow.jpg");
            if (wowPic != null) {
                channel.sendFile(wowPic, "wow.jpg").queue();
            }
        } else if ("wow".equals(command)) {
            for (Message message : event.getChannel().getHistoryBefore(event.getMessageId(), 1).complete().getRetrievedHistory()) {
                message.addReaction(event.getGuild().getEmoteById("481569620118208512")).queue();
            }
        } else if ("gruhz".equals(command)) {
            channel.sendMessage("Fuck off, Oly").queue();
        } else if ("!chirpchirp".equals(command)) {
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
        } else if ("!vote".equals(command)) {
            UserInputManager.createPoll(event);
        }

        //Multi word commands
        if ("!no u".equals(lcContent) || "!no you".equals(lcContent)) {
            File noYou = fileManager.getFile("noYou.png");
            if (noYou != null) {
                channel.sendFile(noYou, "noYou.png").queue();
            }
        } else if ("no u".equals(lcContent) || "no you".equals(lcContent)) {
            for (Message message : event.getChannel().getHistoryBefore(event.getMessageId(), 1).complete().getRetrievedHistory()) {
                message.addReaction(event.getGuild().getEmoteById("481561171653165067")).queue();
            }
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
