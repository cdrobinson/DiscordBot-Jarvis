/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package featureRequester;

import bot.utilities.FunctionHelp;

public class Help extends FunctionHelp {

    public Help() {
        super("Feature Requester",
                "For use in [#feature-request](https://discord.gg/Nd5h4aC) only\n" +
                        "!rf [request]: Adds your request to the list\n" +
                        "!rfRepost: Reposts the feature request list in the chat");
    }
}
