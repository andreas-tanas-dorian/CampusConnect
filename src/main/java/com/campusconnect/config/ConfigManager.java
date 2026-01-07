package com.campusconnect.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handles loading settings from a file and overriding them with command line args.
 */
public class ConfigManager {
    private Properties properties = new Properties();
    private boolean debugMode = false;
    private String storageType = "FILE"; // Default

    public ConfigManager(String[] args) {
        // 1. Load from file
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            storageType = properties.getProperty("storage.type", "FILE");
        } catch (IOException e) {
            System.out.println("Warning: config.properties not found. Using defaults.");
        }

        // 2. Override with arguments
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