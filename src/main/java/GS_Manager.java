import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


class GS_Manager {
    private static final String APPLICATION_NAME = "Overwatch SR Bot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final Set<String> SCOPES = Collections.singleton(SheetsScopes.SPREADSHEETS);
    private static Sheets service;
    //Stored in the resources folder
    private static final String CREDENTIALS_FILE_PATH = "/serviceAccountCredentials.json";
    //This is the string between /d/ and /edit...
    private static final String spreadsheetId = "1pnXicMQPwG0J4VSeJp_CjSMMiAe0D9FM7_cwTDFarek";
    //Must have editor access to the Google Sheet
    private static final String serviceAccountUser = "discordbot@overwatch-sr-bot-1542389554492.iam.gserviceaccount.com";
    private static final String sheetRange = "SRTracking!A2:D";

    GS_Manager() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential googleCredential = GoogleCredential
                    .fromStream(GS_Manager.class.getResourceAsStream(CREDENTIALS_FILE_PATH))
                    .createScoped(SCOPES);
            GoogleCredential.Builder credentialBuilder = new GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountScopes(SCOPES)
                    .setServiceAccountId(googleCredential.getServiceAccountId())
                    .setServiceAccountPrivateKey(googleCredential.getServiceAccountPrivateKey())
                    .setServiceAccountPrivateKeyId(googleCredential.getServiceAccountPrivateKeyId())
                    .setTokenServerEncodedUrl(googleCredential.getTokenServerEncodedUrl())
                    .setServiceAccountUser(serviceAccountUser);
            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentialBuilder.build()).setApplicationName(APPLICATION_NAME).build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            System.out.println("There was an error creating the GS_Manager");
        }
    }

    List<SR_DatabaseUser> getFullDatabase(){
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
            List<SR_DatabaseUser> databaseValues = new ArrayList<>();
            for (List<Object> row : values) {
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
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, sheetRange)
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
            List<String> allBattletags = new ArrayList<>();
            for (List<Object> row : values) {
                allBattletags.add(row.get(2).toString());
            }
            return allBattletags;
        }
    }

    String getUserSRByDiscordID(String userDiscordID) {
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

    boolean addSRTracking(String discordUsername, String discordID, String battletag, String sr) {
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

    String getUserBattletagByDiscordID(String lookUpID) {
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
            String storedBattletag = "";
            for (List<Object> row : values) {
                if (row.get(1).equals(lookUpID)) {
                    storedBattletag = row.get(2).toString();
                }
            }
            if (storedBattletag.equals("")) {
                System.out.printf("There is currently no record for user [%s]\n", lookUpID);
                return null;
            } else {
                System.out.printf("The stored Battletag for user [%s] is [%s]\n", lookUpID, storedBattletag);
                return storedBattletag;
            }
        }
    }

    String getUserDiscordIDByBattletag(String lookUpID) {
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
            String storedDiscordID = "";
            for (List<Object> row : values) {
                if (row.get(2).equals(lookUpID)) {
                    storedDiscordID = row.get(1).toString();
                }
            }
            if (storedDiscordID.equals("")) {
                System.out.printf("There is currently no record for user [%s]\n", lookUpID);
                return null;
            } else {
                System.out.printf("The stored Discord ID for user [%s] is [%s]\n", lookUpID, storedDiscordID);
                return storedDiscordID;
            }
        }
    }
}