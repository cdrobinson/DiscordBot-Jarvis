import junit.framework.Assert;
import org.junit.Test;

public class EmailTests {

    @Test
    public void testBuildConfirmationEmail(){
        EmailManager mailer = new EmailManager();
        String bodyText = EmailManager.buildConfirmationEmail("ConfirmationCode");
        Assert.assertEquals("Hello Cardinal! \nPlease PM your confirmation message back to the Charlie Cardinal bot." +
                "\nHere is your confirmation message: ConfirmationCodeConfirmationCode", bodyText);
    }

    @Test
    public void testBuildConfirmationEmailWithGenerator(){
        EmailManager mailer = new EmailManager();
        CodeGenerator codeGen = new CodeGenerator();
        String code = CodeGenerator.generateEmailCode();
        String bodyText = EmailManager.buildConfirmationEmail(code);
        Assert.assertEquals("Hello Cardinal! \nPlease PM your confirmation message back to the Charlie Cardinal bot." +
                "\nHere is your confirmation message: ConfirmationCode" +code, bodyText);
    }
}
