# Campus Connect 🎓

**Campus Connect** is a desktop social media application designed for university students to share knowledge, ask questions, and collaborate in study groups.

Built with **JavaFX** and **Oracle Database**, this project demonstrates a robust **Client-Server Architecture** using Java Sockets for communication and Multithreading for concurrent user handling.

---

## 🚀 Key Features

### 📱 Social & Community
* **News Feed:** Post updates, share thoughts, and attach images.
* **Comments:** Interactive comment section for every post.
* **Groups:** Create and view specific study groups (e.g., "Computer Science Year 1").
* **Inbox:** Receive system notifications when your questions are answered.

### 🎮 Gamification & Q&A
* **Live Q&A Queue:** Students can ask help questions.
* **Timer System:** Questions are tracked by time (Green/Orange/Red status).
* **Points & Leaderboard:** Users earn points for posting and resolving questions quickly.
    * *Speed Bonus:* +50 points for answering within 5 minutes.
    * *Standard Reward:* Points scale based on difficulty.

### 🛠 Technical Highlights
* **Client-Server Architecture:** Custom TCP protocol using `java.net.Socket`.
* **Multithreaded Server:** Handles multiple concurrent clients using a Cached Thread Pool.
* **Database Integration:** Persistent storage using **Oracle Database 23c Free** (Docker).
* **Concurrency:** JavaFX Tasks for background UI updates to prevent freezing.
* **Security:** Role-Based Access Control (Admin vs. Student) for moderation tools.

---

## 🏗 Architecture

1. **Client:** Sends commands (e.g., `"GET_ALL_POSTS"`, `"SAVE_STUDENT"`) via Object Streams.
2. **Server:** Listens on Port `5000`, processes the request, connects to Oracle, and returns the result.
3. **Database:** Stores Users, Posts, Groups, and Gamification data.

---

## 🛠 Tech Stack

* **Language:** Java 21
* **UI Framework:** JavaFX (FXML)
* **Database:** Oracle Database 23c Free
* **Containerization:** Docker
* **Build Tool:** Maven
* **Testing:** JUnit 5

---

## ⚙️ Setup & Installation

### Prerequisites

* Java Development Kit (JDK) 21+
* Maven
* Docker Desktop (for the database)
* IntelliJ IDEA (Recommended)

### 1. Start the Database

Ensure Docker is running, then pull and run the Oracle Free container:

```bash
docker run -d -p 1521:1521 -e ORACLE_PASSWORD=SecretPassword123 gvenzl/oracle-free:23-slim-faststart

```

*Note: The application is configured to connect to `localhost:1521` with user `SYSTEM` and password `SecretPassword123`.*

### 2. Clone the Repository

```bash
git clone [https://github.com/YOUR_USERNAME/CampusConnect.git](https://github.com/YOUR_USERNAME/CampusConnect.git)
cd CampusConnect

```

### 3. Build the Project

```bash
mvn clean install

```

---

## ▶️ How to Run

Since this is a Client-Server application, you must run the components in a specific order:

### Step 1: Start the Server

Run the `Server.java` file.

* **Location:** `src/main/java/com/campusconnect/server/Server.java`
* **Output:** You should see `🚀 Server listening on port 5000`.

### Step 2: Start the Client

Run the `App.java` file.

* **Location:** `src/main/java/com/campusconnect/App.java`
* **Action:** The Login window will appear.

---

## 🧪 Usage Guide

1. **Register:** Create a new account with your email and group details.
2. **Login:** Use your credentials to enter the Main Feed.
3. **Post:** Write a message or upload an image.
4. **Admin Features:** Login as `admin@campus.com` to see special moderation buttons (Delete Post).
5. **Simulate concurrency:** You can run multiple instances of `App.java` to simulate different students talking to the server at the same time.

---

## 📂 Project Structure

```
src/main/java/com/campusconnect/
├── controllers/       # JavaFX Controllers (Feed, Login, Inbox)
├── models/            # Serializable Data Entities (Student, Post)
├── server/            # Server Application & Client Handlers
├── storage/           # Data Layer
│   ├── DatabaseStorageService.java  # (Server-side) JDBC Logic
│   └── SocketStorageService.java    # (Client-side) Network Logic
├── views/             # FXML Layout Files
└── App.java           # Client Entry Point

```

---

## 📝 License

This project was developed for the [University Name / Course Name] project.

```

```
