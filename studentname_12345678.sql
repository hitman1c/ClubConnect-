-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 13, 2025 at 09:54 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `studentname_12345678`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `present` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `budgets`
--

CREATE TABLE `budgets` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL,
  `requested_by` int(11) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `budgets`
--

INSERT INTO `budgets` (`id`, `event_id`, `requested_by`, `amount`, `status`, `notes`) VALUES
(1, 1, 1, 1000, 'approved', 'Test budget request');

-- --------------------------------------------------------

--
-- Table structure for table `clubs`
--

CREATE TABLE `clubs` (
  `id` int(11) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `archived` tinyint(4) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `clubs`
--

INSERT INTO `clubs` (`id`, `name`, `category`, `description`, `created_by`, `archived`) VALUES
(1, 'Soccer', 'ball', 'Soccer Club – Botho University\nThe Soccer Club at Botho University brings together students who share a passion for teamwork, fitness, and competitive spirit. Whether you are an experienced player or just starting out, this club offers an inclusive environment to develop skills, improve strategy, and enjoy the game. Members participate in regular training sessions, friendly matches, and inter-university competitions, fostering discipline, leadership, and camaraderie. Beyond the field, the club emphasizes community engagement and sportsmanship, making it a perfect platform to connect with fellow students while representing Botho University with pride.', 128, 1),
(2, 'Basketball', 'ball', '', 1, 1),
(3, 'Debate and Public Speaking Society', 'Academic', 'Focused on improving students’ communication, research, and argumentation skills. The society represents Botho University in national debate competitions and hosts campus discussions.', 1, 1),
(4, 'Botho Tech Innovators Club', 'Technology', 'A vibrant group of tech enthusiasts exploring programming, robotics, and innovation. The club holds hackathons, coding bootcamps, and AI workshops to build digital skills.', 1, 1),
(5, 'Drama and Performing Arts Club', 'Arts & Culture', 'This club allows creative students to showcase their acting, directing, and scriptwriting skills through stage plays, talent shows, and cultural events.', 182, 0),
(6, 'Environmental Awareness Club', 'Community & Environment', 'Dedicated to promoting sustainability and environmental protection. Members engage in clean-up campaigns, tree planting, and recycling drives around Maseru and beyond.', 1, 0),
(7, 'Entrepreneurship & Business Club', 'Business & Innovation', 'Encourages entrepreneurship by helping students develop business ideas, pitch competitions, and connect with successful entrepreneurs for mentorship.', 110, 0),
(8, 'Music and Creative Arts Club', 'Arts & Entertainment', 'Brings together talented singers, musicians, and artists to create and perform. The club organizes open mic nights, concerts, and recording sessions.', 115, 0),
(9, 'Christian Union', 'Faith & Fellowship', 'A spiritual community for Christian students who wish to grow in faith through prayer meetings, Bible studies, and outreach programs.', 1, 0),
(10, 'Health and Fitness Club', 'Wellness & Sports', 'Encourages healthy living through workouts, yoga sessions, and health awareness campaigns. Members promote physical fitness and mental wellness on campus.', 1, 0),
(11, 'Women Empowerment Network', 'Social Impact', 'Focuses on empowering female students through leadership training, mentorship programs, and campaigns addressing gender equality and inclusion.', 1, 0),
(12, 'Volunteer & Charity Club', 'Community Engagement', 'A club for compassionate students who wish to serve their communities through volunteer projects, donations, and visiting orphanages and hospitals.', 1, 0),
(13, 'Cybersecurity and Networking Club', 'Technology', 'Provides a platform for students interested in ethical hacking, cybersecurity, and network engineering. Offers workshops and simulations to build technical skills.', 160, 0),
(14, 'Photography and Media Club', 'Arts & Media', 'Brings together creative minds passionate about photography, videography, and digital media. Members document campus events and create media content.', 1, 0),
(15, 'Chess & Mind Games Club', 'Recreation', 'Encourages critical thinking and strategy through chess, checkers, and other board games. Members participate in friendly tournaments and competitions.', 1, 0),
(16, 'Cultural Exchange Society', 'Culture', 'Celebrates diversity by promoting Lesotho and African culture while encouraging cross-cultural understanding through traditional events and festivals.', 1, 0),
(17, 'Law and Justice Club', 'Academic', 'For students passionate about law, justice, and civic responsibility. The club organizes mock trials, debates, and visits to legal institutions.', 1, 0),
(18, 'Student Leadership Forum', 'Leadership', 'Develops leadership and management skills among students through workshops, motivational talks, and community leadership projects.', 1, 0),
(19, 'Botho Esports & Gaming Club', 'Technology & Entertainment', 'A fun, inclusive space for gamers to connect, compete, and explore the esports industry through campus tournaments and online gaming sessions.', 1, 0),
(20, 'Science and Innovation Society', 'STEM', 'Promotes scientific research, innovation, and experimentation through lab projects, exhibitions, and science fairs.', 1, 0),
(21, 'Marketing and Advertising Club', 'Business & Creativity', 'Combines creativity with business strategy. Members work on marketing campaigns, branding exercises, and advertising competitions.', 1, 0),
(22, 'Mathematics & Data Analytics Society', 'Academic', 'A group for math lovers and aspiring data scientists focusing on problem-solving, analytics, and research-based learning.', 1, 0),
(23, 'ICT Student Association', 'Technology', 'Supports ICT students through mentorship, certification study groups, and exposure to the latest trends in computing and information systems.', 1, 0),
(24, 'Journalism & Communication Club', 'Media & Communication', 'Trains aspiring journalists and communicators to write articles, conduct interviews, and publish the university newsletter.', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `created_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `event_id`, `user_id`, `message`, `created_at`) VALUES
(1, 1, 131, 'where exactly is it located', '2025-10-23 13:18:56'),
(2, 1, 131, 'fghj', '2025-10-23 13:20:02');

-- --------------------------------------------------------

--
-- Table structure for table `discussions`
--

CREATE TABLE `discussions` (
  `id` int(11) NOT NULL,
  `club_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `created_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `discussions`
--

INSERT INTO `discussions` (`id`, `club_id`, `user_id`, `content`, `created_at`) VALUES
(1, 1, 117, 'hi guys', '2025-10-21 01:37:46'),
(2, 1, 117, 'hi', '2025-10-21 01:37:56'),
(3, 7, 110, 'ola', '2025-11-02 19:38:01'),
(4, 6, 184, 'sad', '2025-11-11 16:27:28');

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `id` int(11) NOT NULL,
  `club_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `date_time` datetime DEFAULT NULL,
  `venue` varchar(200) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `details` text DEFAULT NULL,
  `budget_requested` tinyint(4) DEFAULT 0,
  `budget_status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `club_id`, `title`, `date_time`, `venue`, `capacity`, `details`, `budget_requested`, `budget_status`) VALUES
(1, 1, 'Soccer Tournament 2025', '2025-11-10 15:00:00', 'Main Stadium', 100, 'Annual inter-club soccer tournament', 127, 'pending'),
(2, 2, 'Friendly Soccer Match', '2025-10-25 18:30:00', 'Field A', 50, 'Friendly match between two local clubs', 127, 'approved'),
(3, 1, 'Youth Soccer Camp', '2025-12-05 09:00:00', 'Training Ground', 30, 'Soccer training camp for youth players', 127, 'pending'),
(4, 1, 'sechaba', '2025-11-28 11:00:00', 'motanyane', 50, 'b', 0, 'pending');

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `id` int(11) NOT NULL,
  `club_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `created_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `memberships`
--

CREATE TABLE `memberships` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `club_id` int(11) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `memberships`
--

INSERT INTO `memberships` (`id`, `user_id`, `club_id`, `role`, `status`) VALUES
(1, 1, 1, 'member', 'active'),
(2, 2, 1, 'leader', 'active'),
(3, 3, 2, 'member', 'pending'),
(4, 4, 2, 'member', 'active'),
(5, 5, 1, 'member', 'inactive'),
(6, 105, 1, 'leader', 'pending'),
(7, 110, 1, 'leader', 'pending'),
(8, 115, 1, 'leader', 'pending'),
(9, 117, 2, 'leader', 'pending'),
(10, 128, 24, 'leader', 'pending'),
(11, 51, 7, 'member', 'approved'),
(12, 36, 22, 'member', 'approved'),
(13, 131, 1, 'member', 'approved'),
(14, 152, 23, 'member', 'approved'),
(15, 160, 13, 'leader', 'pending'),
(16, 175, 7, 'member', 'approved'),
(17, 178, 20, 'member', 'approved'),
(18, 182, 5, 'leader', 'pending'),
(19, 184, 6, 'member', 'approved'),
(20, 194, 3, 'member', 'approved'),
(21, 198, 6, 'member', 'approved'),
(22, 11, 184, 'pending', '2025-11-11'),
(23, 1, 184, 'pending', '2025-11-11'),
(24, 18, 184, 'pending', '2025-11-11');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `club_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `sent` tinyint(4) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

CREATE TABLE `password_resets` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(128) NOT NULL,
  `expires_at` datetime NOT NULL,
  `used` tinyint(4) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `password_resets`
--

INSERT INTO `password_resets` (`id`, `user_id`, `token`, `expires_at`, `used`) VALUES
(1, 160, '8bdf8f45-a604-4742-b92b-1ffe208ecb1e', '2025-10-23 14:45:14', 0),
(2, 160, 'f1cf3101-1666-47dc-924e-72434058c66b', '2025-10-23 14:45:43', 0),
(3, 160, '47a409ff-4487-4b48-801f-e73c340c7db7', '2025-10-23 15:12:49', 0),
(4, 160, 'a55dd4b9-0cbc-45fc-8768-8ab6095b66fb', '2025-10-23 15:18:51', 0),
(5, 160, 'b38651e2-c832-45ff-9820-f728edd5841f', '2025-10-23 15:22:42', 0),
(6, 160, 'afd20169-e97a-4bfd-b558-2fc00252d5fa', '2025-10-23 15:26:25', 0),
(7, 160, '29f0dbc9-ae30-4391-b11a-f83d44956396', '2025-10-23 15:29:16', 0),
(8, 160, 'f2d8a55d-bae8-4751-b2e7-dffcebfe5508', '2025-10-23 15:32:50', 0),
(9, 160, '323a2538-4ea8-413b-bc69-3b9402f4cdaf', '2025-10-23 15:40:28', 0),
(10, 160, '9ad84b76-b04a-45de-9940-7e4817bc2490', '2025-10-23 15:52:51', 0),
(11, 160, '70f96195-2442-40f7-ab55-bc7eefd209f0', '2025-10-23 15:57:39', 0),
(12, 160, '03a10966-e204-4f7f-90fd-9c1950263856', '2025-10-23 16:20:22', 0),
(13, 160, '846c41d8-5983-49d3-bd23-183f6aeb1301', '2025-10-23 16:48:34', 0),
(14, 160, '1d22a868-4a70-471b-9d44-4fcb8d084c35', '2025-10-23 16:49:36', 0),
(15, 175, '2f059524-0083-41ef-9823-daac0ec0a1ab', '2025-10-23 16:52:49', 0),
(16, 175, 'eb5b2779-9e93-49d4-87a5-9d6b8fc952c4', '2025-10-23 16:58:06', 0),
(17, 175, '72dcd020-4382-4cdb-bace-e162ca50cdf9', '2025-10-23 17:00:20', 0),
(18, 178, 'd76d522c-08da-489f-a85c-b4a48f3479b8', '2025-10-23 17:08:33', 0),
(19, 182, '2366ef7d-263c-43e9-a95a-8dd3094fe991', '2025-10-23 17:30:13', 0),
(20, 182, 'e8a00f14-7e18-4d3a-b9e5-fd44435c5b6d', '2025-10-23 17:32:02', 0),
(21, 160, '8dc5143f-e3d5-4689-950c-06a143f24493', '2025-10-23 17:34:39', 0),
(22, 184, 'a0dc3b53-72ec-421a-9c32-a0307c4fbf0f', '2025-10-23 17:45:45', 0),
(23, 160, 'b8ec7d18-3a0c-4660-a90c-245decb6dd9b', '2025-10-23 18:14:48', 0),
(24, 194, '486bfbc2-3f82-4cc4-935e-2153e8a1937d', '2025-10-23 18:17:34', 0),
(25, 160, '57a0eda5-d375-40e2-ab2d-b463a99e46ab', '2025-10-23 18:22:40', 0),
(26, 160, 'ea157910-1a73-427b-a796-c66a1398fc2a', '2025-10-23 23:52:48', 0),
(27, 160, '89ca7ed1-5cf7-44fa-8835-da9e0b1febc9', '2025-10-23 23:53:35', 0),
(28, 175, '869afafe-ea43-4937-939a-21ec4f9620e0', '2025-10-23 23:54:20', 0),
(29, 160, 'b4ecbc00-54ed-4e8f-ad70-9274617205b0', '2025-10-23 23:55:25', 0),
(30, 160, 'eff1a2b0-6fc2-4c50-9266-726c3a3b731b', '2025-10-23 23:56:53', 0),
(31, 198, 'e8436d80-5574-4593-8aa4-24c3e017dd65', '2025-10-23 23:57:39', 0),
(32, 160, '05c1f3ad-13dc-4b6c-9932-d0a6bb86add1', '2025-10-24 00:12:56', 0),
(33, 160, '1422a0bf-366d-4ac8-83a4-54eccaaa994b', '2025-11-10 17:02:32', 0),
(34, 175, '8f8c3cb1-f45d-405d-b6fd-910f8f6d273e', '2025-11-10 17:02:57', 0),
(35, 160, '71db183d-421c-4c3a-862e-426d6cdecc31', '2025-11-10 17:14:25', 0);

-- --------------------------------------------------------

--
-- Table structure for table `resources`
--

CREATE TABLE `resources` (
  `id` int(11) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `available` tinyint(4) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `resources`
--

INSERT INTO `resources` (`id`, `name`, `capacity`, `description`, `available`) VALUES
(1, 'Main Hall', 200, 'Large venue', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `full_name` varchar(200) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `student_id` varchar(100) DEFAULT NULL,
  `approved` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `role`, `email`, `student_id`, `approved`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator', 'admin', 'admin@university.edu', 'ADMIN000', 1),
(36, 'seabata', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'sechaba me', 'member', 'seabatasechaba@gmail.com', '2333774', 1),
(51, 'karabo', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'karabo me', 'member', 'seabatasechaba@gmail.com', '2333779', 1),
(53, 'karabo1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'karabo me', 'member', 'seabatasechaba@gmail.com', '2333779', 1),
(110, 'kananelo1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'palesa', 'leader', 'palesa@gmail.com', '12345678', 1),
(115, 'seabata1', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'sechaba me1', 'leader', 'seabata@gmail.com', '23337779', 1),
(117, 'mpho', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'karabo mpho', 'leader', 'mpho@gmail.com', '2333779', 1),
(128, 'morena', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'morena', 'leader', 'y@botho.com', '1234567543', 1),
(131, 't', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 't', 'member', 't@botho.com', '555', 1),
(152, 'p', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 't', 'member', 'u@botho.com', '12345678', 1),
(160, 'kalo', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'dfgh', 'leader', 'seabatasechaba0@gmail.com', '123456', 1),
(175, 'relebohile', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'mah', 'member', 'seabatasechaba57@gmail.com', '12345232', 1),
(178, 'Nkopane Matsaba', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'wergh', 'member', 'nkopane.matsaba@bothouniversity.com', '12345678', 1),
(182, 'Morena1', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Morena Sechaba', 'leader', 'sechabamorena6@gmail.com', '123456789', 1),
(184, 'rama', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'popza', 'member', 'hlompho.ramathaha@bothouniversity.com', '1245678', 1),
(194, 'Babe', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225', 'thato', 'member', 'tlalithato20@gmalil.com', '2345678', 1),
(198, 'phaello', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', 'Phaello Motsienyane', 'member', 'phaello.motsienyane@bothouniversity.com', '2345678', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `budgets`
--
ALTER TABLE `budgets`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `clubs`
--
ALTER TABLE `clubs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `event_id` (`event_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `discussions`
--
ALTER TABLE `discussions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `memberships`
--
ALTER TABLE `memberships`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `resources`
--
ALTER TABLE `resources`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `budgets`
--
ALTER TABLE `budgets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `clubs`
--
ALTER TABLE `clubs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `discussions`
--
ALTER TABLE `discussions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `memberships`
--
ALTER TABLE `memberships`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `password_resets`
--
ALTER TABLE `password_resets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT for table `resources`
--
ALTER TABLE `resources`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=249;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD CONSTRAINT `password_resets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
