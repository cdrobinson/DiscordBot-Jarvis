/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package listeners;

import bot.fun.MemeInline;
import bot.utilities.ConfigManager;
import bot.utilities.HelpMessageBuilder;
import commandParsers.CommandParser;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import twitter.Twitter_Manager;

import java.util.List;

public class MainListener extends ListenerAdapter {

    private JDA jdaApi;
    private ConfigManager cm;
    private static final String commandPrefix = "!";

    public MainListener(JDA api) {
        this.cm = new ConfigManager();
        this.jdaApi = api;
        Thread thread = new Thread(new Twitter_Manager(jdaApi.getGuildById(cm.getProperty("guildID")).getTextChannelsByName(cm.getProperty("twitterOutputChannelName"), true).get(0)));
        thread.start();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getGuild().getId().equals(cm.getProperty("guildID"))) return;
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    channel.getName(), event.getAuthor(), message.getContentRaw());
        }
        if (message.getContentRaw().startsWith(commandPrefix)) {
            CommandParser commandParser = new CommandParser(event, commandPrefix);
            commandParser.parseCommand(jdaApi);
        } else {
            Thread memesThread = new Thread(new MemeInline(event));
            memesThread.start();
        }
    }


    @Override
    public void onUserUpdateGame(UserUpdateGameEvent event) {
        //System.out.printf("[%s][%s] switched from %s to %s%n", event.getGuild().getName(), event.getMember().getEffectiveName(), event.getOldGame(), event.getNewGame());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals(cm.getProperty("guildID"))) return;
        if (event.getUser().isBot()) return;
        //event.getAuthor().openPrivateChannel(.queue((userPM) -> userPM.sendMessage("Message").queue());
        String welcomeMessage = "Welcome to the Frontline! Here are a list of my commands:\r" + HelpMessageBuilder.getHelpMessage();
        event.getMember().getUser().openPrivateChannel().queue((userPM) -> userPM.sendMessage(welcomeMessage).queue());
        List<Guild> mutualGuilds = event.getUser().getMutualGuilds();
        for (Guild guild : mutualGuilds) {
            if (guild.getId().equals("260565533575872512")) {
                List<Role> userRoles = guild.getMemberById(event.getUser().getId()).getRoles();
                for (Role role : userRoles) {
                    if (role.getId().equals("443151138062073866")) {
                        event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById("451495511724130305")).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
    }
}