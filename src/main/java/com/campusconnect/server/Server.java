package com.campusconnect.server;

import com.campusconnect.models.*;
import com.campusconnect.storage.DatabaseStorageService;
import com.campusconnect.storage.StorageService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 5050;
    private static StorageService dbService;

    public static void main(String[] args) {
        System.out.println("Starting Campus Connect Server...");

        try {
            dbService = new DatabaseStorageService();
            System.out.println("✅ Connected to Database Service.");

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Server listening on port " + PORT);

            ExecutorService threadPool = Executors.newFixedThreadPool(10);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client Connected: " + clientSocket.getInetAddress());

                threadPool.execute(new ClientHandler(clientSocket));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                while (true) {
                    String command = (String) in.readObject();

                    switch (command) {
                        case "FIND_STUDENT":
                            String email = (String) in.readObject();
                            out.writeObject(dbService.findStudentByEmail(email));
                            break;

                        case "SAVE_STUDENT":
                            Student s = (Student) in.readObject();
                            dbService.saveStudent(s);
                            out.writeObject("OK");
                            break;

                        case "GET_ALL_STUDENTS":
                            out.writeObject(dbService.getAllStudents());
                            break;

                        case "GET_ALL_POSTS":
                            out.writeObject(dbService.getAllPosts());
                            break;

                        case "GET_POSTS_BY_GROUP":
                            String groupId = (String) in.readObject();
                            out.writeObject(dbService.getPostsByGroupId(groupId));
                            break;

                        case "SAVE_POST":
                            Post p = (Post) in.readObject();
                            dbService.savePost(p);
                            out.writeObject("OK");
                            break;

                        case "DELETE_POST":
                            String pid = (String) in.readObject();
                            dbService.deletePost(pid);
                            out.writeObject("OK");
                            break;

                        case "GET_COMMENTS":
                            String postId = (String) in.readObject();
                            out.writeObject(dbService.getCommentsForPost(postId));
                            break;

                        case "SAVE_COMMENT":
                            Comment c = (Comment) in.readObject();
                            dbService.saveComment(c);
                            out.writeObject("OK");
                            break;

                        case "GET_ALL_GROUPS":
                            out.writeObject(dbService.getAllGroups());
                            break;

                        case "SAVE_GROUP":
                            Group g = (Group) in.readObject();
                            dbService.saveGroup(g);
                            out.writeObject("OK");
                            break;

                        case "GET_OPEN_QUESTIONS":
                            out.writeObject(dbService.getOpenQuestions());
                            break;

                        case "SAVE_QUESTION":
                            Question q = (Question) in.readObject();
                            dbService.saveQuestion(q);
                            out.writeObject("OK");
                            break;

                        case "RESOLVE_QUESTION":
                            Question qRes = (Question) in.readObject();
                            String ans = (String) in.readObject();
                            String resId = (String) in.readObject();
                            dbService.resolveQuestion(qRes, ans, resId);
                            out.writeObject("OK");
                            break;

                        case "ADD_SCORE":
                            String stId = (String) in.readObject();
                            int points = (int) in.readObject();
                            dbService.addScore(stId, points);
                            out.writeObject("OK");
                            break;

                        case "GET_NOTIFICATIONS":
                            String recipientId = (String) in.readObject();
                            out.writeObject(dbService.getNotifications(recipientId));
                            break;

                        case "SAVE_IMAGE":
                            String fileName = (String) in.readObject();
                            byte[] fileBytes = (byte[]) in.readObject();

                            File tempFile = new File("server_temp_" + fileName);
                            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                                fos.write(fileBytes);
                            }
                            String serverPath = dbService.saveImageFile(tempFile);
                            out.writeObject(serverPath);
                            break;

                        default:
                            System.out.println("Unknown Command: " + command);
                            out.writeObject("ERROR");
                    }
                    out.flush();
                    out.reset();
                }
            } catch (EOFException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}