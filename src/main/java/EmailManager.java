import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class EmailManager {
    static String sendConfirmationEmail(String recipientEmail, String emailText) {

        // Sender's email ID needs to be mentioned
        String from = "ballstateesports@gmail.com";
        final String username = "ballstateesports@gmail.com";//change accordingly
        final String password = "iloveteemo";//change accordingly

        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));

            // Set Subject: header field
            message.setSubject("Cardinal Esports - Confirmation Code");

            // Now set the actual message
            message.setText(emailText);

            // Send message
            Transport.send(message);

            System.out.println("[Confirmation Code] Sent confirmation code successfully to " + recipientEmail);
            return "Message sent";

        } catch (MessagingException e) {
            System.out.println(e.toString());
            return e.toString();
        }
    }

    static String buildConfirmationEmail(String confirmationCode) {
        return "Hello Cardinal! " +
                "\nPlease PM your confirmation message back to the Charlie Cardinal bot." +
                "\nHere is your confirmation message: ConfirmationCode" + confirmationCode;
    }
}
