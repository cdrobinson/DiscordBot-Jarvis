/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.utilities;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

public class HelpMessageBuilder {

    private MessageEmbed helpMessage;

    public HelpMessageBuilder(String title, String body) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(0, 247, 255));
        embedBuilder.addField("**" + title + "**", body, false);
        this.helpMessage = embedBuilder.build();
    }

    public MessageEmbed getHelpMessage() {
        return this.helpMessage;
    }
}
