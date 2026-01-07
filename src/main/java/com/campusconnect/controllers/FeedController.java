package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Student;
import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService; // Import the interface

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality; // FIXED: Added this import
import javafx.stage.Stage;

import java.io.IOException; // FIXED: Added this import

public class FeedController {

    // UI Elements for Profile
    @FXML private Label userNameLabel;
    @FXML private Label userDetailLabel;
    @FXML private Label userScoreLabel;

    // Sub-controller injection
    // The fx:id in feed.fxml is "questionsPanel", so JavaFX looks for "questionsPanelController"
    @FXML private QuestionsController questionsPanelController;

    private StorageService storage;

    @FXML
    public void initialize() {
        // 1. Get the global storage
        this.storage = App.storage;

        // 2. Pass storage to the included "Questions" sidebar
        if (questionsPanelController != null) {
            questionsPanelController.setStorageService(storage);
        }

        // 3. Load Current User Data for the Profile Card
        Student currentUser = AppState.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getName());

            // Format: "Specialization - Year X - Group Y"
            // We use safe checks in case fields are null (e.g. admin user)
            String spec = currentUser.getSpecialization() != null ? currentUser.getSpecialization() : "General";
            String group = currentUser.getStudentGroup() != null ? currentUser.getStudentGroup() : "N/A";

            String details = String.format("%s - Year %d - %s", spec, currentUser.getStudyYear(), group);
            userDetailLabel.setText(details);

            // Display Score
            userScoreLabel.setText(String.valueOf(currentUser.getScore()));
        }
    }

    @FXML
    private void openLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/leaderboard.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Campus Connect - Leaderboard");
            stage.setScene(new Scene(root));

            // Blocks interaction with the main window until leaderboard is closed
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load leaderboard.fxml");
        }
    }

    @FXML
    private void openInbox() {
        // Placeholder for existing inbox logic
        System.out.println("Opening Inbox...");
    }

    // --- Existing Feed Handlers (Stubs to prevent FXML errors if buttons exist) ---

    @FXML private void handleCreateGroup() {
        System.out.println("Create Group Clicked");
    }

    @FXML private void handlePost() {
        System.out.println("Post Clicked");
    }

    @FXML private void handleAttachImage() {
        System.out.println("Attach Image Clicked");
    }

    @FXML private void handleShowNotifications() {
        openInbox();
    }

    @FXML
    private void handleLogout() throws IOException {
        AppState.getInstance().setCurrentUser(null);
        App.setRoot("login");
    }
}