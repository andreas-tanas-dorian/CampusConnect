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
            String authorId = AppState.getInstance().getCurrentUser().getId();

            Question q = new Question(authorId, content);

            if (storageService != null) {
                storageService.saveQuestion(q);
                System.out.println("Question saved!");
            }

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