import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MyListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.equals("!ping")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }

        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getAuthor(), event.getMessage().getContentDisplay());
        }

        if (event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
            String privateMessage = event.getMessage().getContentRaw();
            if(privateMessage.contains("@bsu.edu")) {
                //String[] separatedWords = privateMessage.split(" ");
                MessageChannel channel = event.getChannel();
                channel.sendMessage("Sending a confirmation email to " + privateMessage).queue();
                channel.sendMessage("Please reply with the confirmation code sent to your email").queue();
                EmailManager emailManager = new EmailManager();
                CodeGenerator codeGen = new CodeGenerator();
                emailManager.sendConfirmationEmail(privateMessage, emailManager.buildConfirmationEmail(codeGen.generateEmailCode()));
            }
        }

    }
}
