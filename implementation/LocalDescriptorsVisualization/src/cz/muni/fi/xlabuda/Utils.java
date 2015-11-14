package cz.muni.fi.xlabuda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utilities for application Local Descriptors Visualization
 * @author Marian Labuda
 * @version 1.0
 */
public class Utils {


    /**
     * Set property dir to current directory
     * @param directory new directory path
     */
    public static void setDirectoryProperty(File directory, String key) {
        if (directory.isDirectory()) {
            Properties properties = new Properties();
            File propFile = new File("config.properties");
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(propFile);
                properties.setProperty(key, directory.getAbsolutePath());
                properties.store(outputStream, null);
                outputStream.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public static String getDirectoryProperty(String key) {
        Properties properties = new Properties();
        File propFile = new File("config.properties");
        FileInputStream inputStream;
        String directory = null;
        try {
            inputStream = new FileInputStream(propFile);
            properties.load(inputStream);
            directory = properties.getProperty(key);
            inputStream.close();
        } catch (IOException e) {
        }
        return directory;
    }

    /**
     * Set directory of choosers to default = user directory.
     * Method is called once - when the program starts.
     */
    public static void setDefaultCurrentDirectory() {
        Properties properties = new Properties();
        File propFile = new File("config.properties");
        FileInputStream inputStream;
        FileOutputStream outputStream;
        if (propFile.exists()) {
            try {
                inputStream = new FileInputStream(propFile);
                properties.load(inputStream);
                inputStream.close();
                if (properties.getProperty("directory") == null) {
                    outputStream = new FileOutputStream(propFile);
                    properties.setProperty("directory", System.getProperty("user.dir"));
                    properties.store(outputStream, "config file");
                    outputStream.close();
                }
            } catch (IOException ex) {
            }
        } else {
            try {
                outputStream = new FileOutputStream(propFile);
                properties.setProperty("directory", System.getProperty("user.dir"));
                properties.store(outputStream, "config file");
                outputStream.close();
            } catch (IOException ex) {
            }
        }
    }
}