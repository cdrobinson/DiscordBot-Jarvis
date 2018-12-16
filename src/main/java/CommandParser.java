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

        switch (command) {
            case "!ping":
                channel.sendMessage("Pong! `" + jdaApi.getPing() + "`").queue();
                break;
            case "!bing":
                channel.sendMessage("Bong!").queue();
                break;
            case "!help":
                channel.sendMessage(Util_HelpMessageBuilder.getHelpMessage()).queue();
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
                for (Message message : event.getChannel().getHistoryBefore(event.getMessageId(), 1).complete().getRetrievedHistory()) {
                    message.addReaction(event.getGuild().getEmoteById("481569620118208512")).queue();
                }
                break;
            case "gruhz":
                channel.sendMessage("Fuck off, Oly").queue();
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
            case "!feed":
                event.getMessage().addReaction("\uD83D\uDEE2").queue();
                channel.sendMessage("\uD83D\uDEE2 \uD83D\uDE00 \uD83D\uDE42 \uD83D\uDE16 \uD83D\uDCA9 \uD83D\uDE0C").queue();
                break;
            case "!test":
                new SR_OverwatchProfile("Manofvault#1415");
                new SR_OverwatchProfile("Solitary#11979");
                /*EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.YELLOW);
                embedBuilder.setAuthor("BattlemanMK2", "https://playoverwatch.com/en-us/career/pc/Battlemanmk2-1251");
                //embedBuilder.setTitle("Overwatch Profile Report", "https://playoverwatch.com/en-us/career/pc/Battlemanmk2-1251");
                //embedBuilder.setDescription("Here is a breakdown of BattlemanMK2's SR");
                embedBuilder.setThumbnail("https://d15f34w2p8l1cc.cloudfront.net/overwatch/155a82e7279318dc60344907aed290f2ea4c4387e73285659969668939979cfa.png");
                embedBuilder.addField("SR", "3000", false);
                embedBuilder.setFooter("Battlemanmk2#1251", "https://d1u1mce87gyfbn.cloudfront.net/game/rank-icons/rank-PlatinumTier.png");
                channel.sendMessage(embedBuilder.build()).queue();*/
                break;
            default:
                    break;
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
