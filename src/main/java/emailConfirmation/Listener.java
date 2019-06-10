/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package emailConfirmation;

import bot.configuration.ConfigManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listener extends ListenerAdapter {

    private ConfigManager cm;
    private final static String initialMessage = "Please enter a valid school email:";
    private final static String privacyErrorMessage = "You need to adjust your privacy settings before I can message you\n" +
            "Instructions:```\n" +
            "1. Settings (gear icon)\n" +
            "2. Privacy & Saftey tab\n" +
            "3. Turn on 'Allow direct messages from server members.'\n" +
            "4. Rerun the !register command" +
            "```";
    private ArrayList<Student> studentsPendingConfirmation;
    private JDA api;

    public Listener(JDA api) {
        this.api = api;
        this.studentsPendingConfirmation = new ArrayList<>();
        this.cm = new ConfigManager();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (!event.getGuild().getId().equals(cm.getGuildId())) return;
        String message = event.getMessage().getContentRaw();
        if (message.startsWith(cm.getCommandPrefix())) {
            switch (message.substring(1)) {
                case "register":
                    sendUserInitialPM(event);
                    event.getMessage().delete().queue();
                    break;
                case "confirmed":
                    if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        sendUserConfirmedList(event);
                        event.getChannel().sendMessage("I have sent the list to you in a PM").queue();
                        break;
                    } else {
                        event.getChannel().sendMessage("You don't have permission to run that command").queue();
                        break;
                    }
                default:
                    break;
            }
        }
    }

    private void sendUserConfirmedList(MessageReceivedEvent event) {
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        ArrayList<Student> allStudents = mongoDbConnector.getAllRegisteredStudents();
        String studentListFormatted = formatStudentListForPlainText(allStudents);
        mongoDbConnector.endConnection();
        event.getMember().getUser().openPrivateChannel().queue((userPM) ->
                userPM.sendMessage(studentListFormatted).queue(), (CANNOT_SEND_TO_USER) -> event.getChannel().sendMessage(privacyErrorMessage).queue());

    }

    private String formatStudentListForPlainText(ArrayList<Student> allStudents) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("```");
        for (Student student : allStudents) {
            stringBuilder.append(student.getDiscordID());
            stringBuilder.append(", ");
            stringBuilder.append(student.getDiscordName());
            stringBuilder.append(", ");
            stringBuilder.append(student.getSchoolEmail());
            stringBuilder.append("\r");
        }
        stringBuilder.append("```");
        return stringBuilder.toString();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (messageContainsSchoolEmail(event.getMessage().getContentRaw())) {
            parseEmail(event);
            return;
        }
        Student student = studentIsPendingConfirmation(event.getAuthor().getId());
        if (student != null) {
            parseConfirmationCode(event, student);
        }
    }

    private void parseConfirmationCode(PrivateMessageReceivedEvent event, Student student) {
        List<String> messageAsList = Arrays.asList(event.getMessage().getContentRaw().split(" "));
        if (messageAsList.size() > 1) {
            //ErrorHandling
            event.getChannel().sendMessage("Make sure you are sending just your confirmation code.").queue();
            return;
        }
        if (student.getConfirmationCode().equals(messageAsList.get(0))) {
            event.getChannel().sendMessage("Thank you for confirming your email!").queue();
            approveStudent(event, student);
        }
    }

    private void approveStudent(PrivateMessageReceivedEvent event, Student student) {
        studentsPendingConfirmation.remove(student);
        Guild guild = api.getGuildById(cm.getGuildId());
        guild.getController().addSingleRoleToMember(guild.getMemberById(student.getDiscordID()), guild.getRoleById(cm.getProperty("confirmedRoleID"))).queue();
        if (!storeStudentInfo(student)) {
            event.getChannel().sendMessage("There was an error adding your info to the confirmed list. Please notify an admin.").queue();
        }
    }

    private boolean storeStudentInfo(Student student) {
        MongoDbConnector mongoDbConnector = new MongoDbConnector();
        boolean worked = mongoDbConnector.storeStudentInfo(student);
        mongoDbConnector.endConnection();
        return worked;
    }

    private Student studentIsPendingConfirmation(String discordID) {
        for (Student student : studentsPendingConfirmation) {
            if (student.getDiscordID().equals(discordID)) return student;
        }
        return null;
    }

    private void parseEmail(PrivateMessageReceivedEvent event) {
        List<String> messageAsList = Arrays.asList(event.getMessage().getContentRaw().split(" "));
        if (messageAsList.size() > 1) {
            //ErrorHandling
            event.getChannel().sendMessage("Make sure you are sending just your email as the message ie. `google@google.com`").queue();
            return;
        }
        String userEmail = messageAsList.get(0);
        for (Student storedStudent : studentsPendingConfirmation) {
            if (storedStudent.getSchoolEmail().equals(userEmail)) {
                event.getChannel().sendMessageFormat("I have already sent an email to %s.", userEmail).queue();
                return;
            }
        }
        Student student = new Student(event.getAuthor().getId(), event.getAuthor().getAsTag(), userEmail);
        if (sendConfirmationCode(student)) {
            event.getChannel().sendMessageFormat("An email confirmation has been sent to %s", userEmail).queue();
            addStudentToPendingList(student);
        } else {
            event.getChannel().sendMessageFormat("There was an error sending the email confirmation to %s", userEmail).queue();
        }
    }

    private void addStudentToPendingList(Student student) {
        studentsPendingConfirmation.add(student);
    }

    private boolean sendConfirmationCode(Student student) {
        EmailManager emailManager = new EmailManager();
        //emailManager.sendConfirmationEmail(student);
        return emailManager.sendEmail(student);
    }

    private boolean messageContainsSchoolEmail(String contentRaw) {
        if (contentRaw.contains("@") && contentRaw.endsWith(".edu")) {
            return contentRaw.indexOf("@") <= contentRaw.indexOf(".edu");
        }
        return false;
    }

    private void sendUserInitialPM(MessageReceivedEvent event) {
        event.getMember().getUser().openPrivateChannel().queue((userPM) ->
                userPM.sendMessage(initialMessage).queue(), (CANNOT_SEND_TO_USER) -> event.getChannel().sendMessage(privacyErrorMessage).queue());
    }
}
