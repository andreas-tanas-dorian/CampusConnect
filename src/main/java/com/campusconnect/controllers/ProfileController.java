package com.campusconnect.controllers;

import com.campusconnect.models.Student;
import com.campusconnect.services.AppState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label groupLabel;
    @FXML private Label scoreLabel;

    @FXML
    public void initialize() {
        Student s = AppState.getInstance().getCurrentUser();
        if (s != null) {
            nameLabel.setText(s.getName());
            emailLabel.setText(s.getEmail());
            groupLabel.setText(s.getStudentGroup() + " (Year " + s.getStudyYear() + ")");
            scoreLabel.setText(String.valueOf(s.getScore()));
        }
    }
}