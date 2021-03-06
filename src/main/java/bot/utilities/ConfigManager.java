/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package bot.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private Properties prop;

    public ConfigManager() {
        this.prop = new Properties();
        InputStream input = null;
        String filename = "config.properties";

        try {
            input = getClass().getClassLoader().getResourceAsStream(filename);
            if(input==null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }
            //load a properties file from class path, inside static method
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String key) {
        return this.prop.getProperty(key);
    }
}
