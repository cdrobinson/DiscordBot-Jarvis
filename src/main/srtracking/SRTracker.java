import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.Map.Entry;

class SRTracker {

    private HashMap<String, Integer> srMap;

    SRTracker() {
        this.srMap = new HashMap<>();
        FileManager fileManager = new FileManager();
        loadSRHistory(fileManager.parseStorageFile(fileManager.readFromTextFile("SRHistory.txt")));
    }

    void parseSrUpdate(String content, SRTracker srTracker, MessageReceivedEvent event, FileManager fileManager){
        MessageChannel channel = event.getChannel();
        String[] input = content.split(" ");
        if (input.length == 1 && isInteger(content)) {
            Integer updatedSR = Integer.parseInt(content);
            HashMap<String, Integer> srHistory = srTracker.updateSR(event.getAuthor().getId(), updatedSR);
            String authorAsMention = event.getAuthor().getAsMention();
            SRReporter srReporter = new SRReporter();
            channel.sendMessage(srReporter.build(authorAsMention, "SR", "New", srHistory.get("New SR"),
                    "Previous", srHistory.get("Old SR"), srHistory.get("Difference"))).queue();
            if (srHistory.get("Difference") > 0) {
                event.getMessage().addReaction("\uD83D\uDC4D").queue();
                event.getMessage().addReaction("\uD83D\uDC4C").queue();
            } else if (srHistory.get("Difference") < 0) {
                event.getMessage().addReaction("\uD83D\uDC4E").queue();
                event.getMessage().addReaction("\uD83D\uDE22").queue();
            } else {
                event.getMessage().addReaction("\uD83D\uDE10").queue();
                event.getMessage().addReaction("\uD83E\uDD37").queue();
            }
            fileManager.writeToTextFile(srTracker.getHistory().toString(), "SRHistory.txt");
        }
    }

    private HashMap<String, Integer> updateSR(String userID, Integer updatedSR) {
        HashMap<String, Integer> srHistory = new HashMap<>();
        Integer oldSR = this.srMap.get(userID);
        if (oldSR == null) {
            this.srMap.put(userID, updatedSR);
            srHistory.put("Old SR", 0);
            srHistory.put("New SR", updatedSR);
            srHistory.put("Difference", 0);
            return srHistory;
        } else {
            this.srMap.put(userID, updatedSR);
            srHistory.put("Old SR", oldSR);
            srHistory.put("New SR", updatedSR);
            srHistory.put("Difference", (updatedSR-oldSR));
            return srHistory;
        }
    }

    private HashMap<String, Integer> getHistory() {
        return this.srMap;
    }

    String getLeaderboard(Guild guild) {
        Set<Entry<String, Integer>> set = this.srMap.entrySet();
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
    }

    private void loadSRHistory(HashMap<String, Integer> srHistory) {
        this.srMap = srHistory;
    }

    Integer getPlayerSR(String userID) {
        return this.srMap.get(userID);
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
}
