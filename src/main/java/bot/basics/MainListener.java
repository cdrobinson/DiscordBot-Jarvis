/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.basics;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class MainListener extends ListenerAdapter {

    private JDA jdaApi;
    private ConfigManager cm = new ConfigManager();
    private String commandPrefix = cm.getCommandPrefix();

    public MainListener(JDA api) {
        this.jdaApi = api;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    channel.getName(), event.getAuthor(), message.getContentRaw());
        }
        if (message.getContentRaw().startsWith(commandPrefix)) {
            CommandParser commandParser = new CommandParser(event, commandPrefix);
            commandParser.parseCommand(jdaApi);
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        System.out.printf("[Joined Guild] %s%n", event.getGuild().getName());
    }

    @Override
    public void onRoleCreate(RoleCreateEvent event) {
        System.out.printf("[Role created] The role: %s was just created on %s%n", event.getRole().getName(), event.getGuild().getName());
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        System.out.printf("[Role given] %s was given the %s role%n", event.getMember().getEffectiveName(), event.getRoles());
        List<Role> rolesGiven = event.getRoles();
        for (Role role : rolesGiven) {
            if (role.getName().equals("Jarvis")) {
                List<TextChannel> channelList = event.getGuild().getTextChannelsByName("general", true);
                if (!channelList.isEmpty()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(new Color(17, 17, 17));
                    //Can eventually make the author URL the Jarvis website
                    embedBuilder.setAuthor("Jarvis", "https://discordapp.com/oauth2/authorize?client_id=469725382858244099&scope=bot&permissions=8",
                            event.getGuild().getMemberById("469725382858244099").getUser().getAvatarUrl());
                    embedBuilder.setThumbnail(event.getGuild().getIconUrl());
                    ConfigManager cm = new ConfigManager();
                    embedBuilder.setTitle("Thanks for adding me!", "https://discordapp.com/oauth2/authorize?client_id=469725382858244099&scope=bot&permissions=8");
                    embedBuilder.setDescription("For a list of my commands, use the `" + cm.getCommandPrefix() + "help` command");
                    embedBuilder.setFooter("Created by BattlemanMK2", "https://cdn.discordapp.com/avatars/230347831335059457/e2de17db52fd61940f5d8bdeaf2148e8.png?size=2048");
                    channelList.get(0).sendMessage(embedBuilder.build()).queue();
                }
            }
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        System.out.printf("[Left Guild] %s%n", event.getGuild().getName());
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}