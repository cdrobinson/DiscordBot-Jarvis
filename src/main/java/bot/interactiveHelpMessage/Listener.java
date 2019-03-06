/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.interactiveHelpMessage;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Listener extends ListenerAdapter {

    //TODO: Add table of contents functionality

    private ConfigManager cm = new ConfigManager();
    private String commandPrefix = cm.getCommandPrefix();
    private Map<String, Integer> existingHelpMessages = new HashMap<>();
    private static ArrayList<MessageEmbed> helpMessageList = new ArrayList<>();

    public Listener() {
        helpMessageList.add(new bot.basics.Help().getHelpMessage());
        helpMessageList.add(new featureRequester.Help().getHelpMessage());
        helpMessageList.add(new frontline.Help().getHelpMessage());
        helpMessageList.add(new music.Help().getHelpMessage());
        helpMessageList.add(new srTracking.Help().getHelpMessage());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        String content = event.getMessage().getContentRaw();
        if (content.startsWith(commandPrefix)) {
            if (Arrays.asList(Arrays.asList(content.split(commandPrefix)).get(1).split(" ")).get(0).equals("help")) {
                createNewHelpMessage(event);
            }
        }
    }

    private void createNewHelpMessage(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(getHelpMessage(0)).queue((message -> {
            message.addReaction("⬅").queue();
            message.addReaction("➡").queue();
            existingHelpMessages.put(message.getId(), 0);
        }));
        System.out.printf("Existing list created with length: %s\n", existingHelpMessages.size());
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals("237059614384848896")) return;
        String messageId = event.getMessageId();
        if (existingHelpMessages.containsKey(messageId)) {
            event.getChannel().getMessageById(messageId).queue((message -> {
                message.clearReactions().queue();
                message.editMessage(getHelpMessage(changePage(messageId, event.getReactionEmote()))).queue((editedMessage) -> {
                    editedMessage.addReaction("⬅").queue();
                    editedMessage.addReaction("➡").queue();
                });
            }));
        }
        //if message reaction is one of the help messages out there
        //interact with help message
    }

    private Integer changePage(String messageId, MessageReaction.ReactionEmote reactionEmote) {
        String emote = reactionEmote.getName();
        Integer pageNumber = existingHelpMessages.get(messageId);
        Integer lastPage = helpMessageList.size() - 1;
        switch (emote) {
            case "⬅":
                if (pageNumber == 0) {
                    pageNumber = lastPage;
                } else {
                    pageNumber = pageNumber - 1;
                }
                break;
            case "➡":
                if (pageNumber.equals(lastPage)) {
                    pageNumber = 0;
                } else {
                    pageNumber = pageNumber + 1;
                }
                break;
            default:
                System.out.println("There was a new emote added\n");
                break;
        }
        existingHelpMessages.replace(messageId, pageNumber);
        return pageNumber;
    }


    //interact with help message
    //if the reaction is to flip page to the right, flip to the right
    //if the reaction is to flip page to the left, flip to the left



    private MessageEmbed getHelpMessage(Integer pageNumber) {
        return helpMessageList.get(pageNumber);
    }
}
