class SRDatabaseUser {

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

    SRDatabaseUser(String discordID, String battletag, Integer SR) {
        DiscordID = discordID;
        Battletag = battletag;
        this.SR = SR;
    }
}
