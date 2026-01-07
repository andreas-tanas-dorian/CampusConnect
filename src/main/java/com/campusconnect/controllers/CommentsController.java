package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Comment;
import com.campusconnect.models.Post;
import com.campusconnect.services.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.UUID;

public class CommentsController {

    @FXML private Label postLabel;
    @FXML private ListView<String> commentsList;
    @FXML private TextField commentInput;

    private Post currentPost;

    // This method is called by the FeedController to pass the data
    public void setPost(Post post) {
        this.currentPost = post;
        postLabel.setText(post.getContent()); // Show which post we are looking at
        loadComments();
    }

    private void loadComments() {
        try {
            List<Comment> comments = App.storage.getCommentsForPost(currentPost.getId());
            // Convert to simple strings for display
            commentsList.setItems(FXCollections.observableArrayList(
                    comments.stream().map(c -> c.getAuthorId() + ": " + c.getContent()).toList()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddComment() {
        try {
            String content = commentInput.getText();
            String authorId = AppState.getInstance().getCurrentUser().getName(); // Use Name for display
            String id = UUID.randomUUID().toString();

            Comment c = new Comment(id, currentPost.getId(), authorId, content);
            c.validate();

            App.storage.saveComment(c);

            commentInput.clear();
            loadComments(); // Refresh list

        } catch (Exception e) {
            System.out.println("Error adding comment: " + e.getMessage());
        }
    }
}