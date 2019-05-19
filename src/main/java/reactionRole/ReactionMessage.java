/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;

class ReactionMessage {

    private String messageID;
    private String title;
    private String description;
    private HashMap<String, ReactionRole> rolesList = new HashMap<>();
    private String channelID;

    ReactionMessage(String messageTitle, String messageDescription) {
        this.title = messageTitle;
        this.description = messageDescription;
    }

    MessageEmbed build() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(description);
        for (ReactionRole reactionRole : rolesList.values()) {
            stringBuilder.append("\r");
            stringBuilder.append(reactionRole.getEmoteAsString());
            stringBuilder.append("|");
            stringBuilder.append(reactionRole.getRoleAsMention());
            stringBuilder.append(" - ");
            stringBuilder.append(reactionRole.getDescription());
        }
        embedBuilder.addField(title, stringBuilder.toString(), true);
        embedBuilder.setColor(new Color(255, 156, 0));
        return embedBuilder.build();
    }

    void setMessageID(String id) {
        this.messageID = id;
    }

    void addRoleToMessage(MessageChannel channel, ReactionRole reactionRole) {
        addRoleToMessageList(reactionRole);
        Message message = channel.getMessageById(this.messageID).complete().editMessage(build()).complete();
        if (reactionRole.isSnowFlakeEmote()) {
            message.addReaction(message.getGuild().getEmoteById(reactionRole.getEmoteID())).queue();
        } else {
            message.addReaction(reactionRole.getEmoteID()).queue();
        }
    }

    void addRoleToMessageList(ReactionRole reactionRole) {
        this.rolesList.put(reactionRole.getRoleID(), reactionRole);
    }


    String getMessageID() {
        return this.messageID;
    }

    void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    HashMap<String, ReactionRole> getRolesList() {
        return rolesList;
    }

    boolean hasRoleByEmote(String emoteAsString) {
        for (ReactionRole reactionRole : this.rolesList.values()) {
            if (reactionRole.getEmoteID().equals(emoteAsString)) {
                return true;
            }
        }
        return false;
    }

    ReactionRole getRoleByEmote(String emoteAsString) {
        for (ReactionRole reactionRole : this.rolesList.values()) {
            if (reactionRole.getEmoteID().equals(emoteAsString)) {
                return reactionRole;
            }
        }
        return null;
    }

    String getTitle() {
        return this.title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getDescription() {
        return this.description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getChannelID() {
        return this.channelID;
    }

    void update(MessageChannel channel) {
        Message message = channel.getMessageById(this.messageID).complete();
        message.editMessage(build()).queue();
        for (ReactionRole reactionRole : this.rolesList.values()) {
            if (reactionRole.isSnowFlakeEmote()) {
                message.addReaction(message.getGuild().getEmoteById(reactionRole.getEmoteID())).queue();
            } else {
                message.addReaction(reactionRole.getEmoteID()).queue();
            }
        }
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        mongoDbConnector.addReactionMessage(this);
        mongoDbConnector.endConnection();
    }

    void removeRole(MessageChannel channel, String roleID) {
        this.rolesList.remove(roleID);
        channel.getMessageById(this.messageID).complete().clearReactions().complete();
        update(channel);
    }
}
