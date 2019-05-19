/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Listener extends ListenerAdapter {

    private final ConfigManager cm = new ConfigManager();
    private String commandPrefix;
    private HashMap<String, ReactionMessage> allReactionMessages = new HashMap<>();

    public Listener() {
        this.commandPrefix = cm.getCommandPrefix();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        if (event.getMessage().getContentRaw().startsWith(commandPrefix)) {
            if (event.getChannel().getName().equals("bot_stuff")) {
                parseCommand(event);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        String messageID = event.getMessageId();
        if (allReactionMessages.containsKey(messageID)) {
            ReactionMessage reactionMessage = allReactionMessages.get(messageID);
            MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
            String emoteAsString;
            if (reactionEmote.isEmote()) {
                emoteAsString = reactionEmote.getEmote().getAsMention();
            } else {
                emoteAsString = reactionEmote.toString();
            }
            if (reactionMessage.getRolesList().containsKey(emoteAsString)) {
                ReactionRole reactionRole = reactionMessage.getRolesList().get(emoteAsString);
//                event.getGuild().getController().addSingleRoleToMember(event.getMember(), reactionRole.getRole()).queue();
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(reactionRole.getRoleID())).queue();
            }
        }
    }

    private void parseCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().substring(1);
        List<String> messageList = Arrays.asList(message.split(" "));
        String[] messageSplitByQuotes = message.split("\"");
        switch (messageList.get(0).toLowerCase()) {
            case "createmessage": //!createMessage #[channel] "[Title]" "[Description]"
                TextChannel channel = event.getMessage().getMentionedChannels().get(0);
                createMessage(channel, messageSplitByQuotes);
                break;
            case "editmessage": //!editMessage #[channel] [messageId] "[Description]"

                break;
            case "edittitle": //!editTitle #[channel] [messageId} "[Title]"

                break;
            case "addrole": //!addRole #[channel] [messageId] [emote] @[role] "[Role Description]"
                addRole(event, messageList.get(2), messageList, messageSplitByQuotes);
                break;
            case "removerole":

                break;
            case "testing":
                event.getMessage().addReaction(event.getGuild().getEmoteById("523536842298097668")).queue();
                MongoDbConnector mongoDbConnector = new MongoDbConnector();
                mongoDbConnector.getAllReactionMessages();
                break;
            default:

                break;
        }
    }

    private void createMessage(MessageChannel channel, String[] messageSplitByQuotes) {
        //!createMessage #[channel] "[Title]" "[Description]"
        String messageTitle = messageSplitByQuotes[1];
        String messageDescription = messageSplitByQuotes[3];
        ReactionMessage reactionMessage = new ReactionMessage(messageTitle, messageDescription);
        Message postedMessage = channel.sendMessage(reactionMessage.build()).complete();
        reactionMessage.setMessage(postedMessage);
        reactionMessage.setMessageID(postedMessage.getId());
        reactionMessage.setChannelID(channel.getId());
        this.allReactionMessages.put(reactionMessage.getMessageID(), reactionMessage);
        updateDatabse(reactionMessage);
    }

    private void addRole(MessageReceivedEvent event, String messageId, List<String> messageList, String[] messageSplitByQuotes) {
        //!addRole #[channel] [messageId] [emote] @[role] "[Role Description]"
        List<Emote> listOfEmotes = event.getMessage().getEmotes();
        List<Role> mentionedRoles = event.getMessage().getMentionedRoles();
        ReactionRole reactionRole = new ReactionRole();
        reactionRole.setDescription(messageSplitByQuotes[1]);
        if (!mentionedRoles.isEmpty()) {
            reactionRole.setRoleID(mentionedRoles.get(0).getId());
        } else {
            System.out.println("There was no role mentioned in the command");
            return;
        }
        if (listOfEmotes.isEmpty()) {
            reactionRole.setEmoteAsString(messageList.get(3));
            reactionRole.setEmoteID(messageList.get(3));
            reactionRole.setSnowFlakeStatus(false);
        } else {
            reactionRole.setEmoteAsString(listOfEmotes.get(0).getAsMention());
            reactionRole.setEmoteID(listOfEmotes.get(0).getId());
            reactionRole.setSnowFlakeStatus(true);
        }
        ReactionMessage reactionMessage = this.allReactionMessages.get(messageId);
        reactionMessage.addRoleToMessage(reactionRole);
        updateDatabse(reactionMessage);
    }

    private void updateDatabse(ReactionMessage reactionMessage) {
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        mongoDbConnector.addReactionMessage(reactionMessage);
        mongoDbConnector.endConnection();
    }
}
