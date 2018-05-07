import junit.framework.Assert;
import org.junit.Test;

public class EmailTests {

    @Test
    public void testBuildConfirmationEmail(){
        String bodyText = EmailManager.buildConfirmationEmail("ConfirmationCode");
        Assert.assertEquals("Hello Cardinal! \nPlease PM your confirmation message back to the Charlie Cardinal bot." +
                "\nHere is your confirmation message: ConfirmationCodeConfirmationCode", bodyText);
    }

    @Test
    public void testBuildConfirmationEmailWithGenerator(){
        String code = CodeGenerator.generateEmailCode();
        String bodyText = EmailManager.buildConfirmationEmail(code);
        Assert.assertEquals("Hello Cardinal! \nPlease PM your confirmation message back to the Charlie Cardinal bot." +
                "\nHere is your confirmation message: ConfirmationCode" +code, bodyText);
    }
}
