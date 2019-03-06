/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.interactiveHelpMessage;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

public abstract class FunctionHelp {

    private String title;
    private String body;

    public FunctionHelp(String title, String body) {
        this.title = title;
        this.body = body;
    }


    @SuppressWarnings("WeakerAccess")
    public MessageEmbed getHelpMessage() {
        return new EmbedBuilder()
                .setColor(new Color(0, 247, 255))
                .setTitle("**" + this.title + "**")
                .setDescription(this.body + "\n\n⬅ Previous Page | Next Page ➡")
                .build();
    }
}
