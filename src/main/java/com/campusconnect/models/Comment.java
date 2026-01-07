package com.campusconnect.models;

import com.campusconnect.exceptions.InvalidInputException;
import com.campusconnect.interfaces.SerializableEntity;
import com.campusconnect.interfaces.Validatable;

public class Comment implements SerializableEntity, Validatable {
    private String id;
    private String postId;
    private String authorId;
    private String content;

    public Comment() {}

    public Comment(String id, String postId, String authorId, String content) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
    }

    @Override
    public void validate() throws InvalidInputException {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidInputException("Comment cannot be empty.");
        }
    }

    @Override
    public String toCSV() {
        // We replace commas to prevent CSV breaking
        return id + "," + postId + "," + authorId + "," + content.replace(",", ";");
    }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = parts[0];
        this.postId = parts[1];
        this.authorId = parts[2];
        this.content = parts[3].replace(";", ",");
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
}