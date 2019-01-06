/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MainListener extends ListenerAdapter {

    private JDA jdaApi;
    private ConfigManager cm = new ConfigManager();
    private String commandPrefix = cm.getCommandPrefix();

    public MainListener(JDA api) {
        this.jdaApi = api;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    channel.getName(), event.getAuthor(), message.getContentRaw());
        }
        if (message.getContentRaw().startsWith(commandPrefix)) {
            CommandParser commandParser = new CommandParser(event, commandPrefix);
            commandParser.parseCommand(jdaApi);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}