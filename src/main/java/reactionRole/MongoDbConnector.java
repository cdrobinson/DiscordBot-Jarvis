/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import mongodb.MongoDbClient;
import org.bson.Document;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

class MongoDbConnector {

    private MongoDbClient mongoDbClient;
    private MongoCollection<Document> collection;

    MongoDbConnector() {
        this.mongoDbClient = new MongoDbClient();
        this.collection = mongoDbClient.getCollection(mongoDbClient.getDatabase("Frontline"), "ReactionRole");
    }

    void addReactionMessage(ReactionMessage reactionMessage) {
        Document reactionMessageInformation = new Document();
        reactionMessageInformation.append("ChannelID", reactionMessage.getChannelID());
        reactionMessageInformation.append("MessageID", reactionMessage.getMessageID());
        reactionMessageInformation.append("Title", reactionMessage.getTitle());
        reactionMessageInformation.append("Description", reactionMessage.getDescription());
        Document roleListDocument = new Document();
        for (ReactionRole reactionRole : reactionMessage.getRolesList().values()) {
            Document roleDocument = new Document();
            roleDocument.append("RoleID", reactionRole.getRoleID())
                    .append("RoleDescription", reactionRole.getDescription())
                    .append("EmoteAsString", reactionRole.getEmoteAsString())
                    .append("EmoteID", reactionRole.getEmoteID());
            roleListDocument.append(reactionRole.getRoleID(), roleDocument);
        }
        reactionMessageInformation.append("Roles", roleListDocument);
        if (collection.find(eq("MessageID", reactionMessage.getMessageID())).first() == null) {
            collection.insertOne(reactionMessageInformation);
        } else {
            collection.replaceOne(eq("MessageID", reactionMessage.getMessageID()), reactionMessageInformation);
        }
    }

    HashMap<String, ReactionMessage> getAllReactionMessages() {
        HashMap<String, ReactionMessage> allReactionMessages = new HashMap<>();
        FindIterable<Document> foundSet = collection.find();
        for (Document document : foundSet) {
            ReactionMessage reactionMessage = new ReactionMessage(document.get("Title").toString(), document.get("Description").toString());
            reactionMessage.setMessageID(document.get("MessageID").toString());
            reactionMessage.setChannelID(document.get("ChannelID").toString());

            Document parentRoleListDocument = (Document) document.get("Roles");
            for (Object roleObject : parentRoleListDocument.values()) {
                Document roleDocument = (Document) roleObject;
                ReactionRole reactionRole = new ReactionRole();
                reactionRole.setRoleID(roleDocument.get("RoleID").toString());
                reactionRole.setDescription(roleDocument.get("RoleDescription").toString());
                reactionRole.setEmoteAsString(roleDocument.get("EmoteAsString").toString());
                reactionRole.setEmoteID(roleDocument.get("EmoteID").toString());
                reactionMessage.addRoleToMessageList(reactionRole);
            }
            allReactionMessages.put(reactionMessage.getMessageID(), reactionMessage);
        }
        return allReactionMessages;
    }

    void endConnection() {
        mongoDbClient.endConnection();
    }
}
