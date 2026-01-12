package com.campusconnect;

import com.campusconnect.config.ConfigManager;
import com.campusconnect.storage.SocketStorageService; // <--- CHANGED IMPORT
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
        config = new ConfigManager(new String[]{});

        System.out.println("Initializing Client-Server Connection...");
        storage = new SocketStorageService();
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Campus Connect (Client Mode)");
        stage.show();
    }

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