import bot.basics.MainListener;
import bot.configuration.ConfigManager;
import music.MusicPlayerControl;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import twitter.Twitter_Manager;

import javax.security.auth.login.LoginException;

public class Bot {

    private static final String botToken = "NDY5NzI1MzgyODU4MjQ0MDk5.DjL9jQ.NrmqdL4jUiyIknGAtdlpGU_w9_M";
    private static final String defaultPlaying = "Minecraft VR";

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = new JDABuilder(AccountType.BOT).setToken(botToken).build().awaitReady();
        api.addEventListener(new MainListener(api));
        api.addEventListener(new MusicPlayerControl());
        api.addEventListener(new srTracking.Listener());
        api.addEventListener(new featureRequester.Listener());
        api.addEventListener(new fun.Listener());
        api.addEventListener(new frontline.Listener());
        api.setAutoReconnect(true);
        api.getPresence().setGame(Game.playing(defaultPlaying));

        ConfigManager cm = new ConfigManager();
        Thread thread = new Thread(new Twitter_Manager(api.getGuildById(cm.getGuildId()).getTextChannelsByName(cm.getProperty("twitterOutputChannelName"), true).get(0)));
        thread.start();
    }


}