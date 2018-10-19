import java.util.HashMap;

class SRSession {
    private HashMap<String, Integer> srSessionMap;
    private FileManager fileManager;

    SRSession() {
        this.srSessionMap = new HashMap<>();
        this.fileManager = new FileManager();
        loadSessions(fileManager.parseStorageFile(fileManager.readFromTextFile("SRSessions.txt")));
    }

    Boolean startSession(String userID, Integer startingSR) {
        if (srSessionMap.get(userID) == null) {
            srSessionMap.put(userID, startingSR);
            fileManager.writeToTextFile(srSessionMap.toString(), "SRSessions.txt");
            return true;
        } else {
            return false;
        }
    }

    Boolean isSessionRunning(String userID) {
        return srSessionMap.get(userID) != null;
    }

    void endSession(String userID) {
        srSessionMap.remove(userID);
        fileManager.writeToTextFile(srSessionMap.toString(), "SRSessions.txt");
    }

    private void loadSessions(HashMap<String, Integer> srSessions) {
        this.srSessionMap = srSessions;
    }

    Integer getStoredSR(String authorID) {
        return this.srSessionMap.get(authorID);
    }
}
