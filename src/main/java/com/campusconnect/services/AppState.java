package com.campusconnect.services;

import com.campusconnect.models.Student;

public class AppState {
    private static AppState instance;
    private Student currentUser;

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) instance = new AppState();
        return instance;
    }

    public Student getCurrentUser() { return currentUser; }
    public void setCurrentUser(Student currentUser) { this.currentUser = currentUser; }
}