package com.campusconnect.controllers;

import com.campusconnect.App;
import com.campusconnect.models.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardController {

    @FXML private TableView<Student> leaderboardTable;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> groupCol;
    @FXML private TableColumn<Student, Integer> scoreCol;

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        groupCol.setCellValueFactory(new PropertyValueFactory<>("studentGroup"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        List<Student> allStudents = App.storage.getAllStudents().values().stream()
                .sorted(Comparator.comparingInt(Student::getScore).reversed())
                .collect(Collectors.toList());

        ObservableList<Student> data = FXCollections.observableArrayList(allStudents);
        leaderboardTable.setItems(data);
    }
}