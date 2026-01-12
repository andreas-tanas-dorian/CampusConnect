package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Question;
import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class QuestionsController {

    @FXML private VBox questionsListContainer;

    private StorageService storageService;
    private Timeline chronometer;

    // Called automatically when FXML loads
    @FXML
    public void initialize() {
        // If loaded manually, storage might be null initially.
        // We set it from App.storage for safety, though MainLayout sets it too.
        this.storageService = App.storage;
        startChronometer();
    }

    public void setStorageService(StorageService service) {
        this.storageService = service;
        refreshQuestionsUI();
    }

    private void startChronometer() {
        if (chronometer != null) chronometer.stop();
        chronometer = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshQuestionsUI()));
        chronometer.setCycleCount(Timeline.INDEFINITE);
        chronometer.play();
    }

    public void refreshQuestionsUI() {
        if (storageService == null || questionsListContainer == null) return;

        try {
            questionsListContainer.getChildren().clear();
            List<Question> openQuestions = storageService.getOpenQuestions();

            if (openQuestions.isEmpty()) {
                questionsListContainer.getChildren().add(new Label("No pending questions."));
            } else {
                for (Question q : openQuestions) {
                    questionsListContainer.getChildren().add(createQuestionRow(q));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node createQuestionRow(Question q) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/question_item.fxml"));

            // Reuse the QuestionRowController logic we built earlier
            QuestionRowController controller = new QuestionRowController();
            loader.setController(controller);

            Node node = loader.load();
            controller.setData(q, this::handleResolve);

            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error loading row");
        }
    }

    private void handleResolve(Question q) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Resolve Ticket");
        dialog.setHeaderText("Replying to: " + q.getAuthorId());
        dialog.setContentText("Enter your answer:");

        dialog.showAndWait().ifPresent(answer -> {
            if (answer.trim().isEmpty()) return;
            try {
                String currentUserId = AppState.getInstance().getCurrentUser().getId();
                storageService.resolveQuestion(q, answer, currentUserId);
                refreshQuestionsUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleAskQuestion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/ask_question.fxml"));
            Parent root = loader.load();
            AskQuestionController controller = loader.getController();
            controller.setStorageService(this.storageService);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ask a Question");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshQuestionsUI();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Ensure thread stops when view is destroyed (Optional but good practice)
    public void stopTimer() {
        if (chronometer != null) chronometer.stop();
    }
}