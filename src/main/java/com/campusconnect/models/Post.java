package com.campusconnect.models;

import com.campusconnect.exceptions.InvalidInputException;
import com.campusconnect.interfaces.SerializableEntity;
import com.campusconnect.interfaces.Validatable;

public class Post implements SerializableEntity, Validatable {
    private String id;
    private String authorId;
    private String content;
    private String groupId;
    private String imagePath;

    public Post() {}

    // Update Constructor
    public Post(String id, String authorId, String content, String groupId, String imagePath) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.groupId = groupId;
        this.imagePath = imagePath;
    }

    @Override
    public void validate() throws InvalidInputException {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidInputException("Post content cannot be empty.");
        }
    }

    @Override
    public String toCSV() {
        // Save "null" string if no image exists
        String img = (imagePath == null) ? "null" : imagePath;
        return id + "," + authorId + "," + content.replace(",", ";") + "," + groupId + "," + img;
    }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = parts[0];
        this.authorId = parts[1];
        this.content = parts[2].replace(";", ",");
        this.groupId = parts[3];

        // Handle the new column safely
        if (parts.length > 4 && !parts[4].equals("null")) {
            this.imagePath = parts[4];
        } else {
            this.imagePath = null;
        }
    }

    public String getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public String getGroupId() { return groupId; }
    public String getImagePath() { return imagePath; }
}