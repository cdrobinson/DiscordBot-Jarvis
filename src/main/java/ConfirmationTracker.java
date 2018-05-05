import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

class ConfirmationTracker {

    private HashMap<User, String> confirmationMap;

    ConfirmationTracker() {
        this.confirmationMap = new HashMap();
    }

    void addConfirmation(User username, String confirmationCode) {
        this.confirmationMap.put(username, confirmationCode);
    }

    String checkListByUser(User username){
        return this.confirmationMap.get(username);
    }

    void removeConfirmation(User username) {
        this.confirmationMap.remove(username);
    }
}
