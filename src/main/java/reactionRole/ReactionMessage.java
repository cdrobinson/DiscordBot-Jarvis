/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;

class ReactionMessage {

    private String discordID;
    private String title;
    private String description;
    private ArrayList<RoleData> roles;
    private Message message;

    ReactionMessage(Message message) {
        this.message = message;
    }

    void addReaction(String emote) {
        this.message.addReaction(emote).queue();
    }

    void addReaction(Emote emote) {
        this.message.addReaction(emote).queue();
    }
}
