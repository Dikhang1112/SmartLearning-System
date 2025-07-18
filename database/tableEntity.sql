-- ===================================================================
-- Multi‐Schema SQL for Learning Management System
-- Main App Schema: studysmartdb
-- AI‐Log Schema:  lms_ai
-- Generated: 2025-07-16
-- ===================================================================

-- -------------------------------------------------------------------
-- 1. Create Schemas / Databases
-- -------------------------------------------------------------------
DROP DATABASE IF EXISTS studysmartdb;
CREATE DATABASE studysmartdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
DROP DATABASE IF EXISTS lms_ai;
CREATE DATABASE lms_ai  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- -------------------------------------------------------------------
-- 2. Main Application Schema: studysmartdb
-- -------------------------------------------------------------------
USE studysmartdb;
SET FOREIGN_KEY_CHECKS = 0;

-- 2.1 users
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  email             VARCHAR(255) NOT NULL UNIQUE,
  password     VARCHAR(255) NOT NULL,
  name         VARCHAR(100) NOT NULL,
  role              ENUM('STUDENT','TEACHER','ADMIN') NOT NULL,
  year_study     VARCHAR(20),
  avatar    VARCHAR(255),
  reset_token       VARCHAR(255),
  reset_expires_at  DATETIME,
  created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.2 subjects
DROP TABLE IF EXISTS subjects;
CREATE TABLE subjects (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  teacher_id  INT NOT NULL,
  title       VARCHAR(200) NOT NULL,
  description TEXT,
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.3 chapters
DROP TABLE IF EXISTS chapters;
CREATE TABLE chapters (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  subject_id   INT NOT NULL,
  title        VARCHAR(200) NOT NULL,
  summary_text TEXT,
  order_index  INT NOT NULL,
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.4 notes
DROP TABLE IF EXISTS notes;
CREATE TABLE notes (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  user_id     INT NOT NULL,
  chapter_id  INT NOT NULL,
  content     VARCHAR(500) NOT NULL,
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.5 chapter_attachments
DROP TABLE IF EXISTS chapter_attachments;
CREATE TABLE chapter_attachments (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  chapter_id   INT NOT NULL,
  type         ENUM('SUMMARY','CONTENT') NOT NULL,
  filename     VARCHAR(255) NOT NULL,
  filepath     VARCHAR(500) NOT NULL,
  uploaded_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
  CHECK (type IN ('SUMMARY','CONTENT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.6 exercises
DROP TABLE IF EXISTS exercises;
CREATE TABLE exercises (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  chapter_id   INT NOT NULL,
  title        VARCHAR(200),
  description  TEXT,
  type         ENUM('ESSAY','MCQ') NOT NULL,
  created_by   INT         NOT NULL,
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (chapter_id)    REFERENCES chapters(id) ON DELETE CASCADE,
  CONSTRAINT fk_exercises_created_by
    FOREIGN KEY (created_by)  REFERENCES users(id)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.7 exercise_questions (for MCQ)
DROP TABLE IF EXISTS exercise_questions;
CREATE TABLE exercise_questions (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  exercise_id  INT NOT NULL,
  question     TEXT NOT NULL,
  order_index  INT NOT NULL,
  FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.8 exercise_answers
DROP TABLE IF EXISTS exercise_answers;
CREATE TABLE exercise_answers (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  question_id     INT NOT NULL,
  answer_text     VARCHAR(255) NOT NULL,
  is_correct      BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (question_id) REFERENCES exercise_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.9 exercise_submissions
DROP TABLE IF EXISTS exercise_submissions;
CREATE TABLE exercise_submissions (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  exercise_id    INT NOT NULL,
  student_id     INT NOT NULL,
  submitted_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  grade          DECIMAL(5,2),
  feedback       TEXT,
  essay_answer   TEXT,
  FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE,
  FOREIGN KEY (student_id)  REFERENCES users(id)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.10 mcq_responses
DROP TABLE IF EXISTS mcq_responses;
CREATE TABLE mcq_responses (
  submission_id  INT NOT NULL,
  question_id    INT NOT NULL,
  answer_id      INT NOT NULL,
  PRIMARY KEY (submission_id, question_id),
  FOREIGN KEY (submission_id) REFERENCES exercise_submissions(id) ON DELETE CASCADE,
  FOREIGN KEY (question_id)   REFERENCES exercise_questions(id)    ON DELETE CASCADE,
  FOREIGN KEY (answer_id)     REFERENCES exercise_answers(id)      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.11 chapter_progress cache
DROP TABLE IF EXISTS chapter_progress;
CREATE TABLE chapter_progress (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  student_id    INT NOT NULL,
  chapter_id    INT NOT NULL,
  completed     BOOLEAN DEFAULT FALSE,
  last_score    DECIMAL(5,2),
  updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES users(id)    ON DELETE CASCADE,
  FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
  UNIQUE KEY ux_progress (student_id, chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- 2.12 Indexes for performance
CREATE INDEX idx_user_email          ON users(email);
CREATE INDEX idx_subject_teacher     ON subjects(teacher_id);
CREATE INDEX idx_chapter_subject     ON chapters(subject_id, order_index);
CREATE INDEX idx_submission_exercise ON exercise_submissions(exercise_id);
CREATE INDEX idx_mcq_response_q      ON mcq_responses(question_id);
CREATE INDEX idx_notes_chapter       ON notes(chapter_id);


-- -------------------------------------------------------------------
-- 3. AI‐Log Schema: lms_ai
-- -------------------------------------------------------------------
USE lms_ai;
SET FOREIGN_KEY_CHECKS = 0;

-- 3.1 ai_documents
DROP TABLE IF EXISTS ai_documents;
CREATE TABLE ai_documents (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  uploaded_by  INT NOT NULL,
  chapter_id   INT,
  filename     VARCHAR(255) NOT NULL,
  filepath     VARCHAR(500) NOT NULL,
  uploaded_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3.2 ai_interactions (no partitioning)
DROP TABLE IF EXISTS ai_interactions;
CREATE TABLE ai_interactions (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  exercise_id  INT NULL,
  question     TEXT    NOT NULL,
  response     TEXT    NOT NULL,
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ai_user     (user_id),
  INDEX idx_ai_created  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- ===================================================================
-- End of SQL Schema
-- ===================================================================
