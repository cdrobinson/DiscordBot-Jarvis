import junit.framework.Assert;
import org.junit.Test;

public class EmailTests {

    @Test
    public void testSendingEmail(){
        EmailManager mailer = new EmailManager();
        Assert.assertEquals("Message sent", mailer.sendConfirmationEmail("cdrobinson2@bsu.edu", "Testing Message"));
    }
}
