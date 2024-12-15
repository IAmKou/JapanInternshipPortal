
CREATE DATABASE jip;
USE jip;

CREATE TABLE Role (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL
);

CREATE TABLE Account (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES Role(Id)
);

CREATE TABLE Student (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Fullname VARCHAR(100) NOT NULL,
    Japanname VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    DoB DATE NOT NULL,
    Passport_url VARCHAR(255),
    Gender ENUM('Male', 'Female') NOT NULL,
    phone_number VARCHAR(20),
    img VARCHAR(255),
    email VARCHAR(100) NOT NULL,
    mark boolean,
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES Account(Id)
);

CREATE TABLE Teacher (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Fullname VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Jname VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    Gender ENUM('Male', 'Female') NOT NULL,
    img VARCHAR(255),
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES Account(Id)
);

CREATE TABLE Manager (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Fullname VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    Jname VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    Gender ENUM('Male', 'Female') NOT NULL,
    img VARCHAR(255),
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES Account(Id)
);

CREATE TABLE Class (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    number_of_student INT,
    teacher_id INT,
    FOREIGN KEY (teacher_id) REFERENCES Teacher(Id)
);

CREATE TABLE List (
    class_id INT,
    student_id INT,
    PRIMARY KEY (class_id, student_id),
    FOREIGN KEY (class_id) REFERENCES Class(Id),
    FOREIGN KEY (student_id) REFERENCES Student(Id)
);

CREATE TABLE Schedule (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Date date,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday') NOT NULL,
    class_id INT,
    start_time TIME NULL,
    end_time TIME NULL,
    description VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    event VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    location varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (class_id) REFERENCES Class(Id)
);


CREATE TABLE Attendant (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    schedule_id INT,
    status ENUM('Present', 'Absent', 'Late', 'Permitted') NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(Id),
    FOREIGN KEY (schedule_id) REFERENCES Schedule(Id)
);

CREATE TABLE Assignment (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    date_created DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    content VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    teacher_id INT,
    img VARCHAR(255),
    class_id INT,
    FOREIGN KEY (teacher_id) REFERENCES Teacher(Id),
    FOREIGN KEY (class_id) REFERENCES Class(Id)
);

CREATE TABLE Student_assignment (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    assignment_id INT,
    mark DECIMAL(5,2),
    description VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    content VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(Id),
    FOREIGN KEY (assignment_id) REFERENCES Assignment(Id)
);

CREATE TABLE Exam (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT,
    exam_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    content longtext character set utf8mb4 COLLATE utf8mb4_unicode_ci not null ,
    exam_date DATE NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher(Id)
);

CREATE TABLE Mark_report (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    attendance_rate DECIMAL(5,2),
    avg_assignment_mark DECIMAL(5,2),
    avg_exam_mark DECIMAL(5,2),
    FOREIGN KEY (student_id) REFERENCES Student(Id)
);

CREATE TABLE Material (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    img VARCHAR(255),
    teacher_id INT,
    date_created DATE NOT NULL,
    title varchar(255),
    FOREIGN KEY (teacher_id) REFERENCES Teacher(Id)
);

CREATE TABLE Personal_material (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    material_link VARCHAR(255),
    material_id INT,
    FOREIGN KEY (student_id) REFERENCES Student(Id),
    FOREIGN KEY (material_id) REFERENCES Material(Id) ON DELETE CASCADE
);

CREATE TABLE Thread (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    topic_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    date_created DATE NOT NULL,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    image BLOB,
    creator_id INT,
    FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

CREATE TABLE Post (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    replies VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    date_created DATE NOT NULL,
    image BLOB,
    creator_id INT,
    FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

CREATE TABLE Comment (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    date DATE NOT NULL,
    creator_id INT,
    FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

CREATE TABLE Application (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    img VARCHAR(255),
    category VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    date_created DATE NOT NULL,
    student_id INT,
    teacher_id INT,
    status ENUM('Pending', 'Approved', 'Rejected') NOT NULL,
    reply TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    date_replied DATE,
    FOREIGN KEY (student_id) REFERENCES Student(Id),
    FOREIGN KEY (teacher_id) REFERENCES Teacher(Id)
);

CREATE TABLE Notification (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    Content VARCHAR(250) NOT NULL,
    Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_id INT, 
    role ENUM('Admin', 'Teacher', 'Student', 'Manager') NOT NULL, 
    recipient_id INT DEFAULT NULL, 
    FOREIGN KEY (account_id) REFERENCES Account(Id),
    FOREIGN KEY (recipient_id) REFERENCES Account(Id)
);

Create table Report(
Id int auto_increment primary key,
Title varchar(100),
Content varchar(100),
reporter_id int,
foreign key (reporter_id) references Account(id)
);
