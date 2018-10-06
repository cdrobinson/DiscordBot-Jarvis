import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class SRTracker {
    //HashMap contains <DiscordUserID, <["sr", UserSR], ["battletag", UserBattletag]>>
    private HashMap<String, HashMap<String, String>> srMap;
    private JDA jda;
    private TextChannel srTrackingChannel;

    SRTracker(JDA jda) {
        ConfigManager cm = new ConfigManager();
        this.jda = jda;
        this.srTrackingChannel = jda.getGuildById(cm.getProperty("guildID")).getTextChannelsByName(cm.getProperty("srTrackingChannelName"), true).get(0);
        this.srMap = new HashMap<>();
        loadSRHistory();
        final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(this::updateSRMapFromOnline, 0, 15, TimeUnit.MINUTES);
    }

    private void updateSRMapFromOnline() {
        FileManager fileManager = new FileManager();
        for (Entry<String, HashMap<String, String>> userMap: this.srMap.entrySet()) {
            String battletag = userMap.getValue().get("battletag");
            String userSR = ProfileReader.getSR(battletag);
            assert userSR.length() < 5;
            String oldUserSR = this.srMap.get(userMap.getKey()).put("sr", userSR);
            assert oldUserSR != null;
            if (!oldUserSR.equals(userSR)) {
                notifyUpdatedSR(userMap.getKey(), oldUserSR, userSR);
            }
        }
        ConfigManager cm = new ConfigManager();
        fileManager.writeToTextFile(this.srMap.toString(), cm.getProperty("guildName") + "_SRHistory.txt");
        System.out.println("SR Updated");
    }

    private void notifyUpdatedSR(String userID, String oldSR, String newSR) {
        ConfigManager cm = new ConfigManager();
        String authorAsMention = jda.getGuildById(cm.getProperty("guildID")).getMemberById(userID).getAsMention();
        SRReporter srReporter = new SRReporter();
        Integer newSRInt = Integer.valueOf(newSR);
        Integer oldSRInt = Integer.valueOf(oldSR);
        String srReport = srReporter.build(authorAsMention, "SR", "New", newSRInt, "Previous", oldSRInt, newSRInt - oldSRInt);
        srTrackingChannel.sendMessage(srReport).queue((message -> {
            if (newSRInt - oldSRInt > 0) {
                message.addReaction("\uD83D\uDC4D").queue();
                message.addReaction("\uD83D\uDC4C").queue();
            } else if (newSRInt - oldSRInt < 0) {
                message.addReaction("\uD83D\uDC4E").queue();
                message.addReaction("\uD83D\uDE22").queue();
            } else {
                message.addReaction("\uD83D\uDE10").queue();
                message.addReaction("\uD83E\uDD37").queue();
            }
        }));
    }

    private void addUserToMap(String userID, String battletag, String userSR) {
        HashMap<String, String> userDetails = new HashMap<>();
        userDetails.put("battletag", battletag);
        userDetails.put("sr", userSR);
        srMap.put(userID, userDetails);
        ConfigManager cm = new ConfigManager();
        FileManager fileManager = new FileManager();
        fileManager.writeToTextFile(this.srMap.toString(), cm.getProperty("guildName") + "_SRHistory.txt");
        System.out.printf("%s has registered %s as their Battletag with an SR of %s", userID, battletag, userSR);
    }

    /*
    String getLeaderboard(Guild guild) {
        Set<Entry<String, HashMap<String, String>>> set = this.srMap.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<>(set);
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        String leaderBoard = "=====================\r";
        for(Map.Entry<String, Integer> entry:list) {
            try {
                leaderBoard = leaderBoard.concat(guild.getMemberById(entry.getKey()).getEffectiveName() + " - " + entry.getValue() + "\r");
            } catch (NullPointerException e) {
                System.out.printf("There was an error getting the effective name for %s", entry.getKey());
            }
        }
        leaderBoard = leaderBoard.concat("=====================");
        return leaderBoard;
    }*/

    private void loadSRHistory() {
        FileManager fileManager = new FileManager();
        ConfigManager cm = new ConfigManager();
        String fileContent = fileManager.readFromTextFile(cm.getProperty("guildName") + "_SRHistory.txt");
        if (fileContent != null) {
            HashMap<String, HashMap<String, String>> parsedContent = new HashMap<>();
            if(fileContent.length() > 2) {
                fileContent = fileContent.substring(1, fileContent.length() - 1);
                fileContent = fileContent.replaceAll("}, ", "}|");
                String[] contentAsList = fileContent.split("\\|");
                for (String listEntry : contentAsList) {
                    String[] userInfo = listEntry.split("=\\{");
                    HashMap<String, String> specificsMap = new HashMap<>();
                    userInfo[1] = userInfo[1].substring(0, userInfo[1].length()-1);
                    String[] userSpecifics = userInfo[1].split(", ");
                    for (String userSpecific : userSpecifics) {
                        String [] details = userSpecific.split("=");
                        specificsMap.put(details[0], details[1]);
                    }
                    parsedContent.put(userInfo[0], specificsMap);
                }
            }
            this.srMap = parsedContent;
        } else {
            this.srMap = new HashMap<>();
        }
    }

    private String getPlayerSR(String userID) {
        updateSRMapFromOnline();
        HashMap<String, String> userMap = this.srMap.get(userID);
        if (userMap != null) {
            return userMap.get("sr");
        } else {
            return null;
        }
    }

    void parseCommand(String[] contentString, MessageReceivedEvent event, SRSession srSession) {
        String authorID = event.getAuthor().getId();
        SRReporter srReporter = new SRReporter();
        MessageChannel channel = event.getChannel();
        switch (contentString[0].toLowerCase()) {
            case "!sr":
                try {
                    if (contentString[1] != null) {
                        String lookUpID;
                        //checks if the member @'ed is using a nickname or not
                        String nicknameTag = contentString[1].substring(2, 3);
                        if (nicknameTag.equals("!")) {
                            lookUpID = contentString[1].substring(3, contentString[1].length() - 1);
                        } else {
                            lookUpID = contentString[1].substring(2, contentString[1].length() - 1);
                        }
                        String lookUpSR = getPlayerSR(lookUpID);
                        if (lookUpSR == null) {
                            channel.sendMessageFormat("%s is not tracking their SR").queue();
                            break;
                        }
                        String lookUpName = event.getGuild().getMemberById(lookUpID).getEffectiveName();
                        channel.sendMessageFormat("%s's SR is currently %s", lookUpName, lookUpSR).queue();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    String authorSR = getPlayerSR(authorID);
                    if (authorSR != null) {
                        channel.sendMessage("I'm not sure why you're looking up your own SR but here it is: " + authorSR).queue();
                    } else {
                        channel.sendMessage("Why would you look up your own SR and not even have it tracked?").queue();
                    }
                }
                break;
            case "!leaderboard":
                channel.sendMessage("The leaderboard feature is currently under construction. Thank you for your patience.").queue();
                break;
            case "!srtrack":
                if (contentString.length == 1) {
                    channel.sendMessage("Please enter a battletag after the command ``!srTrack [battletag]``").queue();
                    break;
                }
                channel.sendMessage("Registering....").queue();
                String userSR = ProfileReader.getSR(contentString[1]);
                try {
                    Integer.valueOf(userSR);
                    addUserToMap(event.getAuthor().getId(), contentString[1], userSR);
                    channel.sendMessageFormat("You have registered %s as your battletag with a current SR of %s", contentString[1], userSR).queue();
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessageFormat("There was error registering your Battletag: %s", userSR).queue();
                }
                break;
            case "!session":
                if (contentString.length == 1) {
                    channel.sendMessage("The session command you entered was invalid. Your options are [start, current, end].").queue();
                    break;
                }
                if (this.srMap.get(authorID) == null) {
                    channel.sendMessage("You are not currently tracking your SR. Please use ``!srTrack [battletag]`` to track your SR.").queue();
                    break;
                }
                Integer currentSRInt;
                try {
                    String currentSR = getPlayerSR(authorID);
                    if (currentSR == null) {
                        channel.sendMessage("You are not currently tracking your SR. Please use ``!srTrack [battletag]`` to track your SR.").queue();
                        break;
                    }
                    currentSRInt = Integer.valueOf(currentSR);
                } catch (NumberFormatException e) {
                    channel.sendMessage("There was an error getting your SR. Please contact BattlemanMK2").queue();
                    break;
                }
                Integer storedSR = srSession.getStoredSR(authorID);
                switch (contentString[1]) {
                    case "start":
                        Boolean started = srSession.startSession(authorID, currentSRInt);
                        if (started) {
                            channel.sendMessage("Starting a session for " + event.getAuthor().getAsMention() + " with a starting SR of " + currentSRInt).queue();
                        } else {
                            channel.sendMessageFormat("There is already a session for %s with a starting SR of %s", event.getAuthor().getAsMention(), storedSR).queue();
                        }
                        break;
                    case "current":
                        if (srSession.isSessionRunning(authorID)) {
                            channel.sendMessage(srReporter.build(event.getAuthor().getAsMention(), "Session Details", "Starting", storedSR,
                                    "Current", currentSRInt, (currentSRInt - storedSR))).queue();
                        } else {
                            channel.sendMessage("You don't have a session going right now. Type \"!session start\" to begin one.").queue();
                        }
                        break;
                    case "end":
                        if (srSession.isSessionRunning(authorID)) {
                            channel.sendMessage(srReporter.build(event.getAuthor().getAsMention(), "Session Details", "Starting", storedSR,
                                    "Ending", currentSRInt, (currentSRInt - storedSR))).queue();
                            srSession.endSession(authorID);
                        } else {
                            channel.sendMessage("You don't have a session going right now. Type \"!session start\" to begin one.").queue();
                        }
                        break;
                    default:
                        channel.sendMessage("The session command you entered was invalid. Your options are [start, current, end].").queue();
                        break;
                }
                break;

        }
    }
}
