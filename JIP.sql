CREATE DATABASE jip;
USE jip;

-- Roles table
CREATE TABLE Role (
                      Id INT AUTO_INCREMENT PRIMARY KEY,
                      Name VARCHAR(50) NOT NULL
);

-- Accounts table (linked with Role)
CREATE TABLE Account (
                         Id INT AUTO_INCREMENT PRIMARY KEY,
                         Username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci UNIQUE NOT NULL,
                         Password VARCHAR(255) NOT NULL,
                         role_id INT,
                         FOREIGN KEY (role_id) REFERENCES Role(Id)
);

-- Students table (linked with Account)
CREATE TABLE Student (
                         Id INT AUTO_INCREMENT PRIMARY KEY,
                         Fullname VARCHAR(100) NOT NULL,
                         Japanname VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                         DoB DATE NOT NULL,
                         Passport_url VARCHAR(255),
                         Gender VARCHAR(10) NOT NULL,
                         phone_number VARCHAR(20),
                         img VARCHAR(255),
                         email VARCHAR(100) NOT NULL,
                         mark BOOLEAN,
                         account_id INT,
                         FOREIGN KEY (account_id) REFERENCES Account(Id)
);

-- Teachers table (linked with Account)
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

-- Manager table (linked with Account)
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
CREATE TABLE Room (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE

);

CREATE TABLE Semester (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          start_time DATE NOT NULL,
                          end_time DATE NOT NULL,
                          status ENUM('Active', 'Inactive') DEFAULT 'Inactive'
);

-- Class table (linked with Teacher)
CREATE TABLE Class (
                       Id INT AUTO_INCREMENT PRIMARY KEY,
                       teacher_id INT NOT NULL,
                       name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                       status ENUM('Active', 'Inactive') DEFAULT 'Inactive',
                       number_of_student INT,
                       semester_id INT NOT NULL,
                       foreign key (semester_id) references Semester(Id),
                       FOREIGN KEY (teacher_id) REFERENCES Teacher(Id)
);

-- Student-Class Many-to-Many relationship
CREATE TABLE List (
                      class_id INT,
                      student_id INT,
                      PRIMARY KEY (class_id, student_id),
                      FOREIGN KEY (class_id) REFERENCES Class(Id),
                      FOREIGN KEY (student_id) REFERENCES Student(Id)
);

-- Holiday table
CREATE TABLE Holiday (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         date DATE NOT NULL UNIQUE
);

-- Curriculum table
CREATE TABLE Curriculum (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            subject VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                            total_slot INT DEFAULT 52,
                            total_time INT DEFAULT 536 -- Total time in hours
);

Create table Curriculum_information (
                                        id Int auto_increment primary key,
                                        curriculum_id int,
                                        description LONGTEXT character set utf8mb4 COLLATE utf8mb4_unicode_ci,
                                        foreign key (curriculum_id) references Curriculum(id) ON DELETE CASCADE
);

-- Schedule table
CREATE TABLE Schedule (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          date DATE NOT NULL,
                          day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
                          class_id INT DEFAULT NULL,
                          room VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          activity VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          semester_id INT,
                          status ENUM('Draft', 'Published') DEFAULT 'Draft',
                          FOREIGN KEY (class_id) REFERENCES Class(id) ON DELETE SET NULL,
                          FOREIGN KEY (semester_id) REFERENCES Semester(id) ON DELETE CASCADE
);

CREATE TABLE room_availability (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  room_id INT NOT NULL,
                                  date DATE NOT NULL,
                                  status ENUM('Available', 'Occupied') DEFAULT 'Available',
                                  schedule_id BIGINT DEFAULT NULL,
                                  FOREIGN KEY (room_id) REFERENCES Room(id) ON DELETE CASCADE,
                                  FOREIGN KEY (schedule_id) REFERENCES Schedule(id) ON DELETE SET NULL,
                                  UNIQUE (room_id, date)
);

-- Attendant table
CREATE TABLE Attendant (
                           Id INT AUTO_INCREMENT PRIMARY KEY,
                           student_id INT,
                           schedule_id BIGINT,
                           status ENUM('Present', 'Absent', 'Late', 'Permitted') NOT NULL,
                           date DATE NOT NULL,
                           curriculum_id INT NUll,
                           FOREIGN KEY (student_id) REFERENCES Student(Id),
                           FOREIGN KEY (schedule_id) REFERENCES Schedule(Id),
                           FOREIGN KEY (curriculum_id) REFERENCES curriculum(id)
);



-- Assignment table
CREATE TABLE Assignment (
                            Id INT AUTO_INCREMENT PRIMARY KEY,
                            date_created DATE NOT NULL,
                            end_date DATE NOT NULL,
                            description VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                            content LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                            teacher_id INT,
                            img VARCHAR(255),
                            class_id INT,
                            FOREIGN KEY (teacher_id) REFERENCES Teacher(Id),
                            FOREIGN KEY (class_id) REFERENCES Class(Id)
);

-- Student Assignment table (linked with Assignment and Student)
CREATE TABLE Student_assignment (
                                    Id INT AUTO_INCREMENT PRIMARY KEY,
                                    student_id INT,
                                    assignment_id INT,
                                    mark DECIMAL(5,2),
                                    description VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                    content LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                                    file VARCHAR(255),
                                    date DATE NOT NULL,
                                    FOREIGN KEY (student_id) REFERENCES Student(Id),
                                    FOREIGN KEY (assignment_id) REFERENCES Assignment(Id)
);

-- MarkReport table (linked with Student)
CREATE TABLE mark_report (
                             Id INT AUTO_INCREMENT PRIMARY KEY,
                             student_id INT,
                             softskill DECIMAL(5,2) NULL,
                             presentation DECIMAL(5,2) NULL,
                             script_presentation DECIMAL (5,2) NULL,
                             avg_exam_mark DECIMAL (5,2) NULL,
                             midterm_exam DECIMAL(5,2) NULL,
                             final_exam DECIMAL(5,2) NULL,
                             skill DECIMAL(5,2) NULL,
                             attitude DECIMAL(5,2) NULL,
                             final_mark DECIMAL(5,2) NULL,
                             comment  VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                             FOREIGN KEY (student_id) REFERENCES student(Id)
);
Create table exam (
                      id int auto_increment primary key,
                      title VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
);

CREATE TABLE mark_report_exam (
                                  mark_report_id INT,
                                  exam_id INT,
                                  kanji Decimal(5,2) null,
                                  bunpou Decimal(5,2) null,
                                  kotoba Decimal(5,2) null,
                                  PRIMARY KEY (mark_report_id, exam_id),
                                  FOREIGN KEY (mark_report_id) REFERENCES mark_report(Id) ON DELETE CASCADE,
                                  FOREIGN KEY (exam_id) REFERENCES exam(Id) ON DELETE CASCADE
);
-- Material table (linked with Teacher)
CREATE TABLE Material (
                          Id INT AUTO_INCREMENT PRIMARY KEY,
                          content VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                          img VARCHAR(255),
                          teacher_id INT,
                          date_created DATE NOT NULL,
                          title VARCHAR(255),
                          FOREIGN KEY (teacher_id) REFERENCES Teacher(Id)
);

-- Personal Material table (linked with Student)
CREATE TABLE Personal_material (
                                   Id INT AUTO_INCREMENT PRIMARY KEY,
                                   student_id INT,
                                   material_link VARCHAR(255),
                                   FOREIGN KEY (student_id) REFERENCES Student(Id)
);

-- Thread table (linked with Account)
CREATE TABLE Thread (
                        Id INT AUTO_INCREMENT PRIMARY KEY,
                        topic_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                        date_created DATE NOT NULL,
                        description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                        image BLOB,
                        creator_id INT,
                        FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

-- Post table (linked with Account)
CREATE TABLE Post (
                      Id INT AUTO_INCREMENT PRIMARY KEY,
                      replies VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                      date_created DATE NOT NULL,
                      image BLOB,
                      creator_id INT,
                      FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

-- Comment table (linked with Account)
CREATE TABLE Comment (
                         Id INT AUTO_INCREMENT PRIMARY KEY,
                         content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         date DATE NOT NULL,
                         creator_id INT,
                         FOREIGN KEY (creator_id) REFERENCES Account(Id)
);

-- Application table (linked with Student and Teacher)
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

-- Notification table (linked with Account)
CREATE TABLE Notification (
                              Id INT AUTO_INCREMENT PRIMARY KEY,
                              Title VARCHAR(100) NOT NULL,
                              Created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              account_id INT,
                              recipient_account_id INT DEFAULT NULL,
                              FOREIGN KEY (account_id) REFERENCES Account(Id),
                              FOREIGN KEY (recipient_account_id) REFERENCES Account(Id)
);

-- Report table (linked with Account)
CREATE TABLE Report (
                        Id INT AUTO_INCREMENT PRIMARY KEY,
                        Title VARCHAR(100),
                        Content VARCHAR(100),
                        reporter_id INT,
                        FOREIGN KEY (reporter_id) REFERENCES Account(Id)
);
