package com.campusconnect.models;

import com.campusconnect.interfaces.SerializableEntity;

public class GroupMember implements SerializableEntity {
    private String studentId;
    private String groupId;

    public GroupMember() {}

    public GroupMember(String studentId, String groupId) {
        this.studentId = studentId;
        this.groupId = groupId;
    }

    @Override
    public String toCSV() { return studentId + "," + groupId; }

    @Override
    public void fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        this.studentId = parts[0];
        this.groupId = parts[1];
    }

    public String getId() { return studentId + "-" + groupId; } // Composite ID
    public String getStudentId() { return studentId; }
    public String getGroupId() { return groupId; }
}