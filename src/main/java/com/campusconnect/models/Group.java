package com.campusconnect.models;

import com.campusconnect.exceptions.InvalidInputException;
import com.campusconnect.interfaces.SerializableEntity;
import com.campusconnect.interfaces.Validatable;

public class Group implements SerializableEntity, Validatable {
    private String id;
    private String name;
    private String description;
    private String creatorId;

    public Group() {}

    public Group(String id, String name, String description, String creatorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
    }

    @Override
    public void validate() throws InvalidInputException {
        if (name == null || name.isEmpty()) throw new InvalidInputException("Group name required.");
    }

    @Override
    public String toCSV() {
        return id + "," + name + "," + description + "," + creatorId;
    }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = parts[0];
        this.name = parts[1];
        this.description = parts[2];
        this.creatorId = parts[3];
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String toString() { return name; }
    public String getCreatorId() { return creatorId; }
}