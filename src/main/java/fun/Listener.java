/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package fun;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {

    private final ConfigManager cm = new ConfigManager();
    private final String commandPrefix = cm.getCommandPrefix();


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        String message = event.getMessage().getContentRaw();
        if (message.startsWith(commandPrefix)) {
            String command = message.split(commandPrefix)[1];
            Thread memesThread = new Thread(new MemeCommands(event, command));
            memesThread.start();
        } else {
            Thread memesThread = new Thread(new MemeInline(event));
            memesThread.start();
        }
    }
}
