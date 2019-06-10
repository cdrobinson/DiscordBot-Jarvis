/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package emailConfirmation;

import bot.configuration.ConfigManager;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import mongodb.MongoDbClient;
import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

class MongoDbConnector {

    private MongoDbClient mongoDbClient;
    private MongoCollection<Document> collection;

    MongoDbConnector() {
        ConfigManager cm = new ConfigManager();
        this.mongoDbClient = new MongoDbClient();
        this.collection = mongoDbClient.getCollection(mongoDbClient.getDatabase(cm.getProperty("ConfirmedDatabase")), cm.getProperty("ConfirmedCollection"));
    }

    boolean storeStudentInfo(Student student) {
        Document studentDocument = new Document();
        studentDocument.append("DiscordID", student.getDiscordID());
        studentDocument.append("DiscordName", student.getDiscordName());
        studentDocument.append("SchoolEmail", student.getSchoolEmail());
        studentDocument.append("ConfirmationCode", student.getConfirmationCode());

        if (collection.find(eq("DiscordID", student.getDiscordID())).first() == null) {
            collection.insertOne(studentDocument);
        } else {
            collection.replaceOne(eq("DiscordID", student.getDiscordID()), studentDocument);
        }
        return true;
    }

    ArrayList<Student> getAllRegisteredStudents() {
        ArrayList<Student> allStudents = new ArrayList<>();
        FindIterable<Document> foundSet = collection.find();
        for (Document document : foundSet) {
            String discordID = document.getString("DiscordID");
            String discordName = document.getString("DiscordName");
            String schoolEmail = document.getString("SchoolEmail");
            String confirmationCode = document.getString("ConfirmationCode");
            Student student = new Student(discordID, discordName, schoolEmail, confirmationCode);
            allStudents.add(student);
        }
        return allStudents;
    }

    void endConnection() {
        mongoDbClient.endConnection();
    }
}
