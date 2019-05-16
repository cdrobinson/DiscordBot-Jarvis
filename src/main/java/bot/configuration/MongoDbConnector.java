/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.configuration;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import mongodb.MongoDbClient;
import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

class MongoDbConnector {

    private MongoCollection<Document> collection;
    private MongoDbClient mongoDbClient = new MongoDbClient();

    MongoDbConnector() {
        this.collection = mongoDbClient.getCollection(mongoDbClient.getDatabase("Jarvis"), "Variables");
    }

    String getBotToken() {
        ArrayList<String> nowPlayingList = new ArrayList<>();
        FindIterable<Document> foundSet = collection.find(eq("Function", "BotToken")).projection(include("Token"));
        String token = "";
        for (Document document : foundSet) {
            token = document.getString("Token");
        }
        return token;
    }

    void endConnection() {
        mongoDbClient.endConnection();
    }
}
