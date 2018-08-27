import java.io.*;
import java.util.HashMap;

class FileManager {

    void writeToTextFile(String textToWrite, String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(fileLocation + "/textFiles/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(textToWrite);

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            writeToTextFile(e.toString(), "ErrorLog.txt");
        }
    }

    String readFromTextFile(String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            FileReader fileReader = new FileReader(fileLocation + "/textFiles/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int i;
            String fileAsString = "";
            while((i=bufferedReader.read())!=-1){
                fileAsString = fileAsString.concat(String.valueOf((char)i));
            }
            bufferedReader.close();
            fileReader.close();
            return fileAsString;
        } catch (FileNotFoundException e) {
            System.out.println("There is currently no " + fileName + " file.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            writeToTextFile(e.toString(), "ErrorLog.txt");
            return null;
        }
    }

    HashMap<String, Integer> parseStorageFile(String fileContent) {
        if (fileContent != null) {
            HashMap<String, Integer> parsedContent = new HashMap<>();
            if(fileContent.length() > 2) {
                fileContent = fileContent.substring(1, fileContent.length() - 1);
                String[] contentAsList = fileContent.split(", ");

                for (String listEntry : contentAsList) {
                    String[] userInfo = listEntry.split("=");
                    parsedContent.put(userInfo[0], Integer.valueOf(userInfo[1]));
                }
            }
            return parsedContent;
        } else {
            return new HashMap<>();
        }
    }

    File getFile(String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            File file = new File(fileLocation + "/images/" + fileName);
            Boolean actualFileExists = file.exists();
            if(actualFileExists) {
                return file;
            } else {
                System.out.printf("%s does not exist", fileName);
            }
        }catch (IllegalArgumentException e) {
            System.out.printf("There was an IllegalArgumentException error with queueing the %s", fileName);
            e.printStackTrace();
        }
        return null;
    }
}
