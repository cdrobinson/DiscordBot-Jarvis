import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GS_SRManager {

    private static Sheets service;
    private static String spreadsheetId;

    GS_SRManager(){
        GS_Manager GSManager = new GS_Manager();
        service = GSManager.getSheet();
        spreadsheetId = GSManager.getSpreadsheetId();
    }

    List<SRDatabaseUser> getFullSRDatabase(){
        final String readRange = "SRTracking!A2:D";
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, readRange)
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
            List<SRDatabaseUser> databaseValues = new ArrayList<>();
            for (List<Object> row : values) {
                SRDatabaseUser srDatabaseUser = new SRDatabaseUser(
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
        final String range = "SRTracking!A2:D";
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            values = response.getValues();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error getting values from the spreadsheet while looking up by Battletag");
        }
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                if (row.get(2).equals(userBattletag)) {
                    row.set(3, newSR);
                    ValueRange body = new ValueRange().setValues(values);
                    try {
                        service.spreadsheets().values().update(spreadsheetId, range, body)
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
        final String readRange = "SRTracking!A2:D";
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, readRange)
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
            List<String> allBattletags = new ArrayList<>();
            for (List<Object> row : values) {
                allBattletags.add(row.get(2).toString());
            }
            return allBattletags;
        }
    }

    String getUserSRByDiscordID(String userDiscordID) {
        final String readRange = "SRTracking!A1:D";
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, readRange)
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
            String storedSR = "";
            for (List<Object> row : values) {
                if (row.get(1).equals(userDiscordID)) {
                    storedSR = row.get(3).toString();
                }
            }
            if (storedSR.equals("")) {
                System.out.printf("There is currently no record stored for user [%s]\n", userDiscordID);
                return null;
            } else {
                System.out.printf("The stored SR for user [%s] is [%s]\n", userDiscordID, storedSR);
                return storedSR;
            }
        }
    }

    boolean addUserToSRTracking(String discordUsername, String discordID, String battletag, String sr) {
        if (getUserSRByDiscordID(discordID) == null) {
            final String writeRange = "SRTracking!A2:D";
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
                result = service.spreadsheets().values().append(spreadsheetId, writeRange, body)
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
