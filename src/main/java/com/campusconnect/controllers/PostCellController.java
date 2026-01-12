package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Post;
import com.campusconnect.models.Student;
import com.campusconnect.services.AppState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class PostCellController extends ListCell<Post> {

    @FXML private Label authorLabel;
    @FXML private Label contentLabel;
    @FXML private Label imageLabel;
    @FXML private Button deleteButton;

    private VBox rootNode;
    private Post currentPost;

    private final Map<String, Student> studentCache;
    private final Consumer<Post> onDeleteAction;

    public PostCellController(Map<String, Student> cache, Consumer<Post> onDelete) {
        this.studentCache = cache;
        this.onDeleteAction = onDelete;
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/campusconnect/views/post_item.fxml"));
            loader.setController(this);
            rootNode = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            setText("Error loading cell");
        }
    }

    @Override
    protected void updateItem(Post post, boolean empty) {
        super.updateItem(post, empty);
        this.currentPost = post;

        if (empty || post == null) {
            setGraphic(null);
            setText(null);
        } else {
            // Populate Data
            String name = post.getAuthorId();
            if (studentCache != null && studentCache.containsKey(name)) {
                name = studentCache.get(name).getName();
            }
            authorLabel.setText(name);
            contentLabel.setText(post.getContent());

            // Image Logic
            boolean hasImage = post.getImagePath() != null && !post.getImagePath().equals("null");
            if (imageLabel != null) {
                imageLabel.setVisible(hasImage);
                imageLabel.setManaged(hasImage);
                if (hasImage) imageLabel.setText("📷 " + post.getImagePath());
            }

            // Delete Button Logic
            if (deleteButton != null) {
                boolean canDelete = false;
                try {
                    String curUser = AppState.getInstance().getCurrentUser().getId();
                    String curEmail = AppState.getInstance().getCurrentUser().getEmail();
                    canDelete = post.getAuthorId().equals(curUser) || curEmail.equals("admin@campus.com");
                } catch (Exception e) { /* Ignore */ }

                deleteButton.setVisible(canDelete);
                deleteButton.setManaged(canDelete);
            }

            setGraphic(rootNode);
        }
    }

    @FXML
    private void handleComments() {
        if (currentPost == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/campusconnect/views/comments.fxml"));
            Parent root = loader.load();
            CommentsController controller = loader.getController();
            controller.setPost(currentPost);

            Stage stage = new Stage();
            stage.setTitle("Comments");
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDelete() {
        if (onDeleteAction != null && currentPost != null) {
            onDeleteAction.accept(currentPost);
        }
    }
}