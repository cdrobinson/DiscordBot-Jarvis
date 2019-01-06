/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import bot.utilities.UserInputManager;
import bot.utilities.HelpMessageBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

class CommandParser {

    private final MessageReceivedEvent messageReceivedEvent;
    private final MessageChannel messageChannel;
    private final String message;
    private final String commandPrefix;

    CommandParser(MessageReceivedEvent event, String commandPrefix) {
        this.commandPrefix = commandPrefix;
        this.messageReceivedEvent = event;
        this.messageChannel = messageReceivedEvent.getChannel();
        this.message = messageReceivedEvent.getMessage().getContentRaw();
    }

    void parseCommand(JDA jdaApi) {
        List<String> messageList = Arrays.asList(Arrays.asList(message.split(commandPrefix)).get(1).split(" "));
        String command = messageList.get(0);
        List<String> commandParameters = messageList.subList(1, messageList.size());

        //Basic default commands
        switch (command) {
            case "ping":
                messageChannel.sendMessage("Pong! `" + jdaApi.getPing() + "ms`").queue();
                break;
            case "bing":
                messageChannel.sendMessage("Bong!").queue();
                break;
            case "help":
                messageChannel.sendMessage(HelpMessageBuilder.getHelpMessage()).queue();
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
                case "vote":
                    UserInputManager.createPoll(messageReceivedEvent);
                    break;
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
