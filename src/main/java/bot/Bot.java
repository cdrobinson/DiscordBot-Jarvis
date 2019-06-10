/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot;
import bot.basics.MainListener;
import bot.configuration.ConfigManager;
import bot.utilities.nowPlayingScheduler.Scheduler;
import easyModerator.Listener;
import music.MusicPlayerControl;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;


public class Bot {

    public static void main(String[] args) throws LoginException, InterruptedException {
        ConfigManager cm = new ConfigManager();
        JDA api = new JDABuilder(AccountType.BOT).setToken(cm.getBotToken()).build().awaitReady();
        api.addEventListener(new MainListener(api));
        api.addEventListener(new bot.interactiveHelpMessage.Listener());
        //api.addEventListener(new featureRequester.Listener());
        api.addEventListener(new frontline.Listener());
        api.addEventListener(new fun.Listener());
        api.addEventListener(new MusicPlayerControl());
        //api.addEventListener(new srTracking.Listener());
        api.addEventListener(new reactionRole.Listener());
        api.addEventListener(new Listener());
        api.setAutoReconnect(true);
        Thread nowPlayingThread = new Thread(new Scheduler(api));
        nowPlayingThread.start();
        /*Thread thread = new Thread(new Twitter_Manager(api.getGuildById(cm.getGuildId()).getTextChannelsByName(cm.getProperty("twitterOutputChannelName"), true).get(0)));
        thread.start();
        */
    }
}