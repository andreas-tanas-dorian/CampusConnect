package com.campusconnect.interfaces;

/**
 * Interface for objects that can be saved to a file.
 * We use CSV (Comma Separated Values) for simplicity in this project.
 */
public interface SerializableEntity {
    // Converts the object fields into a single CSV string
    String toCSV();

    // Populates the object fields from a CSV string
    void fromCSV(String csvLine);

    String getId();
}