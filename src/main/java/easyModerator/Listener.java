/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package easyModerator;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listener extends ListenerAdapter {

    private ConfigManager cm = new ConfigManager();
    private static final String deletionEmote = "blobsaluteban";
    //This should work if you use a custom emote or a unicode emoji
    //Custom emote: :leagueOfLegendsLogo:
    //Emoji: 😄 or ❌ or :smiley:

    private final ArrayList<String> bannedWords = new ArrayList<>();

    public Listener() {
        bannedWords.add("gay");
        bannedWords.add("gey");
        bannedWords.add("gai");
        bannedWords.add("ggay");
        bannedWords.add("g4y");
        bannedWords.add("64y");
        bannedWords.add("gaay");
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        if (reactionEmote.getName().equals(deletionEmote)) {
            String messageID = event.getMessageId();
            Message message = event.getChannel().getMessageById(messageID).complete();
            String userName = message.getMember().getEffectiveName();
            message.delete().queue();
            event.getChannel().sendMessage(userName + "'s message has been deleted").queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        List<String> messageSplitBySpaces = Arrays.asList(event.getMessage().getContentRaw().split(" "));
        boolean naughtyMessage = false;
        for (String messageWord : messageSplitBySpaces) {
            if (bannedWords.contains(messageWord)) {
                naughtyMessage = true;
                messageSplitBySpaces.set(messageSplitBySpaces.indexOf(messageWord), "dumb");
            }
        }
        if (naughtyMessage) {
            event.getMessage().delete().complete();
            StringBuilder stringBuilder = new StringBuilder();
            for (String messageWord : messageSplitBySpaces) {
                stringBuilder.append(messageWord);
                stringBuilder.append(" ");
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String userName = event.getMember().getEffectiveName();
            embedBuilder.addField("What " + userName + " was trying to say was:", stringBuilder.toString(), true);
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }
}
