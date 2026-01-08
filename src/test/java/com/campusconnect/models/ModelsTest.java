package com.campusconnect.models;

import com.campusconnect.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class ModelsTest {

    @Test
    void testStudentConstructor() {
        String id = UUID.randomUUID().toString();
        Student s = new Student(id, "test@uni.com", "pass123", "John Doe", "CS", 2, "Group A");

        assertNotNull(s);
        assertEquals(id, s.getId());
        assertEquals("test@uni.com", s.getEmail());
        assertEquals("CS", s.getSpecialization());
        assertEquals(0, s.getScore()); // Verify default score
    }

    @Test
    void testPostConstructor() {
        String id = UUID.randomUUID().toString();
        Post p = new Post(id, "user-1", "Hello World", "group-1", "img.jpg");

        assertNotNull(p);
        assertEquals("Hello World", p.getContent());
        assertEquals("group-1", p.getGroupId());
        assertEquals("img.jpg", p.getImagePath());
    }

    @Test
    void testGroupConstructor() {
        String id = UUID.randomUUID().toString();
        Group g = new Group(id, "Java Study", "Learn Java", "creator-1");

        assertEquals("Java Study", g.getName());
        assertEquals("creator-1", g.getCreatorId());
    }

    @Test
    void testCommentConstructor() {
        String id = UUID.randomUUID().toString();
        Comment c = new Comment(id, "post-1", "user-1", "Nice post!");

        assertEquals("Nice post!", c.getContent());
        assertEquals("post-1", c.getPostId());
    }

    @Test
    void testQuestionConstructor() {
        Question q = new Question("user-5", "How do I fix this bug?");

        assertNotNull(q.getId()); // Should be auto-generated
        assertEquals("user-5", q.getAuthorId());
        assertEquals("How do I fix this bug?", q.getContent());
        assertNotNull(q.getTimestamp()); // Should be set to now()
    }

    @Test
    void testNotificationConstructor() {
        Notification n = new Notification("user-2", "You got a reply!");

        assertNotNull(n.getId());
        assertEquals("user-2", n.getRecipientId());
        assertEquals("You got a reply!", n.getMessage());
        assertNotNull(n.getTimestamp());
    }

    @Test
    void testGroupMemberConstructor() {
        GroupMember gm = new GroupMember("student-1", "group-99");

        assertEquals("student-1", gm.getStudentId());
        assertEquals("group-99", gm.getGroupId());
        // Test the composite ID generation
        assertEquals("student-1-group-99", gm.getId());
    }

    @Test
    void testStudentValidation_InvalidEmail() {
        Student s = new Student("1", "bad-email", "pass", "Name", "Spec", 1, "Grp");

        Exception exception = assertThrows(InvalidInputException.class, s::validate);
        assertEquals("Invalid email format.", exception.getMessage());
    }

    @Test
    void testStudentValidation_InvalidYear() {
        Student s = new Student("1", "a@b.com", "pass", "Name", "Spec", 99, "Grp");

        Exception exception = assertThrows(InvalidInputException.class, s::validate);
        assertEquals("Year must be between 1 and 6.", exception.getMessage());
    }

    @Test
    void testCSVSerialization() {

        Student original = new Student("id-123", "a@b.com", "pass", "Name", "Spec", 3, "Grp");
        original.setScore(50);

        String csv = original.toCSV();

        Student copy = new Student();
        copy.fromCSV(csv);

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getScore(), copy.getScore());
        assertEquals(original.getName(), copy.getName());
    }
}