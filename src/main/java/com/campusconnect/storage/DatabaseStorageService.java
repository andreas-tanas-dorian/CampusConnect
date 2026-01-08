package com.campusconnect.storage;

import com.campusconnect.services.AppState;
import com.campusconnect.models.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DatabaseStorageService implements StorageService {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521/FREE";
    private static final String USER = "SYSTEM";
    private static final String PASS = "SecretPassword123";

    private Connection connection;

    public DatabaseStorageService() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            connection.setAutoCommit(true);
            System.out.println("✅ Connected to Oracle Database!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to DB");
        }
    }

    @Override
    public void loadData() {}


    @Override
    public void saveStudent(Student s) throws Exception {
        if (findStudentByEmail(s.getEmail()) != null) {
            String sql = "UPDATE students SET score = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, s.getScore());
                stmt.setString(2, s.getId());
                stmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO students (id, email, password, name, specialization, study_year, student_group, score) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, s.getId());
                stmt.setString(2, s.getEmail());
                stmt.setString(3, s.getPassword());
                stmt.setString(4, s.getName());
                stmt.setString(5, s.getSpecialization());
                stmt.setInt(6, s.getStudyYear());
                stmt.setString(7, s.getStudentGroup());
                stmt.setInt(8, s.getScore());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public Student findStudentByEmail(String email) throws Exception {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student s = new Student(
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("study_year"),
                        rs.getString("student_group")
                );
                s.setScore(rs.getInt("score"));
                return s;
            }
        }
        return null;
    }

    @Override
    public Map<String, Student> getAllStudents() {
        Map<String, Student> map = new HashMap<>();
        String sql = "SELECT * FROM students";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student(
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getInt("study_year"),
                        rs.getString("student_group")
                );
                s.setScore(rs.getInt("score"));
                map.put(s.getId(), s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    @Override
    public void addScore(String studentId, int points) throws Exception {
        String sql = "UPDATE students SET score = score + ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setString(2, studentId);
            stmt.executeUpdate();
            System.out.println("DEBUG: Added " + points + " to user " + studentId);
        }
    }

    @Override
    public void saveGroup(Group g) throws Exception {
        String sql = "INSERT INTO groups (id, name, description, creator_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, g.getId());
            stmt.setString(2, g.getName());
            stmt.setString(3, g.getDescription());
            // FIX: Using the AppState user as creator if g.getCreatorId() is null/empty
            String creator = (g.getCreatorId() != null) ? g.getCreatorId() : AppState.getInstance().getCurrentUser().getId();
            stmt.setString(4, creator);
            stmt.executeUpdate();
            System.out.println("DEBUG: Group saved: " + g.getName());
        }
    }

    @Override
    public List<Group> getAllGroups() throws Exception {
        List<Group> list = new ArrayList<>();
        String sql = "SELECT * FROM groups";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Group(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("creator_id")
                ));
            }
        }
        return list;
    }

    // Stub for group members to prevent crash
    @Override public void joinGroup(GroupMember gm) {}
    @Override public List<GroupMember> getMembers(String groupId) { return new ArrayList<>(); }


    @Override
    public void savePost(Post p) throws Exception {
        String sql = "INSERT INTO posts (id, author_id, content, group_id, image_path) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getId());
            stmt.setString(2, p.getAuthorId());
            stmt.setString(3, p.getContent());
            stmt.setString(4, (p.getGroupId() == null || p.getGroupId().equals("null")) ? null : p.getGroupId());
            stmt.setString(5, p.getImagePath());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Post> getAllPosts() throws Exception {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE group_id IS NULL";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Post(
                        rs.getString("id"),
                        rs.getString("author_id"),
                        rs.getString("content"),
                        rs.getString("group_id"),
                        rs.getString("image_path")
                ));
            }
        }
        return list;
    }

    @Override
    public void saveComment(Comment c) throws Exception {
        String sql = "INSERT INTO comments (id, post_id, author_id, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, c.getId());
            stmt.setString(2, c.getPostId());
            stmt.setString(3, c.getAuthorId());
            stmt.setString(4, c.getContent());
            stmt.executeUpdate();

            addScore(c.getAuthorId(), 5);
        }
    }

    @Override
    public List<Comment> getCommentsForPost(String postId) throws Exception {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Comment(
                        rs.getString("id"),
                        rs.getString("post_id"),
                        rs.getString("author_id"),
                        rs.getString("content")
                ));
            }
        }
        return list;
    }


    @Override
    public void saveQuestion(Question q) throws Exception {
        String sql = "INSERT INTO questions (id, author_id, content, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, q.getId());
            stmt.setString(2, q.getAuthorId());
            stmt.setString(3, q.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(q.getTimestamp()));
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Question> getOpenQuestions() throws Exception {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY created_at ASC FETCH FIRST 10 ROWS ONLY";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String author = rs.getString("author_id");
                String content = rs.getString("content");
                LocalDateTime ts = rs.getTimestamp("created_at").toLocalDateTime();
                String cleanContent = content.replace(",", ";");
                String csv = id + "," + author + "," + cleanContent + "," + ts.toString();

                Question q = new Question();
                q.fromCSV(csv);
                list.add(q);
            }
        } catch (Exception e) {
            System.err.println("Error loading questions: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void resolveQuestion(Question q, String answerText, String resolverId) throws Exception {
        long minutes = ChronoUnit.MINUTES.between(q.getTimestamp(), LocalDateTime.now());
        int points = (minutes <= 5) ? 50 : 10 + (int)(minutes / 30);

        addScore(resolverId, points);

        Notification n = new Notification(q.getAuthorId(), "Re: " + q.getContent() + " -> " + answerText);
        saveNotification(n);

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            stmt.setString(1, q.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void saveNotification(Notification n) throws Exception {
        String sql = "INSERT INTO notifications (id, recipient_id, message, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, n.getId());
            stmt.setString(2, n.getRecipientId());
            stmt.setString(3, n.getMessage());
            stmt.setTimestamp(4, Timestamp.valueOf(n.getTimestamp()));
            stmt.executeUpdate();
            System.out.println("DEBUG: Notification saved for " + n.getRecipientId());
        }
    }

    @Override
    public List<Notification> getNotifications(String studentId) throws Exception {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String recipient = rs.getString("recipient_id");
                String msg = rs.getString("message");
                LocalDateTime ts = rs.getTimestamp("created_at").toLocalDateTime();

                String cleanMsg = msg.replace(",", ";");
                String csv = id + "," + recipient + "," + cleanMsg + "," + ts.toString();

                Notification n = new Notification();
                n.fromCSV(csv);
                list.add(n);
            }
        } catch (Exception e) {
            System.err.println("Error loading notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String saveImageFile(File sourceFile) throws IOException {
        File dir = new File("images");
        if (!dir.exists()) dir.mkdir();
        String name = UUID.randomUUID() + ".jpg";
        Path target = Paths.get("images", name);
        Files.copy(sourceFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    @Override
    public void deletePost(String postId) throws Exception {
        String deleteComments = "DELETE FROM comments WHERE post_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteComments)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        }

        String deletePost = "DELETE FROM posts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deletePost)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Post> getPostsByGroupId(String groupId) throws Exception {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE group_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Post(
                        rs.getString("id"),
                        rs.getString("author_id"),
                        rs.getString("content"),
                        rs.getString("group_id"),
                        rs.getString("image_path")
                ));
            }
        }
        return list;
    }

}