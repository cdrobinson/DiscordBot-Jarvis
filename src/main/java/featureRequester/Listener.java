/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package featureRequester;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;

public class Listener extends ListenerAdapter {

    private ArrayList<String> channelsToWatch;
    private final ConfigManager cm = new ConfigManager();

    public Listener() {
        this.channelsToWatch = new ArrayList<>();
        this.channelsToWatch.add("bot_stuff");
        this.channelsToWatch.add("feature-request");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        if (channelsToWatch.contains(event.getChannel().getName())) {
            Thread featureRequestThread = new Thread(new FeatureRequester(event));
            featureRequestThread.start();
        }
    }
}
