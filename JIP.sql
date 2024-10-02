Create database JIP;
use JIP;

CREATE TABLE Role (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL
);

CREATE TABLE Account (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES Role(Id)
);

CREATE TABLE Student (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Fullname VARCHAR(100) NOT NULL,
    DoB DATE NOT NULL,
    Passport_url VARCHAR(255),
    Gender ENUM('Male', 'Female') NOT NULL,
    PhoneNumber VARCHAR(20),
    img VARCHAR(255),
    email VARCHAR(100) NOT NULL,
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES Account(Id)
);

CREATE TABLE Teacher (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Fullname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    PhoneNumber VARCHAR(20),
    Gender ENUM('Male', 'Female') NOT NULL,
    img VARCHAR(255),
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES Account(Id)
);

CREATE TABLE Class (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
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
    block INT NOT NULL,
    week_number INT NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday') NOT NULL,
    slot_number INT NOT NULL CHECK(slot_number BETWEEN 1 AND 4),
    teacher_id INT,
    class_id INT,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    description VARCHAR(255),
    event VARCHAR(255),
    FOREIGN KEY (teacher_id) REFERENCES Teacher(Id),
    FOREIGN KEY (class_id) REFERENCES Class(Id)
);

CREATE TABLE Attendant (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    schedule_id INT,
    status ENUM('Present', 'Absent', 'Late') NOT NULL,
    date DATE NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES Student(Id),
    FOREIGN KEY (schedule_id) REFERENCES Schedule(Id)
);

CREATE TABLE Assignment (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    date_created DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(255),
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
    description VARCHAR(255),
    content VARCHAR(255),
    date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(Id),
    FOREIGN KEY (assignment_id) REFERENCES Assignment(Id)
);

CREATE TABLE Exam (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    block INT NOT NULL,
    exam_name VARCHAR(100) NOT NULL,
    mark DECIMAL(5,2) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(Id)
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
    content VARCHAR(255),
    img VARCHAR(255),
    teacher_id INT,
    date DATE NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES Teacher(Id)
);

CREATE TABLE Personal_material (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    material_link VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES Student(Id)
);

CREATE TABLE Forum (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    topic_name VARCHAR(255),
    date_created DATE NOT NULL,
    description TEXT,
    creator_id INT,
    FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

CREATE TABLE Comment (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    date DATE NOT NULL,
    creator_id INT,
    FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

CREATE TABLE Application (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    img VARCHAR(255),
    category VARCHAR(50),
    description TEXT,
    content TEXT,
    date DATE NOT NULL,
    student_id INT,
    status ENUM('Pending', 'Approved', 'Rejected') NOT NULL,
    reply TEXT,
    reply_date DATE,
    FOREIGN KEY (student_id) REFERENCES Student(Id)
);

CREATE TABLE Message (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    recipient_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES Account(Id),
    FOREIGN KEY (recipient_id) REFERENCES Account(Id)
);




