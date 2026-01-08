package com.campusconnect.models;

import com.campusconnect.interfaces.SerializableEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Notification implements SerializableEntity {
    private String id;
    private String recipientId; // Who gets this message?
    private String message;     // The actual answer
    private LocalDateTime timestamp;

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Notification() {}

    public Notification(String recipientId, String message) {
        this.id = UUID.randomUUID().toString();
        this.recipientId = recipientId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toCSV() {
        String safeMessage = message.replace(",", ";");
        return id + "," + recipientId + "," + safeMessage + "," + timestamp.format(ISO_FMT);
    }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = parts[0];
        this.recipientId = parts[1];
        this.message = parts[2];
        this.timestamp = LocalDateTime.parse(parts[3], ISO_FMT);
    }

    public String getId() { return id; }
    public String getRecipientId() { return recipientId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}