import java.io.*;

class FileManager {

    void writeToFile(String textToWrite, String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(fileLocation + "/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(textToWrite);

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            writeToFile(e.toString(), "ErrorLog.txt");
        }
    }

    String readFromFile(String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            FileReader fileReader = new FileReader(fileLocation + "/" + fileName);
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
            writeToFile(e.toString(), "ErrorLog.txt");
            return null;
        }
    }
}
