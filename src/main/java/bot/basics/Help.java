/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import bot.interactiveHelpMessage.FunctionHelp;

public class Help extends FunctionHelp {


    public Help() {
        super("Basic Commands", "!ping: Pong! [current ping to bot in ms] \n" +
                "!bing: Bong! \n" +
                "!help: Shows you this message \n");
    }
}
