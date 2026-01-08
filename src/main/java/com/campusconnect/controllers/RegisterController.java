package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.UUID;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField specField;
    @FXML private TextField yearField;
    @FXML private TextField groupField;

    @FXML private Label statusLabel;

    @FXML
    private void handleRegister() {
        try {
            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Year must be a number.");
            }

            String id = UUID.randomUUID().toString();

            Student newStudent = new Student(
                    id,
                    emailField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    specField.getText(),
                    year,
                    groupField.getText()
            );

            newStudent.validate();

            App.storage.saveStudent(newStudent);

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Success! Please log in.");

        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws Exception {
        App.setRoot("login");
    }
}