package com.campusconnect.interfaces;
import java.io.Serializable;
public interface SerializableEntity extends Serializable {
    String toCSV();
    void fromCSV(String csvLine);
    String getId();
}