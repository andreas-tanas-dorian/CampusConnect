package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.*;
import com.campusconnect.services.AppState;
import com.campusconnect.storage.StorageService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FeedController {

    @FXML private Label userNameLabel;
    @FXML private Label userDetailLabel;
    @FXML private Label userScoreLabel;

    @FXML private ListView<Group> groupListView;
    @FXML private TextField newGroupName;

    @FXML private Label feedLabel;
    @FXML private ListView<Post> postListView;

    @FXML private TextArea postContentArea;
    @FXML private Label selectedImageLabel;

    @FXML private QuestionsController questionsPanelController;

    private StorageService storage;
    private File selectedImageFile;

    // Cache to look up names quickly (ID -> Student)
    private Map<String, Student> studentCache;

    // Track which group we are viewing (null = Main Feed)
    private Group currentViewedGroup = null;

    @FXML
    public void initialize() {
        this.storage = App.storage;

        if (questionsPanelController != null) {
            questionsPanelController.setStorageService(storage);
        }

        refreshUserProfile();
        loadStudentCache(); // Load names so we don't show IDs
        loadGroups();

        // Default: Load Main Feed
        loadFeed(null);

        // Listener: Handle Group Selection
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentViewedGroup = newVal;
                loadFeed(newVal.getId()); // Filter by Group
            }
        });
    }

    private void loadStudentCache() {
        // Fetch all students once so we can look up names instantly
        try {
            studentCache = storage.getAllStudents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshUserProfile() {
        try {
            Student memoryUser = AppState.getInstance().getCurrentUser();
            if (memoryUser != null) {
                Student dbUser = storage.findStudentByEmail(memoryUser.getEmail());
                if (dbUser != null) {
                    AppState.getInstance().setCurrentUser(dbUser);
                    userNameLabel.setText(dbUser.getName());
                    String spec = dbUser.getSpecialization() != null ? dbUser.getSpecialization() : "General";
                    String grp = dbUser.getStudentGroup() != null ? dbUser.getStudentGroup() : "N/A";
                    userDetailLabel.setText(spec + " - Year " + dbUser.getStudyYear() + " - " + grp);
                    userScoreLabel.setText(String.valueOf(dbUser.getScore()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- LOAD FEED (With Comments & Names) ---
    private void loadFeed(String groupId) {
        if (postListView == null) return;

        // Update Header
        if (groupId == null) {
            feedLabel.setText("Main Feed");
        } else {
            feedLabel.setText("Group Feed");
            // Ideally show Group Name here, but we only have ID in this method scope easily
        }

        try {
            List<Post> posts;
            if (groupId == null) {
                posts = storage.getAllPosts(); // Show everything
            } else {
                posts = storage.getPostsByGroupId(groupId); // Filter
            }

            postListView.setItems(FXCollections.observableArrayList(posts));

            // --- CUSTOM CELL FACTORY (The UI for each Post) ---
            postListView.setCellFactory(param -> new ListCell<Post>() {
                @Override
                protected void updateItem(Post p, boolean empty) {
                    super.updateItem(p, empty);
                    if (empty || p == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox card = new VBox(8);
                        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

                        // 1. Author Name
                        String authorName = p.getAuthorId();
                        if (studentCache != null && studentCache.containsKey(p.getAuthorId())) {
                            authorName = studentCache.get(p.getAuthorId()).getName();
                        }
                        Label author = new Label(authorName);
                        author.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                        // 2. Content
                        Label content = new Label(p.getContent());
                        content.setWrapText(true);
                        content.setMaxWidth(450);

                        card.getChildren().addAll(author, content);

                        // 3. Image (if exists)
                        if (p.getImagePath() != null && !p.getImagePath().equals("null")) {
                            Label imgLbl = new Label("📷 [Image Attached] " + p.getImagePath());
                            imgLbl.setStyle("-fx-text-fill: blue; -fx-font-size: 10px;");
                            card.getChildren().add(imgLbl);
                        }
                        HBox actionBox = new HBox(10);
                        // 4. "View Comments" Button
                        Button commentsBtn = new Button("Comments");
                        commentsBtn.setStyle("-fx-font-size: 11px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                        commentsBtn.setOnAction(e -> openCommentsWindow(p));
                        card.getChildren().add(commentsBtn);

                        String currentUserId = AppState.getInstance().getCurrentUser().getId();
                        String currentUserEmail = AppState.getInstance().getCurrentUser().getEmail();

                        if (p.getAuthorId().equals(currentUserId) || currentUserEmail.equals("admin@campus.com")) {
                            Button deleteBtn = new Button("Delete");
                            deleteBtn.setStyle("-fx-font-size: 11px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                            deleteBtn.setOnAction(e -> handleDeletePost(p));

                            actionBox.getChildren().add(deleteBtn);
                        }
                        card.getChildren().add(actionBox);
                        setGraphic(card);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSendComment(String postId, String content) {
        if (content.trim().isEmpty()) return;
        try {
            String userId = AppState.getInstance().getCurrentUser().getId();
            Comment c = new Comment(UUID.randomUUID().toString(), postId, userId, content);
            storage.saveComment(c);
            System.out.println("Comment saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCommentsWindow(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/campusconnect/views/comments.fxml"));
            Parent root = loader.load();

            // Pass the selected post to the popup controller
            CommentsController controller = loader.getController();
            controller.setPost(post);

            Stage stage = new Stage();
            stage.setTitle("Comments");
            stage.setScene(new Scene(root, 400, 500));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load comments.fxml. Check file path.");
        }
    }
    // --- OTHER ACTIONS ---

    private void handleDeletePost(Post p) {
        try {
            // 1. Delete from DB (You need to add this method to StorageService!)
            storage.deletePost(p.getId());

            // 2. Remove from UI immediately
            postListView.getItems().remove(p);

            System.out.println("Post deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete post.");
            alert.show();
        }
    }

    private void loadGroups() {
        if (groupListView == null) return;
        try {
            List<Group> groups = storage.getAllGroups();
            groupListView.setItems(FXCollections.observableArrayList(groups));

            groupListView.setCellFactory(param -> new ListCell<Group>() {
                @Override
                protected void updateItem(Group item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handlePost() {
        String content = postContentArea.getText();
        if (content.trim().isEmpty()) return;

        try {
            Student user = AppState.getInstance().getCurrentUser();
            String imgPath = null;
            if (selectedImageFile != null) imgPath = storage.saveImageFile(selectedImageFile);

            // Determine if this post is for a specific Group or Main Feed
            String targetGroupId = (currentViewedGroup != null) ? currentViewedGroup.getId() : null;

            Post p = new Post(UUID.randomUUID().toString(), user.getId(), content, targetGroupId, imgPath);
            storage.savePost(p);

            storage.addScore(user.getId(), 10);
            refreshUserProfile();

            postContentArea.clear();
            selectedImageFile = null;
            selectedImageLabel.setText("No file selected");

            // Reload the view we are currently looking at
            loadFeed(targetGroupId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleAttachImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = chooser.showOpenDialog(postContentArea.getScene().getWindow());
        if (selectedImageFile != null) selectedImageLabel.setText("Attached: " + selectedImageFile.getName());
    }

    @FXML private void handleCreateGroup() {
        String name = newGroupName.getText();
        if (name == null || name.trim().isEmpty()) return;
        try {
            Group g = new Group(UUID.randomUUID().toString(), name, "Desc", AppState.getInstance().getCurrentUser().getId());
            storage.saveGroup(g);
            newGroupName.clear();
            loadGroups();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- JOIN GROUP LOGIC (Triggered by button if you add one, or auto-join on post) ---
    // Note: Currently just viewing a group allows posting.
    // If you want strict joining, we need a button in the UI.
    // For now, clicking the group simply filters the feed.

    @FXML private void handleShowNotifications() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/campusconnect/views/inbox.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Inbox");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void openLeaderboard() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/campusconnect/views/leaderboard.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout() throws IOException {
        AppState.getInstance().setCurrentUser(null);
        App.setRoot("login");
    }
}