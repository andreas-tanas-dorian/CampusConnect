package com.campusconnect.controllers;

import com.campusconnect.models.Question;
import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AskQuestionController {

    @FXML
    private TextArea questionInput;

    private StorageService storageService;

    // This method is called by the QuestionsController
    public void setStorageService(StorageService service) {
        this.storageService = service;
    }

    @FXML
    private void handleSubmit() {
        String content = questionInput.getText();

        if (content.trim().isEmpty()) {
            System.out.println("Question cannot be empty!");
            return;
        }

        try {
            // 1. Get current user
            String authorId = AppState.getInstance().getCurrentUser().getId();

            // 2. Create and Save Question
            Question q = new Question(authorId, content);

            if (storageService != null) {
                storageService.saveQuestion(q);
                System.out.println("Question saved!");
            }

            // 3. Close the window
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) questionInput.getScene().getWindow();
        stage.close();
    }
}