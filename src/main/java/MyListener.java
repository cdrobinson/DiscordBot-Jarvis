import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;



public class MyListener extends ListenerAdapter {

    private ConfirmationTracker confirmationTracker;

    MyListener() {
        this.confirmationTracker = new ConfirmationTracker();
    }

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
        if(privateMessage.contains("ConfirmationCode")) {
            String receivedConfirmationCode = event.getMessage().getContentRaw();
            String[] separatedWords = receivedConfirmationCode.split(" ");
            if (separatedWords.length > 1) {
                channel.sendMessage("Please only send me just your confirmation code").queue();
                return;
            }
            String parsedConfirmationCode = receivedConfirmationCode.split("ConfirmationCode")[1];
            String storedConfirmationCode = confirmationTracker.checkListByUser(event.getAuthor());
            if(storedConfirmationCode == null) {
                channel.sendMessage("Your username didn't appear in our database, please send me your BSU email again.").queue();
                return;
            }
            if(!storedConfirmationCode.equals(parsedConfirmationCode)) {
                channel.sendMessage("The confirmation code you sent me was incorrect, please recopy the code from your email.").queue();
                return;
            }
            if (parsedConfirmationCode.equals(storedConfirmationCode)) {
                channel.sendMessage("Thank you for confirming your BSU email.").queue();
                confirmationTracker.removeConfirmation(event.getAuthor());
                RoleChanger.makeRegisteredUser(event.getAuthor());
            }
        }
    }
}
