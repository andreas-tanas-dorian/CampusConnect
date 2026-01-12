package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Group;
import com.campusconnect.services.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.UUID;

public class GroupsController {

    @FXML private ListView<Group> groupListView;
    @FXML private TextField newGroupName;

    @FXML
    public void initialize() {
        loadGroups();

        // LISTENER: Detects when a user clicks on a Group
        groupListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                openGroupFeed(newVal);
            }
        });
    }

    private void loadGroups() {
        try {
            groupListView.setItems(FXCollections.observableArrayList(App.storage.getAllGroups()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when a group is selected.
     * Uses the MainLayout singleton to switch the center view.
     */
    private void openGroupFeed(Group group) {
        if (MainLayoutController.getInstance() != null) {
            MainLayoutController.getInstance().showFeed(group.getId());
        } else {
            System.err.println("❌ Error: MainLayoutController instance is null!");
        }
    }

    @FXML
    private void handleCreateGroup() {
        String name = newGroupName.getText();
        if (name == null || name.trim().isEmpty()) return;

        try {
            String uid = AppState.getInstance().getCurrentUser().getId();

            // Create new group
            Group g = new Group(UUID.randomUUID().toString(), name, "User Created Group", uid);
            App.storage.saveGroup(g);

            // Reset UI
            newGroupName.clear();
            loadGroups();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}