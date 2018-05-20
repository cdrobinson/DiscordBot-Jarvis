import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class Bot {

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = new JDABuilder(AccountType.BOT).setToken("NDMyNzU1ODM5MzkzMjAyMTg3.DcjmKw.qyehHPgbwLccSw0kTxyGXD9jbKw").buildBlocking();
        MyListener myListener = new MyListener();
        api.addEventListener(myListener);
        api.setAutoReconnect(true);
        api.getPresence().setGame(Game.playing("with your tuition money \nTwitter: @bsuesports"));
    }
}
