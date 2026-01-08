package com.campusconnect.controllers;

import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService;
import com.campusconnect.models.Question;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @FXML
    private VBox questionsListContainer;

    private StorageService storageService;
    private Timeline chronometer;

    public void setStorageService(StorageService service) {
        this.storageService = service;
        startChronometer();
    }

    private void startChronometer() {
        if (chronometer != null) chronometer.stop();
        chronometer = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshQuestionsUI()));
        chronometer.setCycleCount(Timeline.INDEFINITE);
        chronometer.play();
    }

    private void refreshQuestionsUI() {
        if (storageService == null) return;
        try {
            questionsListContainer.getChildren().clear();
            List<Question> openQuestions = storageService.getOpenQuestions();

            if (openQuestions.isEmpty()) {
                Label emptyLabel = new Label("No pending questions.");
                emptyLabel.setStyle("-fx-text-fill: gray; -fx-padding: 10;");
                questionsListContainer.getChildren().add(emptyLabel);
            } else {
                for (Question q : openQuestions) {
                    questionsListContainer.getChildren().add(createQuestionRow(q));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createQuestionRow(Question q) {
        VBox card = new VBox();
        card.getStyleClass().add("question-card");
        card.setStyle("-fx-border-color: #444444; -fx-border-radius: 5; -fx-background-color: #2b2b2b; -fx-padding: 8; -fx-spacing: 5;");
        Label contentLabel = new Label(q.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        long secondsWaiting = ChronoUnit.SECONDS.between(q.getTimestamp(), LocalDateTime.now());
        String color = (secondsWaiting > 300) ? "red" : (secondsWaiting > 60) ? "orange" : "green";

        Label timeLabel = new Label("Wait Time: " + formatTime(secondsWaiting));
        timeLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px;");

        Button resolveBtn = new Button("Answer & Resolve");
        resolveBtn.setMaxWidth(Double.MAX_VALUE);
        resolveBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        resolveBtn.setOnAction(e -> handleResolve(q));

        card.getChildren().addAll(contentLabel, timeLabel, resolveBtn);
        return card;
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

    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @FXML
    private void handleAskQuestion() {
        if (storageService == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/AskQuestion.fxml"));
            Parent root = loader.load();

            AskQuestionController controller = loader.getController();
            controller.setStorageService(this.storageService);

            Stage stage = new Stage();
            stage.setTitle("Ask a Question");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshQuestionsUI();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load AskQuestion.fxml");
        }
    }

    public void stopTimer() {
        if (chronometer != null) chronometer.stop();
    }
}