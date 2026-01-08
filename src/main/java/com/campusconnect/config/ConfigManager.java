package com.campusconnect.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private Properties properties = new Properties();
    private boolean debugMode = false;
    private String storageType = "FILE";

    public ConfigManager(String[] args) {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            storageType = properties.getProperty("storage.type", "FILE");
        } catch (IOException e) {
            System.out.println("Warning: config.properties not found. Using defaults.");
        }

        for (String arg : args) {
            if (arg.equals("--debug")) {
                debugMode = true;
            } else if (arg.equals("--use-db")) {
                storageType = "DB";
            }
        }
    }

    public boolean isDebugMode() { return debugMode; }
    public String getStorageType() { return storageType; }
    public String getDbUrl() { return properties.getProperty("db.url"); }
    public String getDbUser() { return properties.getProperty("db.user"); }
    public String getDbPass() { return properties.getProperty("db.pass"); }
}