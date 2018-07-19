
import java.util.HashMap;

class SRTracker {

    private HashMap<String, Integer> srMap;

    SRTracker() {
        this.srMap = new HashMap<>();
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

    void loadSRHistory(HashMap<String, Integer> srHistory) {
        this.srMap = srHistory;
    }

    Integer getPlayerSR(String userID) {
        return this.srMap.get(userID);
    }
}
