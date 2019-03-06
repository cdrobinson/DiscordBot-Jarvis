/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import bot.utilities.HelpMessageBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import srTracking.Configuration;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            case "help":
                sendHelpMessage();
                break;
            case "playScheduler":
                break;
            default:
                    break;
        }
    }

    private void sendHelpMessage() {
        ArrayList<MessageEmbed> helpMessageList = new ArrayList<>();

        HelpMessageBuilder basicHelpMessage = new HelpMessageBuilder("Basic Commands", "!ping: Pong! [current ping to bot in ms] \n" +
                        "!bing: Bong! \n" +
                        "!help: Shows you this message \n" +
                        "!vote [option1, option2, etc] \n"
        );
        HelpMessageBuilder bsuHelpMessage = new HelpMessageBuilder("================BSU Students Only================",
                "!chirpchirp: Run this command after registering as a BSU student on the Cardinal Esports Discord server");
        HelpMessageBuilder frHelpMessage = new HelpMessageBuilder("================#feature-request only================",
                "!rf [request]: Adds your request to the list\n" +
                "!rfRepost: Reposts the feature request list in the chat\n");
        HelpMessageBuilder musicHelpMessage = new HelpMessageBuilder("================#music only================",
                ".play: Plays songs from the current queue. Starts playing again if it was previously paused\n" +
                ".play [url]: Adds a new song to the queue and starts playing if it wasn't playing already\n" +
                ".pause: Pauses audio playback\n" +
                ".stop: Completely stops audio playback, skipping the current song. Clears the queue.\n" +
                ".skip: Skips the current song, automatically starting the next \n" +
                ".nowplaying: Prints information about the currently playing song (title, current time)\n" +
                ".np: Alias for .nowplaying\n" +
                ".list: Lists the songs in the queue \n" +
                ".volume [value]: Sets the volume of the MusicPlayer [10 - 100]\n");
        HelpMessageBuilder srHelpMessage = new HelpMessageBuilder(Configuration.getHelpTitle, Configuration.getHelpMessage);

        helpMessageList.add(basicHelpMessage.getHelpMessage());
        helpMessageList.add(bsuHelpMessage.getHelpMessage());
        helpMessageList.add(frHelpMessage.getHelpMessage());
        helpMessageList.add(musicHelpMessage.getHelpMessage());
        helpMessageList.add(srHelpMessage.getHelpMessage());

        for (MessageEmbed helpMessage : helpMessageList) {
            messageChannel.sendMessage(helpMessage).queue();
        }
    }

}
