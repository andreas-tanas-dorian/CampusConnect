package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Post;
import com.campusconnect.models.Student;
import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService;
import com.campusconnect.models.Group;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FeedController {

    @FXML private Label feedLabel;
    @FXML private ListView<Post> postListView;
    @FXML private TextArea postContentArea;
    @FXML private Label selectedImageLabel;

    private StorageService storage;
    private File selectedImageFile;
    private Map<String, Student> studentCache;
    private String currentGroupId = null;


    @FXML
    public void initialize() {
        this.storage = App.storage;
        loadStudentCache();
        loadFeed();
    }

    private void loadStudentCache() {
        try {
            studentCache = storage.getAllStudents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGroupFilter(String groupId) {
        this.currentGroupId = groupId;
        loadFeed(); // Reloads the list with the new filter
    }

    private void loadFeed() {
        if (postListView == null) return;

        try {
            List<Post> posts;

            if (currentGroupId == null) {
                // Mode A: Main Feed
                feedLabel.setText("Main Feed");
                posts = storage.getAllPosts();
            } else {
                // Mode B: Group Feed
                posts = storage.getPostsByGroupId(currentGroupId);

                // Optional: Fetch Group Name for the label
                // Since we don't have a getGroupById method exposed in storage yet,
                // we can just say "Group Feed" or try to find it in the cache if available.
                feedLabel.setText("Group Feed");
            }

            postListView.setItems(FXCollections.observableArrayList(posts));

            postListView.setCellFactory(listView -> new PostCellController(
                    studentCache,
                    this::handleDeletePost
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeletePost(Post p) {
        try {
            storage.deletePost(p.getId());
            postListView.getItems().remove(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePost() {
        String content = postContentArea.getText();
        if (content.trim().isEmpty()) return;

        try {
            Student user = AppState.getInstance().getCurrentUser();
            String imgPath = null;
            if (selectedImageFile != null) imgPath = storage.saveImageFile(selectedImageFile);

            // CHANGED: Pass currentGroupId instead of null!
            // If we are in a group, the post belongs to that group.
            Post p = new Post(UUID.randomUUID().toString(), user.getId(), content, currentGroupId, imgPath);

            storage.savePost(p);
            storage.addScore(user.getId(), 10);

            postContentArea.clear();
            selectedImageFile = null;
            selectedImageLabel.setText("No file selected");

            loadFeed(); // Refresh

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAttachImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = chooser.showOpenDialog(postContentArea.getScene().getWindow());
        if (selectedImageFile != null) selectedImageLabel.setText("Attached: " + selectedImageFile.getName());
    }
}