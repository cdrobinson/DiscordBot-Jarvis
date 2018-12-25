/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.utilities;

import srTracking.Configuration;

public class HelpMessageBuilder {
    public static String getHelpMessage() {
        return "```" +
                "!ping: Pong! [current ping to bot in ms] \n" +
                "!bing: Bong! \n" +
                "!help: Shows you this message \n" +
                "!vote [option1, option2, etc] \n" +
                "================BSU Students Only================ \n" +
                "!chirpchirp: Run this command after registering as a BSU student on the Cardinal Esports Discord server\n" +
                Configuration.getHelpMessage() +
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
