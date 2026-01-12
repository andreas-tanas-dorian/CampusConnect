package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainLayoutController {

    // Singleton Instance so other controllers can access this layout
    private static MainLayoutController instance;

    @FXML private Label currentUserLabel;
    @FXML private BorderPane contentArea; // The middle part of the screen

    private StorageService storage;

    public static MainLayoutController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this; // Capture the running instance
        this.storage = App.storage;

        // Display current user
        try {
            String name = AppState.getInstance().getCurrentUser().getName();
            currentUserLabel.setText("User: " + name);
        } catch (Exception e) {
            currentUserLabel.setText("User: Guest");
        }

        // Load the Main Feed by default
        showFeed(null);
    }

    // --- NAVIGATION METHODS ---

    /**
     * Loads the Feed View.
     * @param groupId If null, shows Main Feed. If set, shows Group Feed.
     */
    public void showFeed(String groupId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/feed.fxml"));
            Parent view = loader.load();

            // Configure the FeedController
            FeedController controller = loader.getController();
            controller.setGroupFilter(groupId); // Pass the filter ID

            // Swap the screen
            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load feed.fxml");
        }
    }

    // Button click for "Feed" (Main Feed)
    @FXML
    private void showFeed() {
        showFeed(null);
    }

    @FXML
    private void showProfile() {
        loadView("profile_view");
    }

    @FXML
    private void showLeaderboard() {
        loadView("leaderboard");
    }

    @FXML
    private void showInbox() {
        loadView("inbox");
    }

    @FXML
    private void showQuestions() {
        // Loads the standalone Questions list into the center
        loadView("questions");
    }

    @FXML
    private void showGroups() {
        loadView("groups_view");
    }

    // --- POPUP ACTIONS ---

    @FXML
    private void handleAskPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/ask_question.fxml"));
            Parent root = loader.load();

            // Inject storage service manually just in case
            AskQuestionController controller = loader.getController();
            controller.setStorageService(this.storage);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks main window
            stage.setTitle("Ask a Question");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for close

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        AppState.getInstance().setCurrentUser(null);
        App.setRoot("login"); // Go back to login screen
    }

    // --- HELPER ---

    private void loadView(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/" + fxmlName + ".fxml"));
            Parent view = loader.load();

            // Special handling if the loaded view needs specific setup
            Object controller = loader.getController();
            if (controller instanceof QuestionsController) {
                ((QuestionsController) controller).setStorageService(storage);
            }

            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load view: " + fxmlName);
        }
    }
}