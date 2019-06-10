/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package emailConfirmation;

import java.util.Random;

class Student {

    private String discordID;
    private String discordName;
    private String schoolEmail;
    private String confirmationCode;

    Student(String discordID, String discordName, String schoolEmail) {
        this.discordID = discordID;
        this.discordName = discordName;
        this.schoolEmail = schoolEmail;
        this.confirmationCode = generateNewCode();
    }

    Student(String discordID, String discordName, String schoolEmail, String confirmationCode) {
        this.discordID = discordID;
        this.discordName = discordName;
        this.schoolEmail = schoolEmail;
        this.confirmationCode = confirmationCode;
    }

    private String generateNewCode() {
        Random rand = new Random();
        StringBuilder generatedCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            generatedCode.append(String.valueOf(rand.nextInt((9) + 1)));
        }
        return generatedCode.toString();
    }

    String getDiscordID() {
        return discordID;
    }

    String getSchoolEmail() {
        return schoolEmail;
    }

    String getConfirmationCode() {
        return confirmationCode;
    }

    String getDiscordName() {
        return discordName;
    }
}
