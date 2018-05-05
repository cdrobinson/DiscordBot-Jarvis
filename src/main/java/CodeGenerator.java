import java.util.Random;

class CodeGenerator {
    static String generate(int codeLength, int numberFloor, int numberCeiling) {
        Random rand = new Random();
        StringBuilder generatedCode = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            generatedCode.append(String.valueOf(rand.nextInt((numberCeiling - numberFloor) + 1) + numberFloor));
        }
        return generatedCode.toString();
    }

    static String generateEmailCode(){
        return generate(6, 0, 9);
    }
}
