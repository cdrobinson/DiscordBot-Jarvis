/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

class CommandParser {

    private final MessageChannel messageChannel;
    private final String message;
    private final String commandPrefix;

    CommandParser(MessageReceivedEvent event, String commandPrefix) {
        this.commandPrefix = commandPrefix;
        this.messageChannel = event.getChannel();
        this.message = event.getMessage().getContentRaw();
    }

    void parseCommand(JDA jdaApi) {
        List<String> messageList = Arrays.asList(Arrays.asList(message.split(commandPrefix)).get(1).split(" "));
        String command = messageList.get(0);

        //Basic default commands
        switch (command) {
            case "ping":
                messageChannel.sendMessage("Pong! `" + jdaApi.getPing() + "ms`").queue();
                break;
            case "bing":
                messageChannel.sendMessage("Bong!").queue();
                break;
            case "playScheduler":
                break;
            case "testing":

                break;
            default:
                    break;
        }
    }
}
