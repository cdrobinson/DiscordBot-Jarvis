import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
            String response = addToFile(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(),
                    event.getAuthor().getId(), contentString[1], userSR);
            switch (response) {
                case "added to file":
                    channel.sendMessageFormat("You have registered %s as your battletag with a current SR of %s", contentString[1], userSR).queue();
                    break;
                case "already on file":
                    channel.sendMessageFormat("You have already registered %s as your battletag", getBattletagFromDiscordID(event.getAuthor().getId())).queue();
                    break;
                default:
                    channel.sendMessage("There was an error saving your registration to file. Please try again. If the problem persists, contact an admin so we can resolve the issue").queue();
                    break;
            }
        } catch (NumberFormatException e) {
            channel.sendMessageFormat("An error occured while looking up your SR: `%s`", userSR).queue();
        }
    }

    private void postBattletagFromDiscordID(String[] contentString) {
        String authorID = event.getAuthor().getId();
        MessageChannel channel = event.getChannel();
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
            GS_SR_Manager GSManager = new GS_SR_Manager();
            lookupBattletag = GSManager.getUserBattletagByDiscordID(lookUpID);
            if (lookupBattletag ==  null) {
                channel.sendMessageFormat("%s's SR is not on file.").queue();
                return;
            }
            String lookUpName = event.getGuild().getMemberById(lookUpID).getEffectiveName();
            channel.sendMessageFormat("%s's stored battletag is %s", lookUpName, lookupBattletag).queue();
        } else {
            event.getChannel().sendTyping().queue();
            String authorBattletag;
            GS_SR_Manager GSManager = new GS_SR_Manager();
            authorBattletag = GSManager.getUserBattletagByDiscordID(authorID);
            if (authorBattletag != null) {
                channel.sendMessage("Your stored battletag is currently: " + authorBattletag).queue();
            } else {
                channel.sendMessage("Your battletag is not currently on file. Run ``.registerBattletag [battletag]`` to register it.").queue();
            }
        }
    }

    private void postDiscordNameFromBattletag(String[] contentString) {
        MessageChannel channel = event.getChannel();
        if (contentString.length > 1) {
            GS_SR_Manager GSManager = new GS_SR_Manager();
            String lookupDiscordID = GSManager.getUserDiscordIDByBattletag(contentString[1]);
            if (lookupDiscordID ==  null) {
                channel.sendMessageFormat("%s's SR is not on file.").queue();
                return;
            }
            event.getChannel().sendTyping().queue();
            String lookUpName = event.getGuild().getMemberById(lookupDiscordID).getEffectiveName();
            channel.sendMessageFormat("%s's stored Discord name is %s", contentString[1], lookUpName).queue();
        } else {
            channel.sendMessage("Make sure you enter a Battletag after the command").queue();
        }
    }

    private void updateSRDatabase() {
        MessageChannel channel = event.getChannel();
        GS_SR_Manager GSManager = new GS_SR_Manager();
        List<String> allBattletags = GSManager.getAllBattletags();
        for (String battletag : allBattletags) {
            event.getChannel().sendTyping().queue();
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
            GSManager.updateUserSRByBattletag(battletag, userSR);
        }
        channel.sendMessage("All of the battletags have been updated").queue();
    }

    private void checkSR(String[] contentString){
        String authorID = event.getAuthor().getId();
        MessageChannel channel = event.getChannel();
        if (contentString.length > 1) {
            String lookUpID;
            //checks if the member @'ed is using a nickname or not
            String nicknameTag = contentString[1].substring(2, 3);
            if (nicknameTag.equals("!")) {
                lookUpID = contentString[1].substring(3, contentString[1].length() - 1);
            } else {
                lookUpID = contentString[1].substring(2, contentString[1].length() - 1);
            }
            Integer lookUpSR;
            GS_SR_Manager GSManager = new GS_SR_Manager();
            lookUpSR = GSManager.getUserSRByDiscordID(lookUpID);
            event.getChannel().sendTyping().queue();
            if (lookUpSR ==  null) {
                channel.sendMessageFormat("%s's SR is not on file.").queue();
                return;
            }
            String lookUpName = event.getGuild().getMemberById(lookUpID).getEffectiveName();
            channel.sendMessageFormat("%s's stored SR is currently %s", lookUpName, lookUpSR).queue();
        } else {
            Integer authorSR;
            GS_SR_Manager GSManager = new GS_SR_Manager();
            authorSR = GSManager.getUserSRByDiscordID(authorID);
            event.getChannel().sendTyping().queue();
            if (authorSR != null) {
                channel.sendMessage("Your stored SR is currently: " + authorSR).queue();
            } else {
                channel.sendMessage("Your SR is not currently on file. Run ``.registerBattletag [battletag]`` to register it.").queue();
            }
        }
    }

    private void getLeaderboard(){
        GS_SR_Manager GSManager = new GS_SR_Manager();
        List<SR_DatabaseUser> databaseData = GSManager.getFullDatabase();
        Comparator<SR_DatabaseUser> databaseComparator = Comparator.comparing(SR_DatabaseUser::getSR);
        databaseData.sort(databaseComparator);
        Collections.reverse(databaseData);
        StringBuilder leaderboardString = new StringBuilder();
        leaderboardString.append("**=====================**\n");
        for (SR_DatabaseUser user : databaseData) {
            event.getChannel().sendTyping().queue();
            String username = event.getGuild().getMemberById(user.getDiscordID()).getEffectiveName();
            String battletag = user.getBattletag();
            String userSr = user.getSR().toString();
            leaderboardString
                    .append(username)
                    .append("** | **")
                    .append(userSr)
                    .append("** | **")
                    .append(battletag)
                    .append("\n");
        }
        leaderboardString.append("**=====================**");
        event.getChannel().sendMessage(leaderboardString.toString()).queue();
    }

    private String addToFile(String userDiscordName, String userID, String battletag, String userSR) {
        GS_SR_Manager GSManager = new GS_SR_Manager();
        boolean added = GSManager.addUserToDatabase(userDiscordName, userID, battletag, userSR);
        if (added) {
            System.out.printf("%s has registered %s as their Battletag with an SR of %s", userID, battletag, userSR);
            return "added to file";
        } else {
            return "already on file";
        }
    }

    private String getBattletagFromDiscordID(String discordID) {
        GS_SR_Manager GSManager = new GS_SR_Manager();
        return GSManager.getUserBattletagByDiscordID(discordID);
    }
}
