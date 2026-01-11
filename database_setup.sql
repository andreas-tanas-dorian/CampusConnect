BEGIN
EXECUTE IMMEDIATE 'DROP TABLE comments';
EXECUTE IMMEDIATE 'DROP TABLE posts';
EXECUTE IMMEDIATE 'DROP TABLE group_members';
EXECUTE IMMEDIATE 'DROP TABLE groups';
EXECUTE IMMEDIATE 'DROP TABLE notifications';
EXECUTE IMMEDIATE 'DROP TABLE questions';
EXECUTE IMMEDIATE 'DROP TABLE students';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
END IF;
END;
/

-- 2. CREATE TABLES

CREATE TABLE students (
                          id VARCHAR2(50) PRIMARY KEY,
                          email VARCHAR2(100) UNIQUE NOT NULL,
                          password VARCHAR2(100) NOT NULL,
                          name VARCHAR2(100),
                          specialization VARCHAR2(100),
                          study_year NUMBER(1),
                          student_group VARCHAR2(20),
                          score NUMBER(5) DEFAULT 0
);

CREATE TABLE groups (
                        id VARCHAR2(50) PRIMARY KEY,
                        name VARCHAR2(100) NOT NULL,
                        description VARCHAR2(255),
                        creator_id VARCHAR2(50)
);

CREATE TABLE group_members (
                               student_id VARCHAR2(50),
                               group_id VARCHAR2(50),
                               PRIMARY KEY (student_id, group_id),
                               FOREIGN KEY (student_id) REFERENCES students(id),
                               FOREIGN KEY (group_id) REFERENCES groups(id)
);

CREATE TABLE posts (
                       id VARCHAR2(50) PRIMARY KEY,
                       author_id VARCHAR2(50) NOT NULL,
                       content VARCHAR2(4000), -- Java String can be long
                       group_id VARCHAR2(50), -- NULL means Main Feed
                       image_path VARCHAR2(255),
                       FOREIGN KEY (author_id) REFERENCES students(id)
    -- Note: We don't enforce FK on group_id purely to simplify deletion logic in this prototype
);

CREATE TABLE comments (
                          id VARCHAR2(50) PRIMARY KEY,
                          post_id VARCHAR2(50) NOT NULL,
                          author_id VARCHAR2(50) NOT NULL,
                          content VARCHAR2(1000),
                          FOREIGN KEY (post_id) REFERENCES posts(id),
                          FOREIGN KEY (author_id) REFERENCES students(id)
);

CREATE TABLE questions (
                           id VARCHAR2(50) PRIMARY KEY,
                           author_id VARCHAR2(50) NOT NULL,
                           content VARCHAR2(2000),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (author_id) REFERENCES students(id)
);

CREATE TABLE notifications (
                               id VARCHAR2(50) PRIMARY KEY,
                               recipient_id VARCHAR2(50) NOT NULL,
                               message VARCHAR2(1000),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (recipient_id) REFERENCES students(id)
);

-- 3. INSERT ADMIN USER (Optional)
INSERT INTO students (id, email, password, name, specialization, study_year, student_group, score)
VALUES ('admin-001', 'admin@campus.com', 'admin123', 'System Administrator', 'Admin', 0, 'STAFF', 9999);

COMMIT;