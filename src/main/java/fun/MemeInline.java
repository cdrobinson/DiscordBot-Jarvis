/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package fun;

import bot.utilities.FileManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;

public class MemeInline implements Runnable{

    private final MessageChannel messageChannel;
    private final FileManager fileManager;
    private final MessageReceivedEvent messageReceivedEvent;
    private final String messageContent;

    MemeInline(MessageReceivedEvent event) {
        this.messageReceivedEvent = event;
        this.messageChannel = event.getChannel();
        this.fileManager = new FileManager();
        this.messageContent = this.messageReceivedEvent.getMessage().getContentRaw();
    }

    private void parseInlineText() {
        String lowerCaseContent = messageContent.toLowerCase();
        //Inline commands
        if (lowerCaseContent.contains("no u") || lowerCaseContent.contains("no you")) {
            for (Message message : messageReceivedEvent.getChannel().getHistoryBefore(messageReceivedEvent.getMessageId(), 1).complete().getRetrievedHistory()) {
                message.addReaction(messageReceivedEvent.getGuild().getEmoteById("481561171653165067")).queue();
            }
        }
        if (lowerCaseContent.contains("girl") || lowerCaseContent.contains("grill") || lowerCaseContent.contains("gorl") || lowerCaseContent.contains("gurl")) {
            messageChannel.sendMessage("If she breathes, she's a thot!").queue();
        }
        if (lowerCaseContent.contains("women")) {
            messageChannel.sendMessage("All women are queens").queue();
        }
        if (lowerCaseContent.contains("opinion")) {
            File myOpinion = fileManager.getFile("myOpinion.png");
            if (myOpinion != null) {
                messageChannel.sendFile(myOpinion, "myOpinion.png").queue();
            }
        }
        if (lowerCaseContent.contains("wow")) {
            for (Message message : messageReceivedEvent.getChannel().getHistoryBefore(messageReceivedEvent.getMessageId(), 1).complete().getRetrievedHistory()) {
                message.addReaction(messageReceivedEvent.getGuild().getEmoteById("481569620118208512")).queue();
            }
        }
        if (lowerCaseContent.contains("gruhz")) {
            messageChannel.sendMessage("Fuck off, Oly").queue();
        }
    }

    @Override
    public void run() {
        parseInlineText();
    }
}
