-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: studysmartdb
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chapter`
--

DROP TABLE IF EXISTS `chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter` (
  `id` int NOT NULL AUTO_INCREMENT,
  `subject_id` int NOT NULL,
  `title` varchar(200) NOT NULL,
  `summary_text` text,
  `order_index` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chapter_subject` (`subject_id`,`order_index`),
  CONSTRAINT `chapter_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter`
--

LOCK TABLES `chapter` WRITE;
/*!40000 ALTER TABLE `chapter` DISABLE KEYS */;
INSERT INTO `chapter` VALUES (1,1,'Tập hợp','Mệnh đề và tập hợp trong toán học',1,'2025-08-09 15:58:54','2025-08-11 20:56:13'),(2,1,'Xác suất','Xác suất thực tiễn',2,'2025-08-09 15:58:54','2025-08-09 15:58:54');
/*!40000 ALTER TABLE `chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter_attachment`
--

DROP TABLE IF EXISTS `chapter_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_attachment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `chapter_id` int NOT NULL,
  `type` enum('SUMMARY','CONTENT') NOT NULL,
  `filename` varchar(255) NOT NULL,
  `filepath` varchar(500) NOT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `extension` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `chapter_attachment` (`chapter_id`),
  CONSTRAINT `chapter_attachment` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chapter_attachment_chk_1` CHECK ((`type` in (_utf8mb4'SUMMARY',_utf8mb4'CONTENT')))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_attachment`
--

LOCK TABLES `chapter_attachment` WRITE;
/*!40000 ALTER TABLE `chapter_attachment` DISABLE KEYS */;
INSERT INTO `chapter_attachment` VALUES (1,1,'CONTENT','1.TapHop.pdf','https://res.cloudinary.com/dao8z029z/raw/upload/v1754921973/chapters/1/chapters/1/1_TapHop-1754921969681','2025-08-11 21:19:35','pdf');
/*!40000 ALTER TABLE `chapter_attachment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter_progress`
--

DROP TABLE IF EXISTS `chapter_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter_progress` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `chapter_id` int NOT NULL,
  `completed` tinyint(1) DEFAULT '0',
  `last_score` decimal(5,2) DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_progress` (`student_id`,`chapter_id`),
  KEY `progress_chapter_id` (`chapter_id`),
  CONSTRAINT `chapter_progress_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `progress_chapter_id` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter_progress`
--

LOCK TABLES `chapter_progress` WRITE;
/*!40000 ALTER TABLE `chapter_progress` DISABLE KEYS */;
/*!40000 ALTER TABLE `chapter_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class` (
  `id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
INSERT INTO `class` VALUES (1,'10A2','2025-08-04 14:38:34','2025-08-09 16:02:49'),(2,'11A1','2025-08-04 14:38:34','2025-08-04 23:20:46');
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise`
--

DROP TABLE IF EXISTS `exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise` (
  `id` int NOT NULL AUTO_INCREMENT,
  `chapter_id` int NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `description` text,
  `type` enum('ESSAY','MCQ') NOT NULL,
  `created_by` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `create_by_teacher_idx` (`created_by`),
  KEY `excercise_chapter` (`chapter_id`),
  CONSTRAINT `create_by_teacher` FOREIGN KEY (`created_by`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `excercise_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise`
--

LOCK TABLES `exercise` WRITE;
/*!40000 ALTER TABLE `exercise` DISABLE KEYS */;
INSERT INTO `exercise` VALUES (1,1,'Bài tập trắc nghiệm về mệnh đề?','Chọn câu trả lời đúng nhất cho mỗi câu','MCQ',2,'2025-08-11 13:14:40');
/*!40000 ALTER TABLE `exercise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_answer`
--

DROP TABLE IF EXISTS `exercise_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_answer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL,
  `answer_text` varchar(255) NOT NULL,
  `is_correct` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `exercise_answers_question` (`question_id`),
  CONSTRAINT `exercise_answers_question` FOREIGN KEY (`question_id`) REFERENCES `exercise_question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_answer`
--

LOCK TABLES `exercise_answer` WRITE;
/*!40000 ALTER TABLE `exercise_answer` DISABLE KEYS */;
INSERT INTO `exercise_answer` VALUES (1,1,'A. Tam giác đều là tam giác có ba cạnh bằng nhau.',0),(2,1,'B. 3 < 1',0),(3,1,'C. 4 - 5 = 1',0),(4,1,'D. Bạn học giỏi quá!',1),(5,2,'A.12 là số tự nhiên lẻ',1),(6,2,'B. An học lớp mấy',0),(7,2,'C.Các bạn có chăm học không',0);
/*!40000 ALTER TABLE `exercise_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_question`
--

DROP TABLE IF EXISTS `exercise_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_question` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exercise_id` int NOT NULL,
  `order_index` int NOT NULL,
  `question` text NOT NULL,
  `solution` longtext,
  PRIMARY KEY (`id`),
  KEY `excercise_question_id` (`exercise_id`),
  CONSTRAINT `excercise_question_id` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_question`
--

LOCK TABLES `exercise_question` WRITE;
/*!40000 ALTER TABLE `exercise_question` DISABLE KEYS */;
INSERT INTO `exercise_question` VALUES (1,1,1,' Câu nào sau đây không là mệnh đề?','Vì “Bạn học giỏi quá!” là câu cảm thán không có khẳng định đúng hoặc sai'),(2,1,2,'Trong các câu sau đây câu nào là mệnh đề?','\"12  là số tự nhiên lẻ\" là mệnh đề vì nó khẳng định');
/*!40000 ALTER TABLE `exercise_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_submission`
--

DROP TABLE IF EXISTS `exercise_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_submission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `exercise_id` int NOT NULL,
  `student_id` int NOT NULL,
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `grade` decimal(5,2) DEFAULT NULL,
  `feedback` text,
  `essay_answer` text,
  PRIMARY KEY (`id`),
  KEY `idx_submission_exercise` (`exercise_id`),
  KEY `exercise_submissions_student` (`student_id`),
  CONSTRAINT `exercise_submissions` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`) ON DELETE CASCADE,
  CONSTRAINT `exercise_submissions_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_submission`
--

LOCK TABLES `exercise_submission` WRITE;
/*!40000 ALTER TABLE `exercise_submission` DISABLE KEYS */;
INSERT INTO `exercise_submission` VALUES (1,1,4,'2025-08-11 13:30:29',10.00,'Good',NULL);
/*!40000 ALTER TABLE `exercise_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mcq_response`
--

DROP TABLE IF EXISTS `mcq_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mcq_response` (
  `submission_id` int NOT NULL,
  `question_id` int NOT NULL,
  `answer_id` int NOT NULL,
  PRIMARY KEY (`submission_id`,`question_id`),
  KEY `idx_mcq_response_q` (`question_id`),
  KEY `mcq_responses_exAnswer` (`answer_id`),
  CONSTRAINT `mcq_responses_exAnswer` FOREIGN KEY (`answer_id`) REFERENCES `exercise_answer` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mcq_responses_exQuestion` FOREIGN KEY (`question_id`) REFERENCES `exercise_question` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mcq_responses_exSubmission` FOREIGN KEY (`submission_id`) REFERENCES `exercise_submission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mcq_response`
--

LOCK TABLES `mcq_response` WRITE;
/*!40000 ALTER TABLE `mcq_response` DISABLE KEYS */;
INSERT INTO `mcq_response` VALUES (1,1,4);
/*!40000 ALTER TABLE `mcq_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `chapter_id` int NOT NULL,
  `content` varchar(500) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notes_chapter` (`chapter_id`),
  KEY `notes_user` (`user_id`),
  CONSTRAINT `notes_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`) ON DELETE CASCADE,
  CONSTRAINT `notes_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note`
--

LOCK TABLES `note` WRITE;
/*!40000 ALTER TABLE `note` DISABLE KEYS */;
/*!40000 ALTER TABLE `note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `user_id` int NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (4),(5);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_class`
--

DROP TABLE IF EXISTS `student_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_class` (
  `student_id` int NOT NULL,
  `class_id` int NOT NULL,
  PRIMARY KEY (`student_id`,`class_id`),
  KEY `student_class_ibfk_2` (`class_id`),
  CONSTRAINT `fk_class_student` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `student_class_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `student_class_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_class`
--

LOCK TABLES `student_class` WRITE;
/*!40000 ALTER TABLE `student_class` DISABLE KEYS */;
INSERT INTO `student_class` VALUES (4,1),(5,1),(4,2),(5,2);
/*!40000 ALTER TABLE `student_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_subject`
--

DROP TABLE IF EXISTS `student_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_subject` (
  `student_id` int NOT NULL,
  `subject_id` int NOT NULL,
  PRIMARY KEY (`student_id`,`subject_id`),
  KEY `idx_student_subject_subject` (`subject_id`),
  CONSTRAINT `fk_student_subject_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_student_subject_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_subject`
--

LOCK TABLES `student_subject` WRITE;
/*!40000 ALTER TABLE `student_subject` DISABLE KEYS */;
INSERT INTO `student_subject` VALUES (4,1),(4,2);
/*!40000 ALTER TABLE `student_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `description` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (1,'Toán 10','https://res.cloudinary.com/dao8z029z/image/upload/v1754729794/ddlwmbnul6f9d5tmpnhb.png','Học về hàm số, đồ thị và xác suất','2025-08-09 15:56:35','2025-08-09 15:58:19'),(2,'History 10','https://res.cloudinary.com/dao8z029z/image/upload/v1754551596/b4wkjowtqwvklcrgk4bp.jpg','Learn about history of VietNam and World','2025-07-24 10:18:52','2025-08-07 14:26:37');
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `user_id` int NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
INSERT INTO `teacher` VALUES (2),(3);
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_class`
--

DROP TABLE IF EXISTS `teacher_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_class` (
  `teacher_id` int NOT NULL,
  `class_id` int NOT NULL,
  PRIMARY KEY (`teacher_id`,`class_id`),
  KEY `teacher_class_ibfk_2` (`class_id`),
  CONSTRAINT `fk_class_teacher` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `teacher_class_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `teacher_class_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_class`
--

LOCK TABLES `teacher_class` WRITE;
/*!40000 ALTER TABLE `teacher_class` DISABLE KEYS */;
INSERT INTO `teacher_class` VALUES (2,1),(3,2);
/*!40000 ALTER TABLE `teacher_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_subject`
--

DROP TABLE IF EXISTS `teacher_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_subject` (
  `teacher_id` int NOT NULL,
  `subject_id` int NOT NULL,
  PRIMARY KEY (`teacher_id`,`subject_id`),
  KEY `teacher_subject_ibfk_2` (`subject_id`),
  CONSTRAINT `teacher_subject_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `teacher_subject_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_subject`
--

LOCK TABLES `teacher_subject` WRITE;
/*!40000 ALTER TABLE `teacher_subject` DISABLE KEYS */;
INSERT INTO `teacher_subject` VALUES (2,1),(3,2);
/*!40000 ALTER TABLE `teacher_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `role` enum('STUDENT','TEACHER','ADMIN') NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `reset_expires_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin@gmail.com','$2a$10$Pq5TmzY.2kTqksPd2uto9uGY6W6PwNYYG8jlCwbqCqRKUmVgcj9/u','Admin','ADMIN','https://res.cloudinary.com/dao8z029z/image/upload/v1753113935/vvgqkeiktwaj38f1euk8.png',NULL,NULL,'2025-07-21 23:05:37','2025-07-21 23:07:26'),(2,'teacher1@gmail.com','$2a$10$vWzLF5jeZjSMyBrkV97yu.i9hhvXUZfbwDpiz5NUtMF6lCYV/Kvbm','Teacher1','TEACHER','https://res.cloudinary.com/dao8z029z/image/upload/v1753114551/u3lyxdgr3aejyglzzg0v.jpg',NULL,NULL,'2025-07-22 14:26:39','2025-08-07 22:18:35'),(3,'teacher2@gmail.com','$2a$10$vWzLF5jeZjSMyBrkV97yu.i9hhvXUZfbwDpiz5NUtMF6lCYV/Kvbm','Teacher2','TEACHER',NULL,NULL,NULL,'2025-07-23 11:59:55','2025-07-23 11:59:55'),(4,'student1@gmail.com','$2a$10$vWzLF5jeZjSMyBrkV97yu.i9hhvXUZfbwDpiz5NUtMF6lCYV/Kvbm','Student1','STUDENT','https://res.cloudinary.com/dao8z029z/image/upload/v1753114551/u3lyxdgr3aejyglzzg0v.jpg',NULL,NULL,'2025-07-21 23:15:53','2025-07-28 16:06:05'),(5,'student2@gmail.com','$2a$10$pRbVnKGJYp1bQec4Mt/XFumt9Zfu4E83KQJ4/M2hhFVIpdquqYCfK','Student2','STUDENT','https://res.cloudinary.com/dao8z029z/image/upload/v1753338618/l5ovtdzwu21vgma8lolg.jpg',NULL,NULL,'2025-07-28 13:30:19','2025-07-28 22:13:29');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-12  1:29:53
