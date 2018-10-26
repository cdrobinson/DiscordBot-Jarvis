import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
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
        final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(2);
        executorService.scheduleAtFixedRate(this::updateSRMapFromOnline, 0, 30, TimeUnit.MINUTES);
    }

    private void updateSRMapFromOnline() {
        FileManager fileManager = new FileManager();
        for (Entry<String, HashMap<String, String>> userMap: this.srMap.entrySet()) {
            String battletag = userMap.getValue().get("battletag");
            OverwatchProfile overwatchProfile = new OverwatchProfile(battletag);
            String userSR = overwatchProfile.getSR();
            assert userSR.length() < 5;
            String oldUserSR = this.srMap.get(userMap.getKey()).put("sr", userSR);
            assert oldUserSR != null;
            if (!oldUserSR.equals(userSR)) {
                notifyUpdatedSR(userMap.getKey(), oldUserSR, userSR);
            }
        }
        ConfigManager cm = new ConfigManager();
        fileManager.writeToTextFile(this.srMap.toString(), cm.getProperty("guildName") + "_SRHistory.txt");
        System.out.println("###User's SR information has been updated automatically");
    }

    private void notifyUpdatedSR(String userID, String oldSR, String newSR) {
        ConfigManager cm = new ConfigManager();
        String authorAsMention = jda.getGuildById(cm.getProperty("guildID")).getMemberById(userID).getAsMention();
        Integer newSRInt = Integer.valueOf(newSR);
        Integer oldSRInt = Integer.valueOf(oldSR);
        String srReport = buildSrReport(authorAsMention, "SR", "New", newSRInt, "Previous", oldSRInt, newSRInt - oldSRInt);
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

    private static <K,V extends Comparable<? super V>> SortedSet<Entry<K,V>> entriesSortedByValues(Map<K, V> map) {
        //https://stackoverflow.com/questions/2864840/treemap-sort-by-value
        SortedSet<Entry<K,V>> sortedEntries = new TreeSet<>((e1, e2) -> {
            int res = e2.getValue().compareTo(e1.getValue());
            return res != 0 ? res : 1;
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    private String getLeaderboard() {
        Map<String, Integer> userSrMap = new HashMap<>();
        for (Entry<String, HashMap<String, String>> set : this.srMap.entrySet()) {
            String userID = set.getKey();
            try {
                Integer userSR = Integer.valueOf(set.getValue().get("sr"));
                userSrMap.put(userID, userSR);
            } catch (NumberFormatException e) {
                System.out.printf("There was an error generating the leaderboard. Error: %s", e);
            }
        }
        SortedSet<Entry<String, Integer>> sortedMap = entriesSortedByValues(userSrMap);
        ConfigManager cm = new ConfigManager();
        Guild guild = jda.getGuildById(cm.getProperty("guildID"));
        String leaderBoard = "=====================\r";
        for(Entry<String, Integer> entry : sortedMap) {
            try {
                leaderBoard = leaderBoard.concat(guild.getMemberById(entry.getKey()).getEffectiveName() + " - " + entry.getValue() + "\r");
            } catch (NullPointerException e) {
                System.out.printf("There was an error getting the effective name for %s", entry.getKey());
            }
        }
        leaderBoard = leaderBoard.concat("=====================");
        return leaderBoard;
    }

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
                        String[] details = userSpecific.split("=");
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
        HashMap<String, String> userMap = this.srMap.get(userID);
        if (userMap != null) {
            return userMap.get("sr");
        } else {
            return null;
        }
    }

    void parseCommand(String[] contentString, MessageReceivedEvent event, SRSession srSession) {
        String authorID = event.getAuthor().getId();
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
                        channel.sendMessageFormat("%s's stored SR is currently %s", lookUpName, lookUpSR).queue();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    String authorSR = getPlayerSR(authorID);
                    if (authorSR != null) {
                        channel.sendMessage("Your stored SR is currently: " + authorSR).queue();
                    } else {
                        channel.sendMessage("Why would you look up your SR without tracking it first? Run ``!srTrack [battletag]``").queue();
                    }
                }
                break;
            case "!leaderboard":
                channel.sendMessage(getLeaderboard()).queue();
                break;
            case "!srtrack":
                if (contentString.length == 1) {
                    channel.sendMessage("Please enter a battletag after the command ``!srTrack [battletag]``").queue();
                    break;
                }
                channel.sendMessage("Registering....").queue();
                channel.sendTyping().queue();
                OverwatchProfile overwatchProfile = new OverwatchProfile(contentString[1]);
                String userSR = overwatchProfile.getSR();
                try {
                    Integer.valueOf(userSR);
                    addUserToMap(event.getAuthor().getId(), contentString[1], userSR);
                    channel.sendMessageFormat("You have registered %s as your battletag with a current SR of %s", contentString[1], userSR).queue();
                } catch (NumberFormatException e) {
                    channel.sendMessageFormat("There was error registering your Battletag: %s", userSR).queue();
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
                            channel.sendMessage(buildSrReport(event.getAuthor().getAsMention(), "Session Details", "Starting", storedSR,
                                    "Current", currentSRInt, (currentSRInt - storedSR))).queue();
                        } else {
                            channel.sendMessage("You don't have a session going right now. Type \"!session start\" to begin one.").queue();
                        }
                        break;
                    case "end":
                        if (srSession.isSessionRunning(authorID)) {
                            channel.sendMessage(buildSrReport(event.getAuthor().getAsMention(), "Session Details", "Starting", storedSR,
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
            case "!update":
                channel.sendTyping().queue();
                updateSRMapFromOnline();
                channel.sendMessage("I have updated the SR record to match the current SR's shown on Overwatch.com").queue();
                break;
        }
    }

    private String buildSrReport(String authorAsMention, String title, String cat1Title, Integer cat1Value, String cat2Title, Integer cat2Value, Integer cat3Value) {
        StringBuilder srReport = new StringBuilder();
        srReport.append(authorAsMention);
        srReport.append("'s ");
        srReport.append(title);
        srReport.append("\r------------------------\r");
        srReport.append(cat1Title);
        srReport.append(" SR: ");
        srReport.append(cat1Value);
        srReport.append("\r");
        srReport.append(cat2Title);
        srReport.append(" SR: ");
        srReport.append(cat2Value);
        srReport.append("\r");
        srReport.append("Difference: ");
        if (cat3Value > 0) {
            srReport.append("+");
        }
        srReport.append(cat3Value);
        srReport.append("\r------------------------\r");
        return srReport.toString();
    }
}
