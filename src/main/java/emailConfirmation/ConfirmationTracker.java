/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package emailConfirmation;

import java.util.HashMap;

public class ConfirmationTracker {

    private HashMap<String, HashMap<String, String>> confirmationMap;
    private String emailList;

    public ConfirmationTracker() {
        this.confirmationMap = new HashMap<>();
        this.emailList = "";
    }

    public void addConfirmation(String userID, String confirmationCode, String userEmail) {
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("confirmation", confirmationCode);
        userInfo.put("email", userEmail);
        this.confirmationMap.put(userID, userInfo);
    }

    public String checkListByUser(String userID){
        //Returns the confirmation code
        return this.confirmationMap.get(userID).get("confirmation");
    }

    public String getUserEmail(String userID) {
        return this.confirmationMap.get(userID).get("email");
    }

    public void removeConfirmation(String userID) {
        this.confirmationMap.remove(userID);
    }

    public HashMap<String, HashMap<String, String>> getConfirmationMap() {
        return this.confirmationMap;
    }

    public void loadConfirmationMap(HashMap<String, HashMap<String, String>> confirmationMap) {
        this.confirmationMap = confirmationMap;
    }

    public void addEmail(String userEmail) {
        this.emailList = emailList + "\r" + userEmail;
    }

    public String getEmailList() {
        return this.emailList;
    }

    public void loadEmailList(String emailList) {
        this.emailList = emailList;
    }
}
