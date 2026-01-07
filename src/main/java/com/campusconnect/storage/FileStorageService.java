package com.campusconnect.storage;

import com.campusconnect.interfaces.SerializableEntity;
import com.campusconnect.models.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation that saves data to CSV files.
 */
public class FileStorageService implements StorageService {

    private Map<String, Student> students = new HashMap<>();

    // Lists for ordered data
    private List<Post> posts = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<GroupMember> members = new ArrayList<>();
    private LinkedList<Question> questionQueue = new LinkedList<>();
    private List<Notification> notifications = new ArrayList<>();

    private static final String STUDENT_FILE = "data/students.csv";
    private static final String POST_FILE = "data/posts.csv";
    private static final String COMMENT_FILE = "data/comments.csv";
    private static final String GROUP_FILE = "data/groups.csv";
    private static final String MEMBER_FILE = "data/members.csv";
    private static final String QUESTION_FILE = "data/questions.csv";
    private static final String NOTIF_FILE = "data/notifications.csv";

    @Override
    public void loadData() throws IOException {
        Files.createDirectories(Paths.get("data"));

        loadMap(STUDENT_FILE, Student.class, students);
        loadList(POST_FILE, Post.class, posts);
        loadList(COMMENT_FILE, Comment.class, comments);
        loadList(GROUP_FILE, Group.class, groups);
        loadList(MEMBER_FILE, GroupMember.class, members);
        loadQueue(QUESTION_FILE, questionQueue);
        loadList(NOTIF_FILE, Notification.class, notifications);
    }

    private <T extends SerializableEntity> void loadMap(String filename, Class<T> clazz, Map<String, T> targetMap) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) return;

        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty()) continue;
            try {
                T obj = clazz.getDeclaredConstructor().newInstance();
                obj.fromCSV(line);
                targetMap.put(obj.getId(), obj);
            } catch (Exception e) {
                System.err.println("Error parsing line in " + filename + ": " + line);
            }
        }
    }

    private <T extends SerializableEntity> void loadList(String filename, Class<T> clazz, List<T> targetList) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) return;

        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty()) continue;
            try {
                T obj = clazz.getDeclaredConstructor().newInstance();
                obj.fromCSV(line);
                targetList.add(obj);
            } catch (Exception e) {
                System.err.println("Error parsing line in " + filename + ": " + line);
            }
        }
    }

    private void loadQueue(String filename, LinkedList<Question> targetQueue) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) return;

        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty()) continue;
            try {
                Question q = new Question();
                q.fromCSV(line);
                targetQueue.add(q);
            } catch (Exception e) {
                System.err.println("Error parsing question: " + line);
            }
        }
    }

    @Override
    public void saveStudent(Student s) throws IOException {
        students.put(s.getId(), s);
        appendToFile(STUDENT_FILE, s.toCSV());
    }

    @Override
    public Student findStudentByEmail(String email) {
        return students.values().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // [NEW] Implementation for Leaderboard
    @Override
    public Map<String, Student> getAllStudents() {
        return this.students;
    }

    @Override
    public void savePost(Post p) throws IOException {
        posts.add(p);
        appendToFile(POST_FILE, p.toCSV());
    }

    @Override
    public List<Post> getAllPosts() {
        return posts;
    }

    @Override
    public void saveComment(Comment c) throws IOException {
        comments.add(c);
        appendToFile(COMMENT_FILE, c.toCSV());
    }

    @Override
    public List<Comment> getCommentsForPost(String postId) {
        return comments.stream()
                .filter(c -> c.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    @Override
    public void saveGroup(Group g) throws IOException {
        groups.add(g);
        appendToFile(GROUP_FILE, g.toCSV());
    }

    @Override
    public List<Group> getAllGroups() {
        return groups;
    }

    @Override
    public void joinGroup(GroupMember gm) throws IOException {
        boolean exists = members.stream().anyMatch(m ->
                m.getStudentId().equals(gm.getStudentId()) && m.getGroupId().equals(gm.getGroupId()));

        if (!exists) {
            members.add(gm);
            appendToFile(MEMBER_FILE, gm.toCSV());
        }
    }

    @Override
    public List<GroupMember> getMembers(String groupId) {
        return members.stream()
                .filter(m -> m.getGroupId().equals(groupId))
                .collect(Collectors.toList());
    }

    public void saveQuestion(Question q) throws IOException {
        questionQueue.add(q);
        appendToFile(QUESTION_FILE, q.toCSV());
    }

    public List<Question> getOpenQuestions() {
        return questionQueue.stream()
                .limit(4)
                .collect(Collectors.toList());
    }

    @Override
    public void saveNotification(Notification n) throws IOException {
        notifications.add(n);
        appendToFile(NOTIF_FILE, n.toCSV());
    }

    @Override
    public List<Notification> getNotifications(String studentId) {
        return notifications.stream()
                .filter(n -> n.getRecipientId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public void resolveQuestion(Question q, String answerText, String resolverId) throws IOException {
        // 1. Remove from Pending Queue
        questionQueue.remove(q);
        overwriteQuestionFile();

        // 2. Create Notification for the asker
        String fullMessage = "Re: " + q.getContent() + " -> " + answerText;
        Notification reply = new Notification(q.getAuthorId(), fullMessage);
        saveNotification(reply);

        // --- NEW: GAMIFICATION LOGIC ---

        // Calculate how many minutes have passed since the question was asked
        long minutesElapsed = java.time.temporal.ChronoUnit.MINUTES.between(q.getTimestamp(), java.time.LocalDateTime.now());

        int pointsAwarded = 0;

        if (minutesElapsed <= 5) {
            // HUGE BONUS for speed
            pointsAwarded = 50;
            System.out.println("SPEED BONUS! Answered in " + minutesElapsed + " minutes.");
        } else {

            pointsAwarded = 12 + (int)(minutesElapsed / 30);
            System.out.println("BOUNTY CLAIMED! Question waited " + minutesElapsed + " minutes.");
        }

        try {
            addScore(resolverId, pointsAwarded);
            System.out.println("Awarded " + pointsAwarded + " points to user " + resolverId);
        } catch (Exception e) {
            System.err.println("Could not add score: " + e.getMessage());
        }
    }

    @Override
    public List<Post> getPostsByGroupId(String groupId) {
        // Filter the in-memory list for posts belonging to this group
        return posts.stream()
                .filter(p -> groupId.equals(p.getGroupId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePost(String postId) throws Exception {
        // Simple implementation: Remove from memory list
        posts.removeIf(p -> p.getId().equals(postId));

        // Note: For FileStorage, strictly deleting from the CSV file
        // requires re-writing the whole file. Since we are using Database now,
        // updating just the memory list is sufficient to stop the error.
    }

    @Override
    public void addScore(String studentId, int points) throws IOException {
        Student s = students.get(studentId);
        if (s != null) {
            s.setScore(s.getScore() + points);
            saveStudent(s);
        }
    }

    private void overwriteQuestionFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(QUESTION_FILE))) {
            for (Question q : questionQueue) {
                writer.println(q.toCSV());
            }
        }
    }

    public String saveImageFile(File sourceFile) throws IOException {
        File directory = new File("images");
        if (!directory.exists()) {
            directory.mkdir();
        }

        String newFileName = UUID.randomUUID().toString() + ".jpg";
        Path targetPath = Paths.get("images", newFileName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }

    private void appendToFile(String filename, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(data);
            writer.newLine();
        }
    }
}