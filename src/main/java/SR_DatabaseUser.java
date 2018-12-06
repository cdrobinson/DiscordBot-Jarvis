class SR_DatabaseUser {

    private String discordID;
    private String battletag;
    private Integer userSR;

    String getDiscordID() {
        return discordID;
    }

    String getBattletag() {
        return battletag;
    }

    Integer getUserSR() {
        return userSR;
    }

    SR_DatabaseUser(String discordID, String battletag, Integer userSR) {
        this.discordID = discordID;
        this.battletag = battletag;
        this.userSR = userSR;
    }
}
