class SR_DatabaseUser {

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

    SR_DatabaseUser(String discordID, String battletag, Integer SR) {
        DiscordID = discordID;
        Battletag = battletag;
        this.SR = SR;
    }
}
