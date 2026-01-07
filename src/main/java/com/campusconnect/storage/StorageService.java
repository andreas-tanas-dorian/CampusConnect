package com.campusconnect.storage; // Note: Package name corrected to match your structure

import com.campusconnect.models.Post;
import com.campusconnect.models.Student;
import com.campusconnect.models.Comment;
import com.campusconnect.models.Group;
import com.campusconnect.models.GroupMember;
import com.campusconnect.models.Question;
import com.campusconnect.models.Notification;

import java.io.File;
import java.util.List;
import java.util.Map; // Import Map

public interface StorageService {

    void saveStudent(Student s) throws Exception;

    Student findStudentByEmail(String email) throws Exception;

    Map<String, Student> getAllStudents();

    List<Post> getAllPosts() throws Exception;

    void savePost(Post p) throws Exception;

    List<Post> getPostsByGroupId(String groupId) throws Exception;

    void loadData() throws Exception;

    void saveComment(Comment c) throws Exception;

    List<Comment> getCommentsForPost(String postId) throws Exception;

    void saveGroup(Group g) throws Exception;

    List<Group> getAllGroups() throws Exception;

    void joinGroup(GroupMember gm) throws Exception;

    List<GroupMember> getMembers(String groupId) throws Exception;

    String saveImageFile(File file) throws Exception;

    void saveQuestion(Question q) throws Exception;

    List<Question> getOpenQuestions() throws Exception;

    void resolveQuestion(Question q, String answerText, String resolverId) throws Exception;

    void addScore(String studentId, int points) throws Exception;

    void deletePost(String postId) throws Exception;

    void saveNotification(Notification n) throws Exception;

    List<Notification> getNotifications(String studentId) throws Exception;
}