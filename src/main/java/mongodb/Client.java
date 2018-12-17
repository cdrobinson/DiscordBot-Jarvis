/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Client {

    private MongoCollection<Document> collection;
    private MongoClient mongoClient;

    public Client(String uriString, String databaseName, String collectionName) {
        MongoClientURI uri = new MongoClientURI(uriString);
        this.mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void endConnection() {
        mongoClient.close();
    }
}
