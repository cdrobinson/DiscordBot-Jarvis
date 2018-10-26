import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class Bot {

    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();
        try {
            JDA api = new JDABuilder(AccountType.BOT).setToken(configManager.getProperty("botToken")).build().awaitReady();
            api.addEventListener(new MainListener(api));
            api.addEventListener(new MusicPlayerControl());
            api.setAutoReconnect(true);
            api.getPresence().setGame(Game.playing(configManager.getProperty("defaultPlaying")));
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
            System.out.println("There was an error loading the bot. There was an Interrupted Exception error");
        }
    }
}