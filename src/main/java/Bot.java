import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import javax.security.auth.login.LoginException;

public class Bot {

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = new JDABuilder(AccountType.BOT).setToken("NDY5NzI1MzgyODU4MjQ0MDk5.DjL9jQ.NrmqdL4jUiyIknGAtdlpGU_w9_M").build().awaitReady();
        api.addEventListener(new MyListener(api));
        api.addEventListener(new MusicPlayerControl());
        api.setAutoReconnect(true);
        api.getPresence().setGame(Game.playing("Minecraft VR"));
    }
}
