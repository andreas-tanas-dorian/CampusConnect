package com.campusconnect;

import com.campusconnect.config.ConfigManager;
import com.campusconnect.storage.FileStorageService;
import com.campusconnect.storage.StorageService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    public static StorageService storage;
    public static ConfigManager config;

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Initialize Config
        config = new ConfigManager(new String[]{}); // Pass args from main here if needed

        // 2. Initialize Storage based on Config
        if (config.getStorageType().equals("DB")) {
            // storage = new DatabaseStorageService(...);
            System.out.println("DB Mode selected (Not fully impl in this snippet)");
        } else {
            storage = new FileStorageService();
            try {
                storage.loadData(); // Load CSVs into memory
            } catch (Exception e) {
                System.err.println("Failed to load files: " + e.getMessage());
            }
        }

        // 3. Load Login Screen
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Campus Connect");
        stage.show();
    }

    // Helper to switch views from Controllers
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/campusconnect/views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}