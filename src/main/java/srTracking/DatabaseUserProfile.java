/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package srTracking;

class DatabaseUserProfile {

    private String DiscordName;
    private String DiscordID;
    private String Battletag;
    private Integer SR;
    private String ProfileURL;
    private String IconURL;
    private String RankIconURL;

    DatabaseUserProfile(String discordName, String discordID, String battletag, Integer SR) {
        this.DiscordID = discordID;
        this.Battletag = battletag;
        this.SR = SR;
        this.DiscordName = discordName;
    }

    DatabaseUserProfile(String discordName, String discordID, String battletag, Integer SR, String profileURL, String iconURL, String rankIconURL) {
        this.DiscordID = discordID;
        this.Battletag = battletag;
        this.SR = SR;
        this.DiscordName = discordName;
        this.ProfileURL = profileURL;
        this.IconURL = iconURL;
        this.RankIconURL = rankIconURL;
    }

    String getDiscordID() {
        return DiscordID;
    }

    String getBattletag() {
        return Battletag;
    }

    Integer getSR() {
        return SR;
    }

    String getDiscordName() {
        return DiscordName;
    }

    String getProfileURL() {
        return ProfileURL;
    }

    String getIconURL() {
        return IconURL;
    }

    String getRankIconURL() {
        return RankIconURL;
    }
}
