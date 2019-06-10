/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package emailConfirmation;

import bot.configuration.ConfigManager;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

class EmailManager {

    // Sender's email ID needs to be mentioned
    private ConfigManager cm = new ConfigManager();
    private String from;
    private String password;

    EmailManager() {
        this.from = cm.getProperty("email_from");
        this.password = cm.getProperty("email_password");
    }

    boolean sendEmail(Student student){
        String subjectLine = cm.getProperty("guildName") + " - Email Confirmation Code";
        String bodyMatter = "Hello " + student.getDiscordName() + ",\r\rPlease PM me back the confirmation code below:\r" + student.getConfirmationCode() + "\r\rThank you";

        final String username = from;
        final String password = this.password;

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(student.getSchoolEmail())
            );
            message.setSubject(subjectLine);
            message.setText(bodyMatter);

            Transport.send(message);

            System.out.println("Email sent");
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
