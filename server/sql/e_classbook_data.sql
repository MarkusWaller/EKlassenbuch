-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 22. Apr 2015 um 20:18
-- Server Version: 5.6.21
-- PHP-Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `e_classbook`
--

--
-- Daten für Tabelle `book`
--

INSERT INTO `book` (`book_id`, `date`, `subject`, `teacher`,`class`, `info`) VALUES
(1, '2015-04-22', 'IT', 1, 'TGQ1c', 'Klassenarbeit geschrieben');

--
-- Daten für Tabelle `class`
--

INSERT INTO `class` (`name`, `h_teacher`) VALUES
('TGQ1c', 1);

--
-- Daten für Tabelle `student`
--

INSERT INTO `student` (`student_id`, `first_name`, `last_name`, `class`, `birth_date`, `email`, `password`) VALUES
(1, 'Markus', 'Waller', 'TGQ1c', '1996-12-07', 'waller.markus@gmail.com', 'MeinPasswort');

--
-- Daten für Tabelle `teacher`
--

INSERT INTO `teacher` (`teacher_id`, `last_name`, `first_name`, `email`, `password`) VALUES
(1, 'Mustermann', 'Max', 'max@mustermann.de', 'Musterpasswort');

--
-- Daten für Tabelle `admin`
--

INSERT INTO `admin` (`admin_id`, `first_name`, `last_name`) VALUES


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
