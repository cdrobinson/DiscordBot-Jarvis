/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package fun;

import bot.utilities.FileManager;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;

public class MemeCommands implements Runnable{

    private final MessageChannel messageChannel;
    private final String command;
    private final FileManager fileManager;
    private final MessageReceivedEvent event;

    MemeCommands(MessageReceivedEvent event, String command) {
        this.messageChannel = event.getChannel();
        this.command = command;
        this.fileManager = new FileManager();
        this.event = event;
    }

    private void parseCommands() {
        switch (command) {
            case "scotland":
                MessageBuilder scotlandBuilder = new MessageBuilder();
                scotlandBuilder.setTTS(true);
                scotlandBuilder.append("SCOTLAND FOREVER!!!");
                messageChannel.sendMessage(scotlandBuilder.build()).queue();
                break;
            case "allwomen":
                MessageBuilder thotBuilder = new MessageBuilder();
                thotBuilder.setTTS(true);
                thotBuilder.append("If she breathes, she's a thot!");
                messageChannel.sendMessage("All women are queens!").queue();
                messageChannel.sendMessage(thotBuilder.build()).queue();
                break;
            case "cherrybomb":
                messageChannel.sendMessage("ch-ch-ch CHERRY BOMB!").queue();
                messageChannel.sendMessage(":cherries::cherries::cherries::cherries::cherries::cherries:").queue();
                break;
            case "damnitjerry":
                File jerryPic = fileManager.getFile("jerryPic.jpg");
                if (jerryPic != null) {
                    messageChannel.sendFile(jerryPic, "jerry.jpg").queue();
                }
                break;
            case "noice":
                File noice = fileManager.getFile("noice.jpg");
                if (noice != null) {
                    messageChannel.sendFile(noice, "noice.jpg").queue();
                    MessageBuilder noiceBuilder = new MessageBuilder();
                    noiceBuilder.setTTS(true);
                    noiceBuilder.append("noice");
                    messageChannel.sendMessage(noiceBuilder.build()).queue();
                }
                break;
            case "wow":
                File wowPic = fileManager.getFile("wow.jpg");
                if (wowPic != null) {
                    messageChannel.sendFile(wowPic, "wow.jpg").queue();
                }
                break;
            case "noyou":
            case "nou":
                File noYou = fileManager.getFile("noYou.png");
                if (noYou != null) {
                    messageChannel.sendFile(noYou, "noYou.png").queue();
                }
                break;
            case "feed":
                event.getMessage().addReaction("\uD83D\uDEE2").queue();
                messageChannel.sendMessage("\uD83D\uDEE2 \uD83D\uDE00 \uD83D\uDE42 \uD83D\uDE16 \uD83D\uDCA9 \uD83D\uDE0C").queue();
                break;
        }
    }

    @Override
    public void run() {
        parseCommands();
    }
}
