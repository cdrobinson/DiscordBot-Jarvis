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
    private HashMap<String, ReactionMessage> allReactionMessages;

    public Listener() {
        this.commandPrefix = cm.getCommandPrefix();
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        this.allReactionMessages = mongoDbConnector.getAllReactionMessages();
        mongoDbConnector.endConnection();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        if (event.getMessage().getContentRaw().startsWith(commandPrefix)) {
            parseCommand(event);
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
                emoteAsString = reactionEmote.getEmote().getId();
            } else {
                emoteAsString = reactionEmote.toString();
            }
            if (reactionMessage.hasRoleByEmote(emoteAsString)) {
                ReactionRole reactionRole = reactionMessage.getRoleByEmote(emoteAsString);
                if (reactionRole != null) {
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(reactionRole.getRoleID())).queue();
                } else {
                    System.out.println("There is either a reaction on a message that isn't a registered role, or the reactionRole lookup list is missing a proper value.");
                }
            }
        }
    }

    private void parseCommand(MessageReceivedEvent event) {
        String messageAsString = event.getMessage().getContentRaw().substring(1);
        messageAsString = messageAsString.replaceAll(" {2}", " ");
        List<String> messageListSplitBySpaces = Arrays.asList(messageAsString.split(" "));
        List<String> messageSplitByQuotes = Arrays.asList(messageAsString.split("\""));
        ReactionMessage reactionMessage;
        if (this.allReactionMessages.size() != 0) {
            reactionMessage = this.allReactionMessages.get(messageListSplitBySpaces.get(2));
        } else {
            reactionMessage = null;
        }
        MessageChannel mentionedChannel;
        if (event.getMessage().getMentionedChannels().size() != 0) {
            mentionedChannel = event.getMessage().getMentionedChannels().get(0);
        } else {
            event.getChannel().sendMessage("Make sure you are tagging a valid channel as the second parameter of the command").queue();
            return;
        }
        switch (messageListSplitBySpaces.get(0).toLowerCase()) {
            case "createmessage": //!createMessage #[channel] "[Title]" "[Description]"
                if (messageSplitByQuotes.size() == 4) {
                    String messageTitle = messageSplitByQuotes.get(1);
                    String messageDescription = messageSplitByQuotes.get(3);
                    createMessage(mentionedChannel, messageTitle, messageDescription);
                } else {
                    event.getChannel().sendMessage("Make sure you include a title and description for the message both enclosed in quotes").queue();
                }
                break;
            case "editmessage": //!editMessage #[channel] [messageId] "[Description]"
                if (reactionMessage == null) {
                    event.getChannel().sendMessage("There is no reaction message initialized with that message ID").queue();
                    return;
                }
                if (messageSplitByQuotes.size() != 2) {
                    event.getChannel().sendMessage("Make sure you include a new description enclosed in quotes").queue();
                    return;
                }
                reactionMessage.setDescription(messageSplitByQuotes.get(1));
                reactionMessage.update(mentionedChannel);

                break;
            case "edittitle": //!editTitle #[channel] [messageId} "[Title]"
                if (reactionMessage == null) {
                    event.getChannel().sendMessage("There is no reaction message initialized with that message ID").queue();
                    return;
                }
                if (messageSplitByQuotes.size() != 2) {
                    event.getChannel().sendMessage("Make sure you include a new title enclosed in quotes").queue();
                    return;
                }
                reactionMessage.setTitle(messageSplitByQuotes.get(1));
                reactionMessage.update(mentionedChannel);
                break;
            case "addrole": //!addRole #[channel] [messageId] [emote] @[role] "[Role Description]"
                if (reactionMessage == null) {
                    event.getChannel().sendMessage("There is no reaction message initialized with that message ID").queue();
                    return;
                }
                if (messageSplitByQuotes.size() != 2) {
                    event.getChannel().sendMessage("Make sure you include a role description enclosed in quotes").queue();
                    return;
                }
                addRole(reactionMessage, event.getMessage(), mentionedChannel, messageListSplitBySpaces.get(3), messageSplitByQuotes.get(1));
                break;
            case "removerole": //!removeRole #[channel] [messageID] @[role]
                if (reactionMessage == null) {
                    event.getChannel().sendMessage("There is no reaction message initialized with that message ID").queue();
                    return;
                }
                removeRole(reactionMessage, event);
                break;
            case "editroleemote":
                break;
            case "editroledescription":
                break;
            case "editcolor": //Will need RGB colors
                break;
            case "testing":

                break;
            default:

                break;
        }
    }

    private void createMessage(MessageChannel channel, String messageTitle, String messageDescription) {
        //!createMessage #[channel] "[Title]" "[Description]"
        ReactionMessage reactionMessage = new ReactionMessage(messageTitle, messageDescription);
        Message postedMessage = channel.sendMessage(reactionMessage.build()).complete();
        reactionMessage.setMessageID(postedMessage.getId());
        reactionMessage.setChannelID(channel.getId());
        this.allReactionMessages.put(reactionMessage.getMessageID(), reactionMessage);
        updateDatabse(reactionMessage);
    }

    private void addRole(ReactionMessage reactionMessage, Message message, MessageChannel mentionedChannel, String emoteParameter, String roleDescription) {
        //!addRole #[channel] [messageId] [emote] @[role] "[Role Description]"
        List<Emote> listOfEmotes = message.getEmotes();
        List<Role> mentionedRoles = message.getMentionedRoles();
        ReactionRole reactionRole = new ReactionRole();
        reactionRole.setDescription(roleDescription);
        if (!mentionedRoles.isEmpty()) {
            reactionRole.setRoleID(mentionedRoles.get(0).getId());
        } else {
            System.out.println("There was no role mentioned in the command");
            return;
        }
        if (listOfEmotes.isEmpty()) {
            if (emoteParameter.length() != emoteParameter.codePointCount(0, emoteParameter.length())) {
                reactionRole.setEmoteAsString(emoteParameter);
                reactionRole.setEmoteID(emoteParameter);
                reactionRole.setSnowFlakeStatus(false);
            } else {
                message.getChannel().sendMessage("Make sure you are entering a valid emoji/emote").queue();
            }
        } else {
            reactionRole.setEmoteAsString(listOfEmotes.get(0).getAsMention());
            reactionRole.setEmoteID(listOfEmotes.get(0).getId());
            reactionRole.setSnowFlakeStatus(true);
        }
        reactionMessage.addRoleToMessage(mentionedChannel, reactionRole);
        updateDatabse(reactionMessage);
    }

    private void removeRole(ReactionMessage reactionMessage, MessageReceivedEvent event) {
        List<Role> mentionedRoles = event.getMessage().getMentionedRoles();
        if (!mentionedRoles.isEmpty()) {
            reactionMessage.removeRole(event.getChannel(), mentionedRoles.get(0).getId());
        } else {
            event.getChannel().sendMessage("There was no role mentioned in the parameters. Make sure you are @'ing a role").queue();
        }
    }

    private void updateDatabse(ReactionMessage reactionMessage) {
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        mongoDbConnector.addReactionMessage(reactionMessage);
        mongoDbConnector.endConnection();
    }
}
