import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SRTracker implements Runnable {
    private MessageReceivedEvent event;

    SRTracker(MessageReceivedEvent event) {
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
                updateSRDatabase();
                break;
            case "!tracksr":
                registerBattletag(contentString);
                break;
            case "!sr":
                checkSR(contentString);
                break;
            case "!leaderboard":
                getLeaderboard();
                break;
            default:
                break;
        }
    }

    private void getLeaderboard(){
        GS_SRManager gs_srManager = new GS_SRManager();
        List<SRDatabaseUser> databaseData = gs_srManager.getFullSRDatabase();
        Comparator<SRDatabaseUser> databaseComparator = Comparator.comparing(SRDatabaseUser::getSR);
        databaseData.sort(databaseComparator);
        Collections.reverse(databaseData);
        StringBuilder leaderboardString = new StringBuilder();
        leaderboardString.append("**=====================**\n");
        for (SRDatabaseUser user : databaseData) {
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

    private void updateSRDatabase() {
        MessageChannel channel = event.getChannel();
        GS_SRManager gs_srManager = new GS_SRManager();
        List<String> allBattletags = gs_srManager.getAllBattletags();
        for (String battletag : allBattletags) {
            OverwatchProfile overwatchProfile = new OverwatchProfile(battletag);
            System.out.println(overwatchProfile.getSR());
            gs_srManager.updateUserSRByBattletag(battletag, overwatchProfile.getSR());
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
            String lookUpSR;
            GS_SRManager gs_srManager = new GS_SRManager();
            lookUpSR = gs_srManager.getUserSRByDiscordID(lookUpID);
            if (lookUpSR ==  null) {
                channel.sendMessageFormat("%s's SR is not on file.").queue();
                return;
            }
            String lookUpName = event.getGuild().getMemberById(lookUpID).getEffectiveName();
            channel.sendMessageFormat("%s's stored SR is currently %s", lookUpName, lookUpSR).queue();
        } else {
            String authorSR;
            GS_SRManager gs_srManager = new GS_SRManager();
            authorSR = gs_srManager.getUserSRByDiscordID(authorID);
            if (authorSR != null) {
                channel.sendMessage("Your stored SR is currently: " + authorSR).queue();
            } else {
                channel.sendMessage("Your SR is not currently on file. Run ``!srTrack [battletag]`` to register it.").queue();
            }
        }
    }

    private void registerBattletag(String[] contentString){
        MessageChannel channel = event.getChannel();
        if (contentString.length == 1) {
            channel.sendMessage("Please enter a battletag after the command ``!srTrack [battletag]``").queue();
            return;
        }
        channel.sendMessage("Registering....").queue();
        channel.sendTyping().queue();
        OverwatchProfile overwatchProfile = new OverwatchProfile(contentString[1]);
        String userSR = overwatchProfile.getSR();
        try {
            Integer.valueOf(userSR);
            String response = addToFile(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(),
                    event.getAuthor().getId(), contentString[1], userSR);
            switch (response) {
                case "added to file":
                    channel.sendMessageFormat("You have registered %s as your battletag with a current SR of %s", contentString[1], userSR).queue();
                    break;
                case "already on file":
                    channel.sendMessageFormat("You have already registered %s as your battletag", contentString[1]).queue();
                    break;
                default:
                    channel.sendMessage("There was an error saving your registration to file. Please try again. If the problem persists, contact an admin so we can resolve the issue").queue();
                    break;
            }
        } catch (NumberFormatException e) {
            channel.sendMessageFormat("An error occured while looking up your SR: ```%s```", userSR).queue();
        }
    }

    private String addToFile(String userDiscordName, String userID, String battletag, String userSR) {
        GS_SRManager gs_srManager = new GS_SRManager();
        boolean added = gs_srManager.addUserToSRTracking(userDiscordName, userID, battletag, userSR);
        if (added) {
            System.out.printf("%s has registered %s as their Battletag with an SR of %s", userID, battletag, userSR);
            return "added to file";
        } else {
            return "already on file";
        }
    }
}
