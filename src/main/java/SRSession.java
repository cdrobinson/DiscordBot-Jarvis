import java.util.HashMap;

class SRSession {
    private HashMap<String, Integer> srSessionMap;

    SRSession() {
        this.srSessionMap = new HashMap<>();
        FileManager fileManager = new FileManager();
        loadSessions(fileManager.parseStorageFile(fileManager.readFromTextFile("SRSessions.txt")));
    }

    void startSession(String userID, Integer startingSR) {
        srSessionMap.put(userID, startingSR);
    }

    void endSession(String userID) {
        srSessionMap.remove(userID);
    }

    HashMap<String, Integer> getHistory() {
        return this.srSessionMap;
    }

    private void loadSessions(HashMap<String, Integer> srSessions) {
        this.srSessionMap = srSessions;
    }

    Integer getStoredSR(String authorID) {
        return this.srSessionMap.get(authorID);
    }
}
