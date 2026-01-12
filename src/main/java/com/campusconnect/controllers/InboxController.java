package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Notification;
import com.campusconnect.services.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InboxController {

    @FXML private ListView<Notification> notificationList;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        try {
            String currentUserId = AppState.getInstance().getCurrentUser().getId();
            List<Notification> myNotifs = App.storage.getNotifications(currentUserId);

            if (myNotifs.isEmpty()) {
                statusLabel.setText("You have no new messages.");
            } else {
                statusLabel.setText("You have " + myNotifs.size() + " messages.");
            }

            notificationList.setItems(FXCollections.observableArrayList(myNotifs));

            notificationList.setCellFactory(param -> new ListCell<Notification>() {
                @Override
                protected void updateItem(Notification item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String date = item.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        setText("[" + date + "] " + item.getMessage());
                        setWrapText(true);
                        setPrefWidth(350);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading notifications.");
        }
    }
}