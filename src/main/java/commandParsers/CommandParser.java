/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package commandParsers;

import bot.fun.MemeCommands;
import bot.utilities.ConfigManager;
import bot.utilities.UserInputManager;
import bot.utilities.HelpMessageBuilder;
import featureRequester.FeatureRequester;
import frontline.BsuChecker;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import srTracking.SrTracker;

import java.util.Arrays;
import java.util.List;

public class CommandParser {

    private final MessageReceivedEvent messageReceivedEvent;
    private final MessageChannel messageChannel;
    private final String message;
    private final String commandPrefix;

    public CommandParser(MessageReceivedEvent event, String commandPrefix) {
        this.commandPrefix = commandPrefix;
        this.messageReceivedEvent = event;
        this.messageChannel = messageReceivedEvent.getChannel();
        this.message = messageReceivedEvent.getMessage().getContentRaw();
    }

    public void parseCommand(JDA jdaApi) {
        List<String> messageList = Arrays.asList(Arrays.asList(message.split(commandPrefix)).get(1).split(" "));
        String command = messageList.get(0);
        List<String> commandParameters = messageList.subList(1, messageList.size());

        if (messageChannel.getName().equals(new ConfigManager().getProperty("srTrackingChannelName")) || messageChannel.getName().equals("bot_stuff")) {
            Thread srTrackerThread = new Thread(new SrTracker(messageReceivedEvent));
            srTrackerThread.start();
        }
        if (messageChannel.getName().equals(new ConfigManager().getProperty("featureRequestChannelName"))) {
            Thread featureRequestThread = new Thread(new FeatureRequester(messageReceivedEvent));
            featureRequestThread.start();
        }
        //Global commands go below all the if checks
        Thread memesThread = new Thread(new MemeCommands(messageReceivedEvent, command));
        memesThread.start();

        //Basic default commands
        switch (command) {
            case "ping":
                messageChannel.sendMessage("Pong! `" + jdaApi.getPing() + "`").queue();
                break;
            case "bing":
                messageChannel.sendMessage("Bong!").queue();
                break;
            case "help":
                messageChannel.sendMessage(HelpMessageBuilder.getHelpMessage()).queue();
                break;
            case "feed":
                messageReceivedEvent.getMessage().addReaction("\uD83D\uDEE2").queue();
                messageChannel.sendMessage("\uD83D\uDEE2 \uD83D\uDE00 \uD83D\uDE42 \uD83D\uDE16 \uD83D\uDCA9 \uD83D\uDE0C").queue();
                break;
            case "chirpchirp":
                //Gives users the
                BsuChecker.cardinalChecker(messageReceivedEvent);
                break;
            case "vote":
                UserInputManager.createPoll(messageReceivedEvent);
                break;
            case "test":

                break;
            default:
                    break;
        }

        //Administrator Commands
        if (messageReceivedEvent.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            //Simple commands
            switch (command) {
                case "say":
                    StringBuilder whatToSay = new StringBuilder();
                    for (String word : commandParameters) {
                        whatToSay.append(word);
                        whatToSay.append(" ");
                    }
                    messageReceivedEvent.getGuild().getTextChannelsByName("general", true).get(0).sendMessage(whatToSay).queue();
                    System.out.printf("You told me to say this in #general chat: %s", whatToSay);
                    break;
                case "perms":
                    List<Permission> authorPerms = messageReceivedEvent.getMember().getPermissions();
                    StringBuilder permList = new StringBuilder();
                    permList.append("```");
                    for (Permission permission : authorPerms) {
                        permList.append(permission.getName());
                        permList.append("\r");
                    }
                    permList.append("```");
                    this.messageChannel.sendMessage(permList.toString()).queue();
                    break;
                default:
                    break;
            }
            //Commands with parameters
            switch (command) {
                case "playing":
                    jdaApi.getPresence().setGame(Game.playing(messageList.get(1)));
                    messageChannel.sendMessageFormat("I will start playing %s now.", messageList.get(1)).queue();
                    break;
                default:
                    break;
            }
        }
    }

}
