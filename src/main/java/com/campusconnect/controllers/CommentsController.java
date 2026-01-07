package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Comment;
import com.campusconnect.models.Post;
import com.campusconnect.models.Student;
import com.campusconnect.services.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommentsController {

    @FXML private Label postLabel;
    @FXML private ListView<String> commentsList;
    @FXML private TextField commentInput;

    private Post currentPost;

    // Called by the FeedController to pass the post data
    public void setPost(Post post) {
        this.currentPost = post;
        postLabel.setText(post.getContent()); // Show the post content at the top
        loadComments();
    }

    private void loadComments() {
        try {
            List<Comment> comments = App.storage.getCommentsForPost(currentPost.getId());

            // Convert comments to a readable string format "Name: Comment"
            // We fetch names dynamically for a better experience
            List<String> displayList = comments.stream().map(c -> {
                String name = c.getAuthorId();
                try {
                    Student s = App.storage.findStudentByEmail(c.getAuthorId()); // Ideally findById, but using ID as fallback
                    // If you implemented a cache in App.java or Storage, use that.
                    // For now, we'll display the ID or Name if available in the ID field.
                    // Note: If you want real names, use the studentCache approach from FeedController here too.
                } catch (Exception e) {}
                return name + ": " + c.getContent();
            }).collect(Collectors.toList());

            commentsList.setItems(FXCollections.observableArrayList(displayList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddComment() {
        String content = commentInput.getText();
        if (content.trim().isEmpty()) return;

        try {
            String userId = AppState.getInstance().getCurrentUser().getId();

            // Create the comment
            Comment c = new Comment(
                    UUID.randomUUID().toString(),
                    currentPost.getId(),
                    userId,
                    content
            );

            // Save to DB
            App.storage.saveComment(c);

            // Clear input and reload list
            commentInput.clear();
            loadComments();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving comment: " + e.getMessage());
        }
    }
}