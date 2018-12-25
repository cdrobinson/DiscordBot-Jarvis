/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package google.sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;


public class Client {
    private static final String APPLICATION_NAME = "Jarvis";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final Set<String> SCOPES = Collections.singleton(SheetsScopes.SPREADSHEETS);
    private static Sheets service;
    //Stored in the resources folder
    private static final String CREDENTIALS_FILE_PATH = "/serviceAccountCredentials.json";
    //Spreadsheet ID is the string between /d/ and /edit...
    //Must have editor access to the Google Sheet
    private static final String serviceAccountUser = "discordbot@overwatch-sr-bot-1542389554492.iam.gserviceaccount.com";

    public Client() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential googleCredential = GoogleCredential
                    .fromStream(Client.class.getResourceAsStream(CREDENTIALS_FILE_PATH))
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
            System.out.println("There was an error creating the google.sheets.Client");
        }
    }

    public Sheets getSheet(){
        return service;
    }
}