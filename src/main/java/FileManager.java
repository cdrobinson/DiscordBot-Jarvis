import java.io.File;

class FileManager {

    File getFile(String fileName) {
        try {
            String fileLocation = System.getProperty("user.dir");
            File file = new File(fileLocation + "/images/" + fileName);
            boolean actualFileExists = file.exists();
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