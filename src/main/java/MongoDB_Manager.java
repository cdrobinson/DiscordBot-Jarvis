import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

class MongoDB_Manager {

    private MongoCollection<Document> collection;
    private MongoClient mongoClient;

    MongoDB_Manager(String uriString, String databaseName, String collectionName) {
        MongoClientURI uri = new MongoClientURI(uriString);
        this.mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
    }

    MongoCollection<Document> getCollection() {
        return collection;
    }

    void endConnection() {
        mongoClient.close();
    }
}
