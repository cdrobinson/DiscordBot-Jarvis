/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package featureRequester;

import bot.configuration.ConfigManager;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import google.sheets.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GoogleSheetsConnector {

    private static Sheets service;
    private static String spreadsheetId;
    private static final String featuresRange = "FeatureRequests!A2:D";
    private static final String detailsRange = "FeatureRequests!E1:H1";

    GoogleSheetsConnector(){
        spreadsheetId = new ConfigManager().getProperty("featureRequestSheetId");
        Client gsManager = new Client();
        service = gsManager.getSheet();
    }

    List<Request> getAllFeatureRequests(){
        ValueRange response;
        List<List<Object>> values = null;
        try {
            response = service.spreadsheets().values()
                    .get(spreadsheetId, featuresRange)
                    .execute();
            values = response.getValues();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error getting values from the spreadsheet.");
        }
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return null;
        } else {
            List<Request> databaseValues = new ArrayList<>();
            for (List<Object> row : values) {
                Request frRequest = new Request(
                        row.get(0).toString(),
                        row.get(1).toString(),
                        row.get(3).toString());
                databaseValues.add(frRequest);
            }
            return databaseValues;
        }
    }

    void addRequest(String requesterID, String requesterName, String request) {
        List<List<Object>> newValues = new ArrayList<>();
        List<Object> tempList = new ArrayList<>();
        tempList.add("Open");
        tempList.add(requesterID);
        tempList.add(requesterName);
        tempList.add(request);
        newValues.add(tempList);

        ValueRange body = new ValueRange().setValues(newValues);
        String valueInputOption = "RAW"; //RAW or USER_ENTERED
        AppendValuesResponse result;
        try {
            result = service.spreadsheets().values().append(spreadsheetId, featuresRange, body)
                    .setValueInputOption(valueInputOption)
                    .execute();
            System.out.printf("%d cells updated.\n", result.getUpdates().getUpdatedCells());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error adding a request to the database.");
        }
    }


    void updatePostDetails(String latestPinnedMessageId, String frChannelName) {
        List<List<Object>> values = getDatabaseValues(detailsRange);
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                row.set(1, latestPinnedMessageId);
                row.set(3, frChannelName);
            }
            setDatabaseValues(values, detailsRange);
        }
    }

    Map<String, String> getPostDetails() {
        List<List<Object>> values = getDatabaseValues(detailsRange);
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return null;
        } else {
            Map<String, String> postDetailList = new HashMap<>();
            for (List<Object> row : values) {
                postDetailList.put("ID",row.get(1).toString());
                postDetailList.put("Channel",row.get(3).toString());
            }
            return postDetailList;
        }
    }

    List<List<Object>> getRequestValues(String identifier) {
        return getDatabaseValues("FeatureRequests!A" + identifier + ":D" + identifier);
    }

    void setRequestValues(String identifier, List<List<Object>> values) {
        setDatabaseValues(values, "FeatureRequests!A" + identifier + ":D" + identifier);
    }

    private List<List<Object>> getDatabaseValues(String readRange) {
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
            return values;
        }
    }

    private void setDatabaseValues(List<List<Object>> values, String writingRange) {
        ValueRange body = new ValueRange().setValues(values);
        try {
            service.spreadsheets().values().update(spreadsheetId, writingRange, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error updating the values");
        }
    }
}
