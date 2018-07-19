import java.util.HashMap;

class SRSession {

    private HashMap<String, Integer> srSessionMap;

    SRSession() {
        this.srSessionMap = new HashMap<>();
    }

    void startSession(String userID, Integer startingSR) {
        srSessionMap.put(userID, startingSR);
    }

    Integer endSession(String userID) {
        Integer startingSR = srSessionMap.get(userID);
        srSessionMap.remove(userID);
        return startingSR;
    }

    HashMap<String, Integer> getHistory() {
        return this.srSessionMap;
    }

    void loadSessions(HashMap<String, Integer> srSessions) {
        this.srSessionMap = srSessions;
    }

    Integer getStartingSR(String userID) {
        return this.srSessionMap.get(userID);
    }
}
