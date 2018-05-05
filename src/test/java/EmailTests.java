import junit.framework.Assert;
import org.junit.Test;

public class EmailTests {

    @Test
    public void testBuildConfirmationEmail(){
        EmailManager mailer = new EmailManager();
        String bodyText = mailer.buildConfirmationEmail("Confirmation Code");
        Assert.assertEquals("Hello Cardinal! \n Please PM your confirmation code back to the Charlie Cardinal bot." +
                "\n Here is your confirmation code: Confirmation Code", bodyText);
    }

    @Test
    public void testBuildConfirmationEmailWithGenerator(){
        EmailManager mailer = new EmailManager();
        CodeGenerator codeGen = new CodeGenerator();
        String code = codeGen.generateEmailCode();
        String bodyText = mailer.buildConfirmationEmail(code);
        Assert.assertEquals("Hello Cardinal! \n Please PM your confirmation code back to the Charlie Cardinal bot." +
                "\n Here is your confirmation code: " +code, bodyText);
    }
}
