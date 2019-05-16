/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package reactionRole;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Listener extends ListenerAdapter {

    private final ConfigManager cm = new ConfigManager();
    private String commandPrefix;

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

    private void parseCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().substring(1);
        List<String> messageList = Arrays.asList(message.split(" "));
        String command = messageList.get(0).toLowerCase();
        TextChannel channel = event.getMessage().getMentionedChannels().get(0);
        String channelID = channel.getId();
        String messageId = messageList.get(2);
        String[] messageSplit = message.split("\"");
        switch (command) {
            case "createmessage": //!createMessage #[channel] "[Title]" "[Description]"
                String messageTitle = messageSplit[1];
                String messageDescription = messageSplit[3];
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(new Color(255, 156, 0));
                embedBuilder.addField(messageTitle, messageDescription, true);
                event.getGuild().getTextChannelById(channelID).sendMessage(embedBuilder.build()).queue();
                break;
            case "editmessage": //!editMessage #[channel] [messageId] "[Description]"

                break;
            case "edittitle": //!editTitle #[channel] [messageId} "[Title]"

                break;
            case "addrole": //!addRole #[channel] [messageId] [emote] @[role] "[Role Description]"
                List<Role> roles = event.getMessage().getMentionedRoles();
                String roleToAdd;
                if (!roles.isEmpty()) {
                    roleToAdd = roles.get(0).getAsMention();
                } else {
                    //Error getting role from message
                    return;
                }
                ReactionMessage reactionMessage = new ReactionMessage(channel.getMessageById(messageId).complete());
                List<Emote> listOfEmotes = event.getMessage().getEmotes();
                String emoteAsString;
                if (listOfEmotes.isEmpty()) {
                    emoteAsString = messageList.get(3);
                    reactionMessage.addReaction(emoteAsString);
                } else {
                    Emote mentionedEmote = listOfEmotes.get(0);
                    emoteAsString = mentionedEmote.getAsMention();
                    reactionMessage.addReaction(mentionedEmote);
                }
                channel.getMessageById(messageId).queue((Message targetMessage) -> {
                    List<MessageEmbed> messageEmbeds = targetMessage.getEmbeds();
                    if (!messageEmbeds.isEmpty()) {
                        EmbedBuilder newEmbed = new EmbedBuilder(messageEmbeds.get(0));
                        MessageEmbed.Field currentField = newEmbed.getFields().get(0);
                        String title = currentField.getName();
                        String content = currentField.getValue();
                        newEmbed.clearFields();
                        String newContent = content + "\r" + emoteAsString + "|" + roleToAdd + " - " + messageSplit[1];
                        newEmbed.addField(title, newContent, true);
                        targetMessage.editMessage(newEmbed.build()).queue();
                    }
                });
                break;
            case "removerole":

                break;
            case "testing":

                break;
            default:

                break;
        }
    }

}
