/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import bot.utilities.FunctionHelp;

class Help extends FunctionHelp {


    Help() {
        super("Basic Commands", "!ping: Pong! [current ping to bot in ms] \n" +
                "!bing: Bong! \n" +
                "!help: Shows you this message \n" +
                "!vote [option1, option2, etc] \n");
    }
}
