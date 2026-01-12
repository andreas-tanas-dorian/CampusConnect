package com.campusconnect.controllers;

import com.campusconnect.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class QuestionRowController {

    @FXML private VBox rootBox;
    @FXML private Label contentLabel;
    @FXML private Label timeLabel;

    private Question question;
    private Consumer<Question> onResolve;

    public void setData(Question q, Consumer<Question> onResolveCallback) {
        this.question = q;
        this.onResolve = onResolveCallback;

        contentLabel.setText(q.getContent());

        // Time Calculation logic
        long secondsWaiting = ChronoUnit.SECONDS.between(q.getTimestamp(), LocalDateTime.now());
        String color = (secondsWaiting > 300) ? "red" : (secondsWaiting > 60) ? "orange" : "green";

        long minutes = secondsWaiting / 60;
        long seconds = secondsWaiting % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);

        timeLabel.setText("Wait Time: " + timeText);
        timeLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px;");
    }

    @FXML
    private void handleResolve() {
        if (onResolve != null) onResolve.accept(question);
    }
}