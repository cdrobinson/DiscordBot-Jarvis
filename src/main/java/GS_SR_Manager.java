import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GS_SR_Manager {

    private static Sheets service;
    private static String spreadsheetId;
    private static final String sheetRange = "SRTracking!A2:D";


    GS_SR_Manager(){
        GS_Manager GSManager = new GS_Manager();
        service = GSManager.getSheet();
        spreadsheetId = GSManager.getSpreadsheetId();
    }

    private List<List<Object>> getDatabaseValues() {
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, sheetRange)
                    .execute();
            values = response.getValues();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error getting values from the spreadsheet while looking up by Discord ID");
        }
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return null;
        } else {
            return values;
        }
    }

    List<SR_DatabaseUser> getFullDatabase(){
        List<List<Object>> values = getDatabaseValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return null;
        } else {
            List<SR_DatabaseUser> databaseValues = new ArrayList<>();
            for (List<Object> row : values) {
                //0: Discord Name, 1: Discord ID, 2: Battletag, 3: SR
                SR_DatabaseUser srDatabaseUser = new SR_DatabaseUser(
                        row.get(1).toString(),
                        row.get(2).toString(),
                        Integer.valueOf(row.get(3).toString())
                );
                databaseValues.add(srDatabaseUser);
            }
            return databaseValues;
        }
    }
    void updateUserSRByBattletag(String userBattletag, String newSR) {
        List<List<Object>> values = getDatabaseValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                if (row.get(2).equals(userBattletag)) {
                    row.set(3, newSR);
                    ValueRange body = new ValueRange().setValues(values);
                    try {
                        service.spreadsheets().values().update(spreadsheetId, sheetRange, body)
                                .setValueInputOption("RAW")
                                .execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.printf("There was an error updating the value for %s", userBattletag);
                    }
                }
            }
        }
    }

    List<String> getAllBattletags(){
        List<List<Object>> values = getDatabaseValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return null;
        } else {
            List<String> allBattletags = new ArrayList<>();
            for (List<Object> row : values) {
                allBattletags.add(row.get(2).toString());
            }
            return allBattletags;
        }
    }

    Integer getUserSRByDiscordID(String userDiscordID) {
        List<SR_DatabaseUser> databaseList = getFullDatabase();
        for (SR_DatabaseUser user: databaseList) {
            if (user.getDiscordID().equals(userDiscordID)) {
                return user.getUserSR();
            }
        }
        return null;
    }

    String getUserBattletagByDiscordID(String lookUpDiscordId) {
        List<SR_DatabaseUser> databaseList = getFullDatabase();
        for (SR_DatabaseUser user: databaseList) {
            if (user.getDiscordID().equals(lookUpDiscordId)) {
                return user.getBattletag();
            }
        }
        return null;
    }

    String getUserDiscordIDByBattletag(String lookUpBattletag) {
        List<SR_DatabaseUser> databaseList = getFullDatabase();
        for (SR_DatabaseUser user: databaseList) {
            if (user.getBattletag().equals(lookUpBattletag)) {
                return user.getDiscordID();
            }
        }
        return null;
    }

    boolean addUserToDatabase(String discordUsername, String discordID, String battletag, String sr) {
        if (getUserSRByDiscordID(discordID) == null) {
            List<List<Object>> newValues = new ArrayList<>();
            List<Object> tempList = new ArrayList<>();
            tempList.add(discordUsername);
            tempList.add(discordID);
            tempList.add(battletag);
            tempList.add(sr);
            newValues.add(tempList);

            ValueRange body = new ValueRange().setValues(newValues);
            String valueInputOption = "RAW"; //RAW or USER_ENTERED
            AppendValuesResponse result;
            try {
                result = service.spreadsheets().values().append(spreadsheetId, sheetRange, body)
                        .setValueInputOption(valueInputOption)
                        .execute();
                System.out.printf("%d cells updated.\n", result.getUpdates().getUpdatedCells());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("There was an error adding %s to the database", discordUsername);
                return false;
            }
        } else {
            System.out.printf("There is already a record for this user [%s]\n", discordUsername);
            return false;
        }
    }
}
