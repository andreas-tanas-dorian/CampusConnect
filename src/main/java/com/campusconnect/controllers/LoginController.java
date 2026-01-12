package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.exceptions.AuthException;
import com.campusconnect.models.Student;
import com.campusconnect.services.AppState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String pass = passwordField.getText();

        try {
            if(email.isEmpty() || pass.isEmpty()) {
                throw new IllegalArgumentException("Fields cannot be empty");
            }

            Student s = App.storage.findStudentByEmail(email);
            if (s == null || !s.getPassword().equals(pass)) {
                throw new AuthException("Invalid credentials");
            }

            AppState.getInstance().setCurrentUser(s);
            App.setRoot("main_layout");

        } catch (IllegalArgumentException | AuthException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister() throws IOException {
        App.setRoot("register");
    }
}