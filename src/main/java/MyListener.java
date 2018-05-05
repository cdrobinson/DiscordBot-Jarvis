import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MyListener extends ListenerAdapter {

    private ConfirmationTracker confirmationTracker;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourselves
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
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
        String privateMessage = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();
        if(privateMessage.contains("@bsu.edu")) {
            String[] separatedWords = privateMessage.split(" ");
            if (separatedWords.length > 1) {
                channel.sendMessage("Please only send me just your Ball State email").queue();
                return;
            }
            channel.sendMessage("Sending a confirmation email to " + privateMessage).queue();
            channel.sendMessage("Please reply with the confirmation message sent to your email").queue();
            String generatedCode = CodeGenerator.generateEmailCode();
            EmailManager.sendConfirmationEmail(privateMessage, EmailManager.buildConfirmationEmail(generatedCode));
            confirmationTracker.addConfirmation(event.getAuthor(), generatedCode);
        }
        //TODO fix the hashmap so that it accepts Users and stores the data for when the user sends back the confirmation message
        if(privateMessage.contains("ConfirmationCode")) {
            if (confirmationTracker.checkListByUser(event.getAuthor()).equals(null)) {
                channel.sendMessage("The confirmation code you entered could not be found.").queue();
            } else {
                channel.sendMessage("Your BSU email has been confirmed, your roles have been updated.").queue();
                confirmationTracker.removeConfirmation(event.getAuthor());
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Charlie Cardinal reporting for duty!");
        this.confirmationTracker = new ConfirmationTracker();
    }
}
