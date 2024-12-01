-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Dec 07, 2023 at 08:34 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `poker`
--

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `student_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `wins` int(11) DEFAULT NULL,
  `second` int(11) DEFAULT NULL,
  `third` int(11) DEFAULT NULL,
  `fourth` int(11) DEFAULT NULL,
  `fifth` int(11) DEFAULT NULL,
  `sixth` int(11) DEFAULT NULL,
  `year` int(11) NOT NULL DEFAULT year(curdate()),
  `last_update` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `avg_bust_round` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `name`, `wins`, `second`, `third`, `fourth`, `fifth`, `sixth`, `year`, `last_update`, `avg_bust_round`) VALUES
(1, 'John Doe', 15, 5, 2, 1, 2, 0, 2019, '2023-12-07 07:49:26', 2.5),
(2, 'Jane Smith', 8, 4, 3, 2, 5, 0, 2019, '2023-12-07 04:50:01', 3),
(3, 'Alice Johnson', 12, 3, 1, 3, 1, 0, 2019, '2023-12-07 04:50:01', 2),
(4, 'Bob Anderson', 15, 2, 4, 1, 3, 0, 2023, '2023-12-07 04:50:01', 1.8),
(5, 'Eva Davis', 9, 6, 2, 2, 4, 0, 2022, '2023-12-07 04:50:01', 2.9),
(6, 'Michael Turner', 11, 3, 5, 1, 2, 0, 2023, '2023-12-07 04:50:01', 2.3),
(7, 'Sophia White', 13, 2, 4, 3, 1, 0, 2019, '2023-12-07 04:50:01', 1.5),
(8, 'Ryan Miller', 7, 5, 3, 2, 4, 0, 2022, '2023-12-07 04:50:01', 3.2),
(9, 'Emma Harris', 14, 1, 4, 2, 3, 0, 2019, '2023-12-07 04:50:01', 2.1),
(10, 'Daniel Clark', 10, 4, 2, 5, 1, 0, 2021, '2023-12-07 04:50:01', 2.7),
(11, 'Olivia Wilson', 9, 3, 2, 4, 1, 0, 2023, '2023-12-07 04:50:01', 2.4),
(12, 'Liam Brown', 12, 2, 1, 5, 3, 0, 2019, '2023-12-07 04:50:01', 1.9),
(13, 'Ava Lee', 10, 4, 3, 2, 1, 0, 2022, '2023-12-07 04:50:01', 2.8),
(14, 'Noah Taylor', 8, 3, 5, 2, 1, 0, 2021, '2023-12-07 04:50:01', 2.6),
(15, 'Isabella Martin', 11, 2, 1, 4, 3, 0, 2018, '2023-12-07 04:50:01', 1.7),
(16, 'Mia Jackson', 14, 1, 3, 2, 5, 0, 2019, '2023-12-07 04:50:01', 3.1),
(17, 'James Harris', 7, 5, 2, 4, 1, 0, 2020, '2023-12-07 04:50:01', 2.2),
(18, 'Sophie Davis', 12, 3, 2, 1, 4, 0, 2023, '2023-12-07 04:50:01', 2),
(19, 'Benjamin Adams', 9, 4, 1, 3, 2, 0, 2019, '2023-12-07 04:50:01', 2.9),
(20, 'Amelia Johnson', 10, 2, 5, 1, 3, 0, 2020, '2023-12-07 04:50:01', 1.8),
(21, 'Ethan Wilson', 11, 3, 4, 2, 1, 0, 2021, '2023-12-07 04:50:01', 2.5),
(22, 'Charlotte White', 8, 5, 2, 1, 3, 0, 2023, '2023-12-07 04:50:01', 2.7),
(23, 'Aiden Smith', 13, 1, 4, 3, 2, 0, 2021, '2023-12-07 04:50:01', 2.3),
(24, 'Lily Miller', 12, 2, 3, 5, 1, 0, 2019, '2023-12-07 04:50:01', 1.9),
(25, 'Lucas Anderson', 9, 4, 1, 2, 5, 0, 2022, '2023-12-07 04:50:01', 3),
(26, 'Grace Davis', 14, 1, 3, 2, 4, 0, 2022, '2023-12-07 04:50:01', 2.6),
(27, 'Jackson Harris', 10, 3, 2, 4, 1, 0, 2021, '2023-12-07 04:50:01', 2.1),
(28, 'Avery Taylor', 11, 2, 5, 1, 3, 0, 2020, '2023-12-07 04:50:01', 1.8),
(29, 'Zoe Martin', 8, 4, 1, 3, 2, 0, 2022, '2023-12-07 04:50:01', 2.9),
(30, 'Carter Adams', 15, 1, 3, 2, 4, 0, 2018, '2023-12-07 04:50:01', 2.4),
(31, 'Mila Wilson', 7, 5, 2, 4, 1, 0, 2021, '2023-12-07 04:50:01', 2.2),
(32, 'Elijah Brown', 12, 3, 2, 1, 5, 0, 2019, '2023-12-07 04:50:01', 2.7),
(33, 'Scarlett Lee', 9, 4, 1, 5, 2, 0, 2020, '2023-12-07 04:50:01', 1.6),
(34, 'Logan Turner', 10, 2, 3, 4, 1, 0, 2021, '2023-12-07 04:50:01', 2),
(35, 'Hazel Martin', 11, 3, 5, 2, 1, 0, 2019, '2023-12-07 04:50:01', 2.3),
(36, 'Jackson Davis', 8, 5, 2, 1, 3, 0, 2018, '2023-12-07 04:50:01', 2.5),
(37, 'Penelope Harris', 13, 1, 4, 3, 2, 0, 2021, '2023-12-07 04:50:01', 2.8),
(38, 'Leo Adams', 14, 2, 3, 1, 5, 0, 2019, '2023-12-07 04:50:01', 3.1),
(39, 'Aria White', 9, 4, 2, 5, 1, 0, 2023, '2023-12-07 04:50:01', 1.9),
(40, 'Grayson Smith', 10, 3, 1, 4, 2, 0, 2020, '2023-12-07 04:50:01', 2.6),
(41, 'Luna Taylor', 11, 2, 5, 1, 3, 0, 2022, '2023-12-07 04:50:01', 2.2),
(42, 'Mateo Miller', 8, 5, 2, 4, 1, 0, 2021, '2023-12-07 04:50:01', 2.4),
(43, 'Stella Harris', 12, 1, 3, 2, 5, 0, 2021, '2023-12-07 04:50:01', 2.7),
(44, 'William Davis', 7, 4, 2, 1, 3, 0, 2020, '2023-12-07 04:50:01', 2.1),
(45, 'Nova Wilson', 14, 2, 3, 5, 1, 0, 2022, '2023-12-07 04:50:01', 1.8),
(46, 'Owen Anderson', 15, 3, 4, 1, 2, 0, 2018, '2023-12-07 04:50:01', 2.9);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`student_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
