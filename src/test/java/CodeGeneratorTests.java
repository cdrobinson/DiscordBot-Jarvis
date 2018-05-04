import junit.framework.Assert;
import org.junit.Test;

public class CodeGeneratorTests {

    @Test
    public void testGenerateEasyCode1(){
        CodeGenerator codeGen = new CodeGenerator();
        String easyCode = codeGen.generate(1,1, 1);
        Assert.assertEquals("1", easyCode);
    }

    @Test
    public void testGenerateEasyCode2(){
        CodeGenerator codeGen = new CodeGenerator();
        String easyCode = codeGen.generate(1,2, 2);
        Assert.assertEquals("2", easyCode);
    }

    @Test
    public void testGenerateEasyCode3(){
        CodeGenerator codeGen = new CodeGenerator();
        String easyCode = codeGen.generate(1,3, 3);
        Assert.assertEquals("3", easyCode);
    }

    @Test
    public void testGenerateLongCode(){
        CodeGenerator codeGen = new CodeGenerator();
        String easyCode = codeGen.generate(5,2, 2);
        Assert.assertEquals("22222", easyCode);
    }

    @Test
    public void testGenerateLongCodeRandom(){
        CodeGenerator codeGen = new CodeGenerator();
        String easyCode = codeGen.generate(5,0, 9);
        System.out.println(easyCode);
        Assert.assertEquals(5, easyCode.length());
    }

    @Test
    public void testGenerateEmailCode(){
        CodeGenerator codeGen = new CodeGenerator();
        String easyCode = codeGen.generateEmailCode();
        System.out.println(easyCode);
        Assert.assertEquals(6, easyCode.length());
    }
}
