import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, InterruptedException {
        new JDABuilder(AccountType.BOT).setToken("NDMyNzU1ODM5MzkzMjAyMTg3.DcjmKw.qyehHPgbwLccSw0kTxyGXD9jbKw").buildBlocking();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Received a message");
    }
}
