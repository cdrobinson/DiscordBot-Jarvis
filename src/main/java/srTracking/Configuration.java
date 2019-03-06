/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package srTracking;

public class Configuration {

    public static String getHelpTitle = "================SR Tracking Commands================";

    public static String getHelpMessage = "!registerBattletag Battletag: Adds your Battletag to the database to have your SR tracked (your profile has to be public) \n" +
            "!sr @someone: Reports the stored SR of either yourself (no parameter) or the person you @'d (currently only accepts discord mentions)\n" +
            "!updateSR: Forces an update on all of the stored Battletags in the database (may take a while)\n" +
            "!leaderboard: Shows the current leaderboard based on SR in the database\n" +
            "!getBattletag @someone: Displays the stored Battletag of the Discord user, if they have registered their Battletag with me\n" +
            "!getDiscord Battletag: Displays the stored Discord name that registered under [Battletag], if they have registered their Battletag with me\n";
}
