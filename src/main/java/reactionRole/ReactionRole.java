/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;


class ReactionRole {

    private String description;
    private String emoteAsString;
    private String emoteID;
    private Boolean isSnowFlakeEmote;
    private String roleID;

    //TODO Transition using Emote to using the emote ID

    ReactionRole() {
    }

    void setEmoteAsString(String emoteAsString) {
        this.emoteAsString = emoteAsString;
    }

    String getEmoteAsString() {
        return this.emoteAsString;
    }

    void setEmoteID(String emoteID) {
        this.emoteID = emoteID;
    }

    String getEmoteID() {
        return emoteID;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getRoleAsMention() {
        return "<@&" + roleID + ">";
    }

    String getDescription() {
        return description;
    }

    void setSnowFlakeStatus(Boolean isSnowFlakeEmote) {
        this.isSnowFlakeEmote = isSnowFlakeEmote;
    }

    boolean isSnowFlakeEmote() {
        return this.isSnowFlakeEmote;
    }

    void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    String getRoleID() {
        return this.roleID;
    }
}
