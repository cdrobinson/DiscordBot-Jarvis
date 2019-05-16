/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.utilities.nowPlayingScheduler;

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

    ArrayList<String> getNowPlayingList() {
        ArrayList<String> nowPlayingList = new ArrayList<>();
        FindIterable<Document> foundSet = collection.find(eq("Function", "NowPlaying")).projection(include("GameName"));
        for (Document document : foundSet) {
            nowPlayingList.add(document.getString("GameName"));
        }
        return nowPlayingList;
    }

    void endConnection() {
        mongoDbClient.endConnection();
    }
}
