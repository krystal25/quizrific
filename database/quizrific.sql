-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 17, 2019 at 04:58 PM
-- Server version: 5.7.26-0ubuntu0.16.04.1
-- PHP Version: 7.0.33-0ubuntu0.16.04.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `quizrific`
--

-- --------------------------------------------------------

--
-- Table structure for table `answers`
--

CREATE TABLE `answers` (
  `id` int(20) NOT NULL,
  `question_id` int(20) NOT NULL,
  `answer` varchar(100) NOT NULL,
  `isCorrect` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `answers`
--

INSERT INTO `answers` (`id`, `question_id`, `answer`, `isCorrect`) VALUES
(1, 1, 'Transport', 1),
(2, 1, 'Network', 0),
(3, 1, 'Session', 0),
(4, 2, 'It stands for \'Transmission Control Protocol\'', 1),
(5, 2, 'It is a connection-oriented protocol', 1),
(6, 2, 'It is a connectionless protocol', 0),
(7, 2, 'If one data packet is lost during transmission, it will not send that packet again', 0),
(8, 3, 'True', 1),
(9, 3, 'False', 0),
(10, 4, 'True', 1),
(11, 4, 'False', 0),
(287, 74, 'Server', 1),
(288, 74, 'Client', 0);

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
  `id` int(20) NOT NULL,
  `course` varchar(40) NOT NULL,
  `course_year` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`id`, `course`, `course_year`) VALUES
(1, 'Computer Networks', '3'),
(2, 'Software Engineering', '3'),
(3, 'Calculus', '2'),
(4, 'Biology', '2'),
(5, 'Computer Graphics', '3'),
(6, 'German', '1'),
(7, 'Linear Algebra', '1'),
(8, 'Physics', '1');

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments` (
  `id` int(20) NOT NULL,
  `department` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`id`, `department`) VALUES
(5, 'Architecture'),
(2, 'Biochemistry'),
(1, 'Computer Science'),
(4, 'Finance'),
(6, 'Languages'),
(3, 'Mathematics'),
(7, 'Veterinary Medicine');

-- --------------------------------------------------------

--
-- Table structure for table `dept_courses`
--

CREATE TABLE `dept_courses` (
  `id` int(11) NOT NULL,
  `department_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dept_courses`
--

INSERT INTO `dept_courses` (`id`, `department_id`, `course_id`) VALUES
(1, 2, 4),
(2, 1, 5),
(3, 1, 1),
(4, 1, 2),
(5, 3, 3),
(6, 3, 7),
(7, 6, 6);

-- --------------------------------------------------------

--
-- Table structure for table `offerings`
--

CREATE TABLE `offerings` (
  `id` int(20) NOT NULL,
  `course_id` int(20) NOT NULL,
  `professor_id` int(20) NOT NULL,
  `department_id` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `offerings`
--

INSERT INTO `offerings` (`id`, `course_id`, `professor_id`, `department_id`) VALUES
(11, 1, 14, 1),
(6, 2, 14, 1),
(12, 3, 14, 3),
(4, 4, 9, 2),
(17, 4, 14, 2),
(13, 5, 14, 1),
(15, 6, 14, 6),
(7, 7, 14, 3);

-- --------------------------------------------------------

--
-- Table structure for table `professors`
--

CREATE TABLE `professors` (
  `professor_id` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `professors`
--

INSERT INTO `professors` (`professor_id`) VALUES
(9),
(14);

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `id` int(20) NOT NULL,
  `question` varchar(200) NOT NULL,
  `points` float NOT NULL,
  `image` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`id`, `question`, `points`, `image`) VALUES
(1, 'What is the 4th layer of the OSI model?', 3, 'http://35.204.4.163/quizrific/images/1188384644_1558726123.jpeg'),
(2, 'Which of the following is true about TCP?', 4, NULL),
(3, 'SDLC is short for Software Development Life Cycle. ', 0.5, NULL),
(4, 'ARP is used to find LAN address from Network address. ', 1.5, NULL),
(74, 'Number 2 in the figure above represents the:', 0.5, 'http://35.204.4.163/quizrific/images/1052318509_1560094037.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `quizzes`
--

CREATE TABLE `quizzes` (
  `id` int(20) NOT NULL,
  `professor_id` int(20) NOT NULL,
  `course_id` int(20) NOT NULL,
  `quiz` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `quizzes`
--

INSERT INTO `quizzes` (`id`, `professor_id`, `course_id`, `quiz`) VALUES
(91, 9, 4, 'Basic Quiz'),
(158, 14, 1, 'Final Quiz'),
(159, 14, 1, 'Initial Quiz'),
(1, 14, 1, 'Midterm Exam'),
(2, 14, 2, 'Basic Quiz'),
(154, 14, 7, 'Quiz');

-- --------------------------------------------------------

--
-- Table structure for table `quizzes_questions`
--

CREATE TABLE `quizzes_questions` (
  `id` int(20) NOT NULL,
  `quiz_id` int(20) NOT NULL,
  `question_id` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `quizzes_questions`
--

INSERT INTO `quizzes_questions` (`id`, `quiz_id`, `question_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(4, 2, 3),
(3, 1, 4),
(70, 1, 74);

-- --------------------------------------------------------

--
-- Table structure for table `quiz_results`
--

CREATE TABLE `quiz_results` (
  `id` int(20) NOT NULL,
  `student_id` int(20) NOT NULL,
  `quiz_id` int(20) NOT NULL,
  `result` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `quiz_results`
--

INSERT INTO `quiz_results` (`id`, `student_id`, `quiz_id`, `result`) VALUES
(1, 1, 1, 9.5),
(2, 1, 91, 9.5),
(3, 40, 1, 6),
(4, 41, 1, 4),
(6, 42, 1, 7),
(7, 43, 1, 8.5),
(8, 44, 1, 8.5),
(9, 45, 1, 7),
(10, 46, 1, 6.5),
(11, 47, 1, 8.5),
(12, 48, 1, 9.5),
(13, 49, 1, 5),
(15, 5, 2, 6),
(16, 40, 2, 4),
(17, 41, 2, 3),
(18, 42, 2, 7),
(35, 49, 2, 9.5),
(36, 43, 2, 8),
(37, 44, 2, 9),
(38, 45, 2, 8),
(39, 46, 2, 7),
(40, 47, 2, 9),
(45, 48, 2, 7),
(48, 5, 158, 5),
(54, 1, 158, 8),
(55, 1, 159, 6),
(57, 5, 1, 9.5),
(67, 5, 159, 7);

-- --------------------------------------------------------

--
-- Table structure for table `scheduled_quizzes`
--

CREATE TABLE `scheduled_quizzes` (
  `id` int(20) NOT NULL,
  `quiz_id` int(20) NOT NULL,
  `quiz_date` date NOT NULL,
  `quiz_hour` varchar(5) NOT NULL,
  `duration` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `scheduled_quizzes`
--

INSERT INTO `scheduled_quizzes` (`id`, `quiz_id`, `quiz_date`, `quiz_hour`, `duration`) VALUES
(29, 2, '2019-06-04', '18:30', 10),
(30, 1, '2019-06-17', '16:00', 120),
(31, 159, '2019-04-01', '18:00', 20),
(32, 158, '2019-06-17', '15:00', 30);

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `student_id` int(20) NOT NULL,
  `student_year` int(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `student_year`) VALUES
(1, 1),
(5, 3),
(40, 1),
(41, 3),
(42, NULL),
(43, NULL),
(44, NULL),
(45, NULL),
(46, NULL),
(47, NULL),
(48, NULL),
(49, NULL),
(50, NULL),
(51, NULL),
(53, NULL),
(56, NULL),
(57, NULL),
(58, NULL),
(59, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `students_courses`
--

CREATE TABLE `students_courses` (
  `id` int(20) NOT NULL,
  `student_id` int(20) NOT NULL,
  `course_id` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students_courses`
--

INSERT INTO `students_courses` (`id`, `student_id`, `course_id`) VALUES
(1, 1, 1),
(4, 1, 2),
(2, 1, 5),
(5, 5, 1),
(7, 5, 2),
(6, 5, 5);

-- --------------------------------------------------------

--
-- Table structure for table `students_departments`
--

CREATE TABLE `students_departments` (
  `id` int(20) NOT NULL,
  `student_id` int(20) NOT NULL,
  `department_id` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students_departments`
--

INSERT INTO `students_departments` (`id`, `student_id`, `department_id`) VALUES
(1, 1, 1),
(2, 5, 1);

-- --------------------------------------------------------

--
-- Table structure for table `students_quizzes`
--

CREATE TABLE `students_quizzes` (
  `id` int(20) NOT NULL,
  `student_id` int(20) NOT NULL,
  `quiz_id` int(20) NOT NULL,
  `question_id` int(20) NOT NULL,
  `answer_id` int(20) NOT NULL,
  `points_awarded` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students_quizzes`
--

INSERT INTO `students_quizzes` (`id`, `student_id`, `quiz_id`, `question_id`, `answer_id`, `points_awarded`) VALUES
(1, 1, 1, 1, 1, '0.5'),
(2, 1, 1, 2, 4, '0.5'),
(3, 1, 1, 2, 5, '1'),
(143, 5, 1, 1, 1, '3.0'),
(144, 5, 1, 2, 4, '2.0'),
(145, 5, 1, 2, 5, '2.0'),
(146, 5, 1, 4, 10, '1.5'),
(147, 5, 1, 74, 288, '0'),
(149, 5, 2, 3, 9, '0');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(20) NOT NULL,
  `email` varchar(40) NOT NULL,
  `username` varchar(10) NOT NULL,
  `password` varchar(256) NOT NULL,
  `firstname` varchar(20) DEFAULT NULL,
  `lastname` varchar(20) DEFAULT NULL,
  `enabled` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `username`, `password`, `firstname`, `lastname`, `enabled`) VALUES
(1, 'cristinna97@yahoo.com', 'dcristina', '$2y$10$ONo8zSsaNp5RsIYNHDu3guZ9DzefEKpFlw59bE0DWUd4bQEnIgo4e', 'Cristina', 'Draghici', NULL),
(5, 'x', 'ioana_d', '$2y$10$WvbTNzKZ2Xua1oQmzL4dOeUArUbT/CTI0UzYRgKJQW6DgaS7yBePO', 'x', 'x', NULL),
(9, 'b@email.com', 'prof_bio', '$2y$10$pPRacaClIenDJAdWVODOfOQju9A.RiZad5efpFsfC0Gs7J1w7OiPW', 'b', 'b', NULL),
(14, 'prof@gmail.com', 'prof', '$2y$10$KuAE/1nOUbaPlQ42aGQ9OeXyiFHfbVoeGkuQJ4UxRphX9..iHBDiq', 'Ioana', 'Ionescu', NULL),
(40, 'student', 'student', 'test', 'test', 'test', NULL),
(41, 'student2', 'student2', 'test2', 'test2', 'test2', NULL),
(42, 'student3', 'student3', 'test3', 'test3', 'test3', NULL),
(43, 'student4', 'student4', 'test4', 'test4', 'test4', NULL),
(44, 'student5', 'student5', 'test5', 'test5', 'test5', NULL),
(45, 'student6', 'student6', 'test6', 'test6', 'test6', NULL),
(46, 'student7', 'student7', 'test7', 'test7', 'test7', NULL),
(47, 'student8', 'student8', 'test8', 'test8', 'test8', NULL),
(48, 'student9', 'student9', 'test9', 'test9', 'test9', NULL),
(49, 'student10', 'student10', 'test10', 'test10', 'test10', NULL),
(50, 't', 't', '$2y$10$qwfpeSqE.bo6gaZOCmxkIeble0hTy8738DMRnmjEV9p0qiyedKKPq', 't', 't', NULL),
(51, 't2', 't2', '$2y$10$W.18KrVccA8wVnW7n3LIc.MlhjSadzO3fyL5njwIQE2XCCxwQiQRC', 't', 't', NULL),
(52, 't3', 't3', '$2y$10$jTYS3MTZ/S0pKUjKRYFgMOAYQ68l0EVbSnMZvZqumJzkQ85oSNX0G', 't', 't', NULL),
(53, 't4', 't4', '$2y$10$sTXLL1I2JAGlLQq31N00k.Lt/erfHPJQ/3mZmDxdJUZqSXs1tvIfK', 't', 't', NULL),
(54, 't5', 't5', '$2y$10$LWsdSv9XfmBBnSX8QpQprOudvzCgIW6vlJKmej7bORaTkJtOrpENe', 't', 't', NULL),
(55, 't6', 't6', '$2y$10$ABYKPKtJOV.pnDV1vjSP7uTYpQGLpaNc2mmwG1MKAFKBlchqO5uOm', 't', 't', NULL),
(56, 't7', 't7', '$2y$10$NuAL2lByPxZMKG9npb.K3O8SoEHafdX.ZX99BLr1rPHAt0/7JY23q', 't', 't', NULL),
(57, 't8', 't8', '$2y$10$ACkrk6C7vUPNFsFQE0WKQuhG2HciSCj57pyyspC5Ogu.H9GjLrRCm', 't', 't', NULL),
(58, 'aaa@mailinator.com', 'aaa', '$2y$10$WL/rUCDk4qC1tHu/gC7Vj.RsAy0wo07CvzQE8y2QtjNYFsb7qN/VW', '1234', 'AAA', NULL),
(59, 'dfgg@gmail.com', 'testuletz', '$2y$10$o/eie.wKOPFiSUx2CbAJZeJaT/b0.Enrq9bjWhsJkDQcMg9DSK0PK', 'cgh', 'chh', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `answers`
--
ALTER TABLE `answers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `question_id` (`question_id`,`answer`);

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `course` (`course`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `department` (`department`);

--
-- Indexes for table `dept_courses`
--
ALTER TABLE `dept_courses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_department` (`department_id`),
  ADD KEY `FK_COURSE` (`course_id`);

--
-- Indexes for table `offerings`
--
ALTER TABLE `offerings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `composite_key` (`course_id`,`professor_id`,`department_id`) USING BTREE,
  ADD KEY `offerings_ibfk_2` (`department_id`),
  ADD KEY `offerings_ibfk_3` (`professor_id`);

--
-- Indexes for table `professors`
--
ALTER TABLE `professors`
  ADD KEY `professors_ibfk_1` (`professor_id`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `question` (`question`);

--
-- Indexes for table `quizzes`
--
ALTER TABLE `quizzes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `professor` (`professor_id`,`course_id`,`quiz`),
  ADD KEY `quizzes_ibfk_4` (`course_id`);

--
-- Indexes for table `quizzes_questions`
--
ALTER TABLE `quizzes_questions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `question_id` (`question_id`,`quiz_id`),
  ADD KEY `quizzes_questions_ibfk_2` (`quiz_id`);

--
-- Indexes for table `quiz_results`
--
ALTER TABLE `quiz_results`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student` (`student_id`,`quiz_id`),
  ADD KEY `quiz_results_ibfk_2` (`quiz_id`);

--
-- Indexes for table `scheduled_quizzes`
--
ALTER TABLE `scheduled_quizzes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `course` (`quiz_id`,`quiz_date`,`quiz_hour`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD UNIQUE KEY `student_id` (`student_id`);

--
-- Indexes for table `students_courses`
--
ALTER TABLE `students_courses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student` (`student_id`,`course_id`),
  ADD KEY `students_courses_ibfk_4` (`course_id`);

--
-- Indexes for table `students_departments`
--
ALTER TABLE `students_departments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student` (`student_id`,`department_id`),
  ADD KEY `students_departments_ibfk_2` (`department_id`);

--
-- Indexes for table `students_quizzes`
--
ALTER TABLE `students_quizzes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `my_unique_index` (`student_id`,`quiz_id`,`question_id`,`answer_id`),
  ADD KEY `students_quizzes_ibfk_2` (`quiz_id`),
  ADD KEY `students_quizzes_ibfk_3` (`question_id`),
  ADD KEY `students_quizzes_ibfk_4` (`answer_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `answers`
--
ALTER TABLE `answers`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=297;
--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `dept_courses`
--
ALTER TABLE `dept_courses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `offerings`
--
ALTER TABLE `offerings`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;
--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;
--
-- AUTO_INCREMENT for table `quizzes`
--
ALTER TABLE `quizzes`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=162;
--
-- AUTO_INCREMENT for table `quizzes_questions`
--
ALTER TABLE `quizzes_questions`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=76;
--
-- AUTO_INCREMENT for table `quiz_results`
--
ALTER TABLE `quiz_results`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;
--
-- AUTO_INCREMENT for table `scheduled_quizzes`
--
ALTER TABLE `scheduled_quizzes`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;
--
-- AUTO_INCREMENT for table `students_courses`
--
ALTER TABLE `students_courses`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `students_departments`
--
ALTER TABLE `students_departments`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `students_quizzes`
--
ALTER TABLE `students_quizzes`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=150;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `answers`
--
ALTER TABLE `answers`
  ADD CONSTRAINT `answers_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `dept_courses`
--
ALTER TABLE `dept_courses`
  ADD CONSTRAINT `FK_COURSE` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `offerings`
--
ALTER TABLE `offerings`
  ADD CONSTRAINT `offerings_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `offerings_ibfk_3` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`professor_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `offerings_ibfk_4` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `professors`
--
ALTER TABLE `professors`
  ADD CONSTRAINT `professors_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `quizzes`
--
ALTER TABLE `quizzes`
  ADD CONSTRAINT `quizzes_ibfk_3` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`professor_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `quizzes_ibfk_4` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `quizzes_questions`
--
ALTER TABLE `quizzes_questions`
  ADD CONSTRAINT `quizzes_questions_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `quizzes_questions_ibfk_2` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `quiz_results`
--
ALTER TABLE `quiz_results`
  ADD CONSTRAINT `quiz_results_ibfk_2` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `quiz_results_ibfk_3` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `scheduled_quizzes`
--
ALTER TABLE `scheduled_quizzes`
  ADD CONSTRAINT `scheduled_quizzes_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `students_courses`
--
ALTER TABLE `students_courses`
  ADD CONSTRAINT `students_courses_ibfk_3` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `students_courses_ibfk_4` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `students_departments`
--
ALTER TABLE `students_departments`
  ADD CONSTRAINT `students_departments_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `students_departments_ibfk_3` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `students_quizzes`
--
ALTER TABLE `students_quizzes`
  ADD CONSTRAINT `students_quizzes_ibfk_2` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `students_quizzes_ibfk_3` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `students_quizzes_ibfk_4` FOREIGN KEY (`answer_id`) REFERENCES `answers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `students_quizzes_ibfk_5` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
