/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package music;

import bot.utilities.FunctionHelp;

public class Help extends FunctionHelp {

    public Help() {
        super("#music only",
                ".play: Plays songs from the current queue. Starts playing again if it was previously paused\n" +
                        ".play [url]: Adds a new song to the queue and starts playing if it wasn't playing already\n" +
                        ".pause: Pauses audio playback\n" +
                        ".stop: Completely stops audio playback, skipping the current song. Clears the queue.\n" +
                        ".skip: Skips the current song, automatically starting the next \n" +
                        ".nowplaying: Prints information about the currently playing song (title, current time)\n" +
                        ".np: Alias for .nowplaying\n" +
                        ".list: Lists the songs in the queue \n" +
                        ".volume [value]: Sets the volume of the MusicPlayer [10 - 100]\n");
    }
}
