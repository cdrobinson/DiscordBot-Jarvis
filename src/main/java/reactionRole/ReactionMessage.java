/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;

class ReactionMessage {

    private String messageID;
    private String title;
    private String description;
    private HashMap<String, ReactionRole> rolesList = new HashMap<>();
    private Message message;
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

    void addRoleToMessage(ReactionRole reactionRole) {
        addRoleToMessageList(reactionRole);
        this.message = message.editMessage(build()).complete();
        if (reactionRole.isSnowFlakeEmote()) {
            message.addReaction(message.getGuild().getEmoteById(reactionRole.getEmoteID())).queue();
        } else {
            message.addReaction(reactionRole.getEmoteID()).queue();
        }
    }

    void addRoleToMessageList(ReactionRole reactionRole) {
        this.rolesList.put(reactionRole.getEmoteAsString(), reactionRole);
    }

    void setMessage(Message message) {
        this.message = message;
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

    String getTitle() {
        return this.title;
    }

    String getDescription() {
        return this.description;
    }

    String getChannelID() {
        return this.channelID;
    }
}
