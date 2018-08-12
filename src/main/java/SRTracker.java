import net.dv8tion.jda.core.entities.Guild;
import java.util.*;
import java.util.Map.Entry;


class SRTracker {

    private HashMap<String, Integer> srMap;

    SRTracker() {
        this.srMap = new HashMap<>();
        FileManager fileManager = new FileManager();
        loadSRHistory(fileManager.parseStorageFile(fileManager.readFromTextFile("SRHistory.txt")));
    }

    HashMap<String, Integer> updateSR(String userID, Integer updatedSR) {
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

    HashMap<String, Integer> getHistory() {
        return this.srMap;
    }

    String getLeaderboard(Guild guild) {
        Set<Entry<String, Integer>> set = this.srMap.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<>(set);
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        String leaderBoard = "```=====================\r";
        for(Map.Entry<String, Integer> entry:list){
            System.out.println(entry.getKey()+" ==== "+entry.getValue());
            leaderBoard = leaderBoard.concat(guild.getMemberById(entry.getKey()).getEffectiveName() + " - " + entry.getValue() + "\r");
        }
        leaderBoard = leaderBoard.concat("=====================```");
        return leaderBoard;
    }

    private void loadSRHistory(HashMap<String, Integer> srHistory) {
        this.srMap = srHistory;
    }

    Integer getPlayerSR(String userID) {
        return this.srMap.get(userID);
    }
}
