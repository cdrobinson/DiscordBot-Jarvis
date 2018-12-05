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

    Sheets getSheet(){
        return service;
    }

    String getSpreadsheetId() {
        return spreadsheetId;
    }
}