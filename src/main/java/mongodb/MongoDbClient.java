/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDbClient {

    private MongoClient mongoClient;
    private static final String uriString = "mongodb+srv://Jarvis:XVM1nCrfotM7tP99@frontline-izf18.mongodb.net/playScheduler?retryWrites=true";

    public MongoDbClient() {
        MongoClientURI uri = new MongoClientURI(uriString);
        this.mongoClient = new MongoClient(uri);
    }

    public MongoCollection<Document> getCollection(MongoDatabase database, String collectionName) {
        return database.getCollection(collectionName);
    }

    public MongoDatabase getDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }

    public void endConnection() {
        mongoClient.close();
    }
}
