package com.campusconnect.models;

import com.campusconnect.exceptions.InvalidInputException;
import com.campusconnect.interfaces.SerializableEntity;
import com.campusconnect.interfaces.Validatable;

public class Student implements SerializableEntity, Validatable {
    private String id;
    private String email;
    private String password;
    private String name;

    // New Profile Fields
    private String specialization;
    private int studyYear;
    private String studentGroup;
    private int score; // Gamification score

    public Student() {}

    public Student(String id, String email, String password, String name,
                   String specialization, int studyYear, String studentGroup) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.specialization = specialization;
        this.studyYear = studyYear;
        this.studentGroup = studentGroup;
        this.score = 0;
    }

    @Override
    public void validate() throws InvalidInputException {
        if (email == null || !email.contains("@")) {
            throw new InvalidInputException("Invalid email format.");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidInputException("Password must be at least 4 characters.");
        }
        if (name == null || name.isEmpty()) {
            throw new InvalidInputException("Name cannot be empty.");
        }
        // New Validations
        if (specialization == null || specialization.isEmpty()) {
            throw new InvalidInputException("Specialization is required.");
        }
        if (studentGroup == null || studentGroup.isEmpty()) {
            throw new InvalidInputException("Group is required.");
        }
        if (studyYear < 1 || studyYear > 6) {
            throw new InvalidInputException("Year must be between 1 and 6.");
        }
    }

    @Override
    public String toCSV() {
        // Appending new fields to CSV string
        return id + "," + email + "," + password + "," + name + "," +
                specialization + "," + studyYear + "," + studentGroup + "," + score;
    }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = parts[0];
        this.email = parts[1];
        this.password = parts[2];
        this.name = parts[3];

        if (parts.length > 4) {
            this.specialization = parts[4];
            this.studyYear = Integer.parseInt(parts[5]);
            this.studentGroup = parts[6];
            this.score = Integer.parseInt(parts[7]);
        } else {
            this.specialization = "Unknown";
            this.studyYear = 1;
            this.studentGroup = "N/A";
            this.score = 0;
        }
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getStudyYear() { return studyYear; }
    public String getStudentGroup() { return studentGroup; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}