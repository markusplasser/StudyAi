package org.example.manage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + "StudyAi";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.properties";

    private Properties prop = new Properties();


    /**
     * loads ot initializes the project default save folder and reads/creates the properties file
     */
    public void loadOrInitialize() {
        File defaultFolder = new File(CONFIG_DIR);
        File propFile = new File(CONFIG_FILE);

        if(!defaultFolder.exists() && !propFile.exists()) {
            firstStart(defaultFolder, propFile);
        }
        else{
            loadProp(propFile);
        }
    }

    /**
     * creates the default save folder and prop file for the project
     * @param defaultFolder defaultFolder
     * @param propFile propFile
     */
    private void firstStart(File defaultFolder, File propFile) {
        if(!defaultFolder.exists()){
            defaultFolder.mkdir();
        }


        prop.setProperty("Project_Save_Path", CONFIG_DIR);
        prop.setProperty("Project_Config_Path", CONFIG_FILE);
        prop.setProperty("Project_Save_File", CONFIG_DIR + File.separator + "save");
        prop.setProperty("API_KEY","");
        prop.setProperty("FIRST_TIME","true");

        try (FileOutputStream out = new FileOutputStream(propFile)) {
            prop.store(out, "Automatisch generiert beim ersten Start");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProp(File propFile) {
        try (FileInputStream in = new FileInputStream(propFile)) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean firstStart() {
        String firstTime = prop.getProperty("FIRST_TIME");
        return firstTime == null || firstTime.equals("true");
    }

    public Properties getProperties() {
        return prop;
    }

    /**
     * saves API KEY and sets the first time property to false
     * @param apiKey API KEY
     */
    public void saveApiKey(String apiKey) {
        prop.setProperty("API_KEY", apiKey);
        prop.setProperty("FIRST_TIME", "false");
        saveToFile();
    }

    private void saveToFile() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            prop.store(out, "Automatisch generiert");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
