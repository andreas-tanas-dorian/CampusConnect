package com.campusconnect.storage;

import com.campusconnect.models.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

public class SocketStorageService implements StorageService {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public SocketStorageService() {
        try {
            socket = new Socket("localhost", 5050);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("✅ Connected to Server via Sockets.");
        } catch (IOException e) {
            System.err.println("❌ Failed to connect to Server. Is Server.java running?");
            e.printStackTrace();
        }
    }

    private synchronized Object send(String command, Object... args) throws Exception {
        out.writeObject(command);
        for (Object arg : args) {
            out.writeObject(arg);
        }
        out.flush();
        out.reset();

        return in.readObject();
    }

    @Override
    public Student findStudentByEmail(String email) throws Exception {
        return (Student) send("FIND_STUDENT", email);
    }

    @Override
    public void saveStudent(Student s) throws Exception {
        send("SAVE_STUDENT", s);
    }

    @Override
    public Map<String, Student> getAllStudents() {
        try {
            return (Map<String, Student>) send("GET_ALL_STUDENTS");
        } catch (Exception e) { return new HashMap<>(); }
    }

    @Override
    public List<Post> getAllPosts() throws Exception {
        return (List<Post>) send("GET_ALL_POSTS");
    }

    @Override
    public List<Post> getPostsByGroupId(String groupId) throws Exception {
        return (List<Post>) send("GET_POSTS_BY_GROUP", groupId);
    }

    @Override
    public void savePost(Post p) throws Exception {
        send("SAVE_POST", p);
    }

    @Override
    public void deletePost(String postId) throws Exception {
        send("DELETE_POST", postId);
    }

    @Override
    public void saveComment(Comment c) throws Exception {
        send("SAVE_COMMENT", c);
    }

    @Override
    public List<Comment> getCommentsForPost(String postId) throws Exception {
        return (List<Comment>) send("GET_COMMENTS", postId);
    }

    @Override
    public void saveGroup(Group g) throws Exception {
        send("SAVE_GROUP", g);
    }

    @Override
    public List<Group> getAllGroups() throws Exception {
        return (List<Group>) send("GET_ALL_GROUPS");
    }

    @Override
    public List<Question> getOpenQuestions() throws Exception {
        return (List<Question>) send("GET_OPEN_QUESTIONS");
    }

    @Override
    public void saveQuestion(Question q) throws Exception {
        send("SAVE_QUESTION", q);
    }

    @Override
    public void resolveQuestion(Question q, String answerText, String resolverId) throws Exception {
        send("RESOLVE_QUESTION", q, answerText, resolverId);
    }

    @Override
    public void addScore(String studentId, int points) throws Exception {
        send("ADD_SCORE", studentId, points);
    }

    @Override
    public List<Notification> getNotifications(String studentId) throws Exception {
        return (List<Notification>) send("GET_NOTIFICATIONS", studentId);
    }

    @Override
    public String saveImageFile(File file) throws Exception {
        byte[] content = Files.readAllBytes(file.toPath());
        return (String) send("SAVE_IMAGE", file.getName(), content);
    }

    @Override public void loadData() {}
    @Override public void saveNotification(Notification n) throws Exception {}
    @Override public void joinGroup(GroupMember gm) throws Exception {}
    @Override public List<GroupMember> getMembers(String groupId) throws Exception { return new ArrayList<>(); }
}