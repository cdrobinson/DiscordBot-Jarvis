import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.List;

class CommandParser {


    CommandParser() {
    }

    void parseCommand(JDA jdaApi, String contentString, MessageReceivedEvent event) {
        String lcContent = contentString.toLowerCase();
        Util_FileManager fileManager = new Util_FileManager();
        String[] contentList = contentString.split(" ");
        String command = contentList[0].toLowerCase();
        MessageChannel channel = event.getChannel();

        if ("!ping".equals(command)) {
            channel.sendMessage("Pong! `" + jdaApi.getPing() + "`").queue();
        } else if ("!bing".equals(command)) {
            channel.sendMessage("Bong!").queue();
        } else if ("!help".equals(command)) {
            channel.sendMessage(Util_HelpMessageBuilder.getHelpMessage()).queue();
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
        } else if ("!feed".equals(command)) {
            event.getMessage().addReaction("\uD83D\uDEE2").queue();
            channel.sendMessage("\uD83D\uDEE2 \uD83D\uDE00 \uD83D\uDE42 \uD83D\uDE16 \uD83D\uDCA9 \uD83D\uDE0C").queue();
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
