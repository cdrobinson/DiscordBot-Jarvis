class HelpMessageBuilder {
    static String getHelpMessage() {
        return "```" +
                "!ping: Pong! [current ping to bot in ms] \n" +
                "!bing: Bong! \n" +
                "!help: Shows you this message \n" +
                "!vote [option1, option2, etc] \n" +
                "================BSU Students Only================ \n" +
                "!chirpchirp: Run this command after registering as a BSU student on the Cardinal Esports Discord server\n" +
                "================#sr-tracking only================\n" +
                "[####]: To set your SR, type only your SR into the #sr-tracking channel, ie. 2500\n" +
                "!session start: Begins an SR tracking session. Will record your SR when the command is ran \n" +
                "!session current: Tells you the current difference between your SR at the beginning of the session and your current SR \n" +
                "!session end: Tells you the final difference between your SR at the beginning of the session and your current SR \n" +
                "!sr [@someone]: Tells you that person's SR and the difference from your current SR \n" +
                "================#feature-request only================\n" +
                "!rf [request]: Adds your request to the list\n" +
                "!rfRepost: Reposts the feature request list in the chat\n" +
                "================#music only================\n" +
                ".play: Plays songs from the current queue. Starts playing again if it was previously paused\n" +
                ".play [url]: Adds a new song to the queue and starts playing if it wasn't playing already\n" +
                ".pause: Pauses audio playback\n" +
                ".stop: Completely stops audio playback, skipping the current song. Clears the queue.\n" +
                ".skip: Skips the current song, automatically starting the next \n" +
                ".nowplaying: Prints information about the currently playing song (title, current time)\n" +
                ".np: Alias for .nowplaying\n" +
                ".list: Lists the songs in the queue \n" +
                ".volume [value]: Sets the volume of the MusicPlayer [10 - 100]\n" +
                "```";
    }
}
