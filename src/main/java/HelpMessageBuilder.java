class HelpMessageBuilder {
    static String getHelpMessage() {
        return "```" +
                "!ping: Pong! [current ping to bot in ms] \r" +
                "!bing: Bong! \r" +
                "!help: Shows you this message \r" +
                "================BSU Students Only================ \r" +
                "!chirpchirp: Run this command after registering as a BSU student on the Cardinal Esports Discord server\r" +
                "================These commands must be used in #sr-tracking================\r" +
                "[####]: To set your SR, type only your SR into the #sr-tracking channel, ie. 2500\r" +
                "!session start: Begins an SR tracking session. Will record your SR when the command is ran \r" +
                "!session current: Tells you the current difference between your SR at the beginning of the session and your current SR \r" +
                "!session end: Tells you the final difference between your SR at the beginning of the session and your current SR \r" +
                "!sr [@someone]: Tells you that person's SR and the difference from your current SR \r" +
                "```";
    }
}
