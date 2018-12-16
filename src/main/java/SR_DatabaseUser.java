class SR_DatabaseUser {

    private String DiscordName;
    private String DiscordID;
    private String Battletag;
    private Integer SR;

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

    SR_DatabaseUser(String discordName, String discordID, String battletag, Integer SR) {
        this.DiscordID = discordID;
        this.Battletag = battletag;
        this.SR = SR;
        this.DiscordName = discordName;
    }
}
