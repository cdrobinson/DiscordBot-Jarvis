class HelpMessageBuilder {
    static String getHelpMessage() {
        return "```" +
                "!ping: Pong! \r" +
                "!bing: Bong! \r" +
                "!help: Shows you this message \r" +
                "!session start: Begins an SR tracking session. Will record your SR when the command is ran \r" +
                "!session current: Tells you the current difference between your SR at the beginning of the session and your current SR \r" +
                "!session end: Tells you the final difference between your SR at the beginning of the session and your current SR \r" +
                "!sr [@someone]: Tells you that person's SR and the difference from your current SR \r" +
                "[####]: To set your SR, type only your SR into the #sr-tracking channel, ie. 2500```";
    }
}
