import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

class UserInputManager {

    static void createPoll(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();
        String[] parameters = content.split("!vote ")[1].split(", ");
        if (parameters.length < 12) {
            String voteMessage = buildVoteMessage(parameters);
            event.getMessage().delete().queue();
            channel.sendMessage(voteMessage).queue((postedVote) -> {
                for (int i=0; i < parameters.length; i++) {
                    postedVote.addReaction(integerToEmoji(i)).queue();
                }
            });
        } else {
            channel.sendMessage("I currently cannot handle more than 10 voting options").queue();
        }
    }

    private static String buildVoteMessage(String[] parameters) {
        StringBuilder voteMessage = new StringBuilder();
        voteMessage.append("Please select the option you would like to vote for. \r");
        for (int i=0; i < parameters.length; i++) {
            voteMessage.append(":");
            voteMessage.append(integerToWord(i));
            voteMessage.append(":");
            voteMessage.append(" ");
            voteMessage.append(parameters[i]);
            voteMessage.append("\r");
        }
        return voteMessage.toString();
    }

    private static String integerToWord(Integer number) {
        String word = null;
        switch (number) {
            case 0:
                word = "zero";
                break;
            case 1:
                word = "one";
                break;
            case 2:
                word = "two";
                break;
            case 3:
                word = "three";
                break;
            case 4:
                word = "four";
                break;
            case 5:
                word = "five";
                break;
            case 6:
                word = "six";
                break;
            case 7:
                word = "seven";
                break;
            case 8:
                word = "eight";
                break;
            case 9:
                word = "nine";
                break;
            default:
                break;
        }
        return word;
    }

    private static String integerToEmoji(Integer number) {
        String word = null;
        switch (number) {
            case 0:
                word = "0⃣";
                break;
            case 1:
                word = "1⃣";
                break;
            case 2:
                word = "2⃣";
                break;
            case 3:
                word = "3⃣";
                break;
            case 4:
                word = "4⃣";
                break;
            case 5:
                word = "5⃣";
                break;
            case 6:
                word = "6⃣";
                break;
            case 7:
                word = "7⃣";
                break;
            case 8:
                word = "8⃣";
                break;
            case 9:
                word = "9⃣";
                break;
            default:
                break;
        }
        return word;
    }
}
