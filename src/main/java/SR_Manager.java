import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SR_Manager implements Runnable {
    private MessageReceivedEvent event;

    SR_Manager(MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] contentList = content.split(" ");
        parseCommand(contentList);
    }

    private void parseCommand(String[] contentString) {
        switch (contentString[0].toLowerCase()) {
            case "!updatesr":
                event.getChannel().sendTyping().queue();
                updateSRDatabase();
                break;
            case "!registerbattletag":
                event.getChannel().sendTyping().queue();
                registerBattletag(contentString);
                break;
            case "!sr":
                event.getChannel().sendTyping().queue();
                checkSR(contentString);
                break;
            case "!leaderboard":
                event.getChannel().sendTyping().queue();
                getLeaderboard();
                break;
            case "!getbattletag":
                event.getChannel().sendTyping().queue();
                postBattletagFromDiscordID(contentString);
                break;
            case "!getdiscord":
                event.getChannel().sendTyping().queue();
                postDiscordNameFromBattletag(contentString);
                break;
            default:
                break;
        }
    }

    private void registerBattletag(String[] contentString){
        MessageChannel channel = event.getChannel();
        if (contentString.length == 1) {
            channel.sendMessage("Please enter a battletag after the command `.registerBattletag [battletag]`").queue();
            return;
        }
        channel.sendMessage("Registering....").queue();
        channel.sendTyping().queue();
        SR_OverwatchProfile overwatchProfile = new SR_OverwatchProfile(contentString[1]);
        String userSR = overwatchProfile.getSR();
        event.getChannel().sendTyping().queue();
        try {
            switch (userSR) {
                case "This player has not placed yet":
                    userSR = "Not Placed";
                    break;
                case "This player's profile is private":
                    channel.sendMessageFormat("I'll register your battletag, but please make sure to set your profile to public").queue();
                    userSR = "Private";
                    break;
                default:
                    Integer.valueOf(userSR);
                    break;
            }
            String fullDiscordName = event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator();
            MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
            boolean added = mongoDB_sr_manager.addUserToDatabase(fullDiscordName, event.getAuthor().getId(),
                    contentString[1], Integer.valueOf(userSR), overwatchProfile.getProfileURL(),
                    overwatchProfile.getPortraitURL(), overwatchProfile.getRankIconURL());
            mongoDB_sr_manager.endConnection();
            if (added) {
                channel.sendMessageFormat("You have registered %s as your battletag with a current SR of %s", contentString[1], userSR).queue();
            } else {
                channel.sendMessageFormat("You have already registered %s as your battletag", getBattletagFromDiscordID(event.getAuthor().getId())).queue();
            }
        } catch (NumberFormatException e) {
            channel.sendMessageFormat("An error occured while looking up your SR: `%s`", userSR).queue();
        }
    }

    private void postBattletagFromDiscordID(String[] contentString) {
        String authorID = event.getAuthor().getId();
        MessageChannel channel = event.getChannel();
        MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
        if (contentString.length > 1) {
            String lookUpID;
            //checks if the member @'ed is using a nickname or not
            String nicknameTag = contentString[1].substring(2, 3);
            if (nicknameTag.equals("!")) {
                lookUpID = contentString[1].substring(3, contentString[1].length() - 1);
            } else {
                lookUpID = contentString[1].substring(2, contentString[1].length() - 1);
            }
            String lookupBattletag;
            event.getChannel().sendTyping().queue();
            lookupBattletag = mongoDB_sr_manager.getBattletagByDiscordId(lookUpID);
            if (lookupBattletag ==  null) {
                channel.sendMessageFormat("%s's SR is not on file.").queue();
                mongoDB_sr_manager.endConnection();
                return;
            }
            String lookUpName = event.getGuild().getMemberById(lookUpID).getEffectiveName();
            channel.sendMessageFormat("%s's stored battletag is %s", lookUpName, lookupBattletag).queue();
        } else {
            String authorBattletag;
            event.getChannel().sendTyping().queue();
            authorBattletag = mongoDB_sr_manager.getBattletagByDiscordId(authorID);
            if (authorBattletag != null) {
                channel.sendMessage("Your stored battletag is currently: " + authorBattletag).queue();
            } else {
                channel.sendMessage("Your battletag is not currently on file. Run ``.registerBattletag [battletag]`` to register it.").queue();
            }
        }
        mongoDB_sr_manager.endConnection();
    }

    private void postDiscordNameFromBattletag(String[] contentString) {
        MessageChannel channel = event.getChannel();
        if (contentString.length > 1) {
            event.getChannel().sendTyping().queue();
            MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
            String lookupDiscordID = mongoDB_sr_manager.getDiscordIdByBattletag(contentString[1]);
            if (lookupDiscordID ==  null) {
                channel.sendMessageFormat("%s's SR is not on file.").queue();
                mongoDB_sr_manager.endConnection();
                return;
            }
            String lookUpName = event.getGuild().getMemberById(lookupDiscordID).getEffectiveName();
            channel.sendMessageFormat("%s's stored Discord name is %s", contentString[1], lookUpName).queue();
            mongoDB_sr_manager.endConnection();
        } else {
            channel.sendMessage("Make sure you enter a Battletag after the command").queue();
        }
    }

    private void updateSRDatabase() {
        MessageChannel channel = event.getChannel();
        MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
        List<String> allBattletags = mongoDB_sr_manager.getAllBattletags();
        for (String battletag : allBattletags) {
            SR_OverwatchProfile overwatchProfile = new SR_OverwatchProfile(battletag);
            String userSR = overwatchProfile.getSR();
            switch (userSR) {
                case "This player has not placed yet":
                    userSR = "Not Placed";
                    break;
                case "This player's profile is private":
                    userSR = "Private";
                    break;
            }
            Boolean updated = mongoDB_sr_manager.updateUserByBattletag(battletag, userSR,
                    overwatchProfile.getProfileURL(), overwatchProfile.getPortraitURL(), overwatchProfile.getRankIconURL());
            if (!updated) {
                channel.sendMessageFormat("There was an issue updating the SR for %s", battletag).queue();
            }
            event.getChannel().sendTyping().queue();
        }
        channel.sendMessage("All of the battletags have been updated").queue();
        mongoDB_sr_manager.endConnection();
    }

    private MessageEmbed getOverwatchEmbed(String battletag, String profileURL, String iconURL, String sr, String rankIconURL) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(255, 156, 0));
        embedBuilder.setAuthor(battletag.split("#")[0], profileURL);
        embedBuilder.setThumbnail(iconURL);
        embedBuilder.addField("Skill Rank:", sr, false);
        embedBuilder.setFooter(battletag, rankIconURL);
        return embedBuilder.build();
    }

    private void checkSR(String[] contentString){
        MessageChannel channel = event.getChannel();
        MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
        String lookUpDiscordID;
        Integer lookUpSR;
        if (contentString.length > 1) {
            lookUpDiscordID = event.getMessage().getMentionedUsers().get(0).getId();
        } else {
            lookUpDiscordID = event.getAuthor().getId();
        }
        lookUpSR = mongoDB_sr_manager.getUserSrByDiscordId(lookUpDiscordID);
        if (lookUpSR != null) {
            SR_DatabaseUser user = mongoDB_sr_manager.getUserDataByDiscordId(lookUpDiscordID);
            channel.sendMessage(getOverwatchEmbed(user.getBattletag(), user.getProfileURL(), user.getIconURL(), user.getSR().toString(), user.getRankIconURL())).queue();
        } else {
            channel.sendMessage("That user has not registered their Battletag with me yet. They need to run ``.registerBattletag [battletag]`` to register it.").queue();
        }
        mongoDB_sr_manager.endConnection();
    }

    private void getLeaderboard(){
        MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
        List<SR_DatabaseUser> databaseData = mongoDB_sr_manager.getFullDatabase();
        Comparator<SR_DatabaseUser> databaseComparator = Comparator.comparing(SR_DatabaseUser::getSR);
        databaseData.sort(databaseComparator);
        Collections.reverse(databaseData);
        StringBuilder leaderboardString = new StringBuilder();
        leaderboardString.append("**=====================**\n");
        event.getChannel().sendTyping().queue();
        for (SR_DatabaseUser user : databaseData) {
            leaderboardString
                    .append(event.getGuild().getMemberById(user.getDiscordID()).getEffectiveName())
                    .append("** | **")
                    .append(user.getSR().toString())
                    .append("** | **")
                    .append(user.getBattletag())
                    .append("\n");
        }
        leaderboardString.append("**=====================**");
        event.getChannel().sendMessage(leaderboardString.toString()).queue();
        mongoDB_sr_manager.endConnection();
    }

    private String getBattletagFromDiscordID(String discordID) {
        MongoDB_SR_Manager mongoDB_sr_manager = new MongoDB_SR_Manager();
        String battletag = mongoDB_sr_manager.getBattletagByDiscordId(discordID);
        mongoDB_sr_manager.endConnection();
        return battletag;
    }
}
