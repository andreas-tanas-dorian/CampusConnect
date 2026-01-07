package com.campusconnect.models;

import com.campusconnect.interfaces.SerializableEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Question implements SerializableEntity {
    private String id;
    private String authorId;
    private String content;
    private LocalDateTime timestamp; // Stores exactly WHEN the question was asked

    // Formatter to save dates safely in CSV (e.g., "2023-11-27T10:00:00")
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Empty constructor (Required for your generic loader)
    public Question() {}

    // Constructor for creating new questions
    public Question(String authorId, String content) {
        this.id = UUID.randomUUID().toString();
        this.authorId = authorId;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Start the clock now
    }

    // --- SerializableEntity Implementation ---

    @Override
    public String toCSV() {
        // ID, Author, Content, Timestamp
        // We use .toString() on timestamp which creates an ISO string
        return id + "," + authorId + "," + content + "," + timestamp.format(ISO_FMT);
    }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = parts[0];
        this.authorId = parts[1];
        this.content = parts[2];
        // Parse the string back into a Date Object
        this.timestamp = LocalDateTime.parse(parts[3], ISO_FMT);
    }

    @Override
    public String getId() { return id; }

    // Getters
    public String getContent() { return content; }
    public String getAuthorId() { return authorId; }
    public LocalDateTime getTimestamp() { return timestamp; }
}