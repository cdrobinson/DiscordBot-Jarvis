import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;

class MongoDB_SR_Manager {

    private MongoDB_Manager mongoDB_manager;
    private MongoCollection<Document> collection;

    MongoDB_SR_Manager() {
        this.mongoDB_manager = new MongoDB_Manager("mongodb+srv://Jarvis:XVM1nCrfotM7tP99@frontline-izf18.mongodb.net/test?retryWrites=true",
                "Frontline", "SR");
        this.collection = mongoDB_manager.getCollection();
    }

    Boolean addUserToDatabase(String discordName, String discordID, String battletag, Integer sr,
                              String profileURL, String portraitURL, String rankIconURL) {
        Document userInformation = new Document();
        userInformation.append("Discord Name", discordName);
        userInformation.append("Discord ID", discordID);
        userInformation.append("Battletag", battletag);
        userInformation.append("SR", sr);
        userInformation.append("Profile URL", profileURL);
        userInformation.append("Portrait URL", portraitURL);
        userInformation.append("Rank Icon URL", rankIconURL);

        if (collection.find(eq("Discord ID", discordID)).first() == null) {
            collection.insertOne(userInformation);
            return true;
        } else {
            System.out.println("Entry already in DB");
            return false;
        }
    }

    Boolean updateUserByBattletag(String battletag, String userSR, String profileURL, String portraitURL, String rankIconURL) {
        try {
            Integer userSRInt = Integer.parseInt(userSR);
            return collection.updateOne(eq("Battletag", battletag),
                    combine(set("SR", userSRInt), currentDate("lastUpdated"), set("Profile URL", profileURL),
                            set("Portrait URL", portraitURL), set("Rank Icon URL", rankIconURL))).wasAcknowledged();
        } catch (NumberFormatException e) {
            return collection.updateOne(eq("Battletag", battletag),
                    combine(set("SR", userSR), currentDate("lastUpdated"), set("Profile URL", profileURL),
                            set("Portrait URL", portraitURL), set("Rank Icon URL", rankIconURL))).wasAcknowledged();
        }
    }

    ArrayList<String> getAllBattletags() {
        ArrayList<String> allBattletags = new ArrayList<>();
        FindIterable<Document> foundSet = collection.find().projection(include("Battletag"));
        for (Document document : foundSet) {
            allBattletags.add(document.getString("Battletag"));
        }
        return allBattletags;
    }

    String getBattletagByDiscordId(String discordId) {
        FindIterable<Document> foundSet = collection.find(eq("Discord ID", discordId)).projection(include("Battletag"));
        return Objects.requireNonNull(foundSet.first()).getString("Battletag");
    }

    String getDiscordIdByBattletag(String battletag) {
        FindIterable<Document> foundSet = collection.find(eq("Battletag", battletag)).projection(include("Discord ID"));
        return Objects.requireNonNull(foundSet.first()).getString("Discord ID");
    }

    Integer getUserSrByDiscordId(String discordId) {
        FindIterable<Document> foundSet = collection.find(eq("Discord ID", discordId)).projection(include("SR"));
        return Objects.requireNonNull(foundSet.first()).getInteger("SR");
    }

    SR_DatabaseUser getUserDataByDiscordId(String discordId) {
        FindIterable<Document> foundSet = collection.find(eq("Discord ID", discordId));
        Document document = foundSet.first();
        if (document != null) {
            return new SR_DatabaseUser(
                    document.getString("Discord Name"),
                    document.getString("Discord ID"),
                    document.getString("Battletag"),
                    document.getInteger("SR"),
                    document.getString("Profile URL"),
                    document.getString("Portrait URL"),
                    document.getString("Rank Icon URL"));
        }
        return null;
    }

    List<SR_DatabaseUser> getFullDatabase() {
        ArrayList<SR_DatabaseUser> databaseData = new ArrayList<>();
        FindIterable<Document> foundSet = collection.find();
        for (Document document : foundSet) {
            //String discordName, String discordID, String battletag, Integer SR
            SR_DatabaseUser user = new SR_DatabaseUser(
                    document.getString("Discord Name"),
                    document.getString("Discord ID"),
                    document.getString("Battletag"),
                    document.getInteger("SR"));
            databaseData.add(user);
        }
        return databaseData;
    }

    void endConnection() {
        mongoDB_manager.endConnection();
    }
}
