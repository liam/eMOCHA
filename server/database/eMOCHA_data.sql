-- phpMyAdmin SQL Dump
-- version 3.1.2deb1ubuntu0.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 26, 2009 at 11:04 AM
-- Server version: 5.0.75
-- PHP Version: 5.2.6-3ubuntu4.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `admin_emocha`
--

--
-- Dumping data for table `sys_group`
--

INSERT INTO `sys_group` (`name`, `description`) VALUES
('root', 'can do everything'),
('translator_read', 'can see translators'),
('anonymous', 'group with least privileges'),
('translator_write', 'can edit translators');

--
-- Dumping data for table `sys_menu`
--

INSERT INTO `sys_menu` (`ID`, `name`, `groups`, `order`) VALUES
('main', 'Main', 'root', 10),
('edu', 'Edu', 'root', 20),
('telemed', 'TeleMed', 'root', 30),
('stats', 'Stats', 'root', 40),
('handsets', 'Handsets', 'root', 50);

--
-- Dumping data for table `sys_submenu`
--

INSERT INTO `sys_submenu` (`ID`, `parentID`, `name`, `groups`, `order`) VALUES
('eduCourses', 'edu', 'Courses', 'root', 0),
('statsExport', 'stats', 'Export', 'root', 0),
('eduLectures', 'edu', 'Lectures', 'root', 0),
('eduLibrary', 'edu', 'Library', 'root', 0),
('handsetsAdd', 'handsets', 'Add', 'root', 20),
('handsetsList', 'handsets', 'List', 'root', 10),
('handsetsLocation', 'handsets', 'Location', 'root', 0),
('mainAlarms', 'main', 'Alarms', 'root', 10),
('mainDataSel', 'main', 'Data Sel', 'root', 0),
('mainMessages', 'main', 'Messages', 'root', 20),
('statsDataSel', 'stats', 'Data Sel', 'root', 0),
('statsEnterData', 'stats', 'Enter Data', 'root', 0),
('telemedAlarms', 'telemed', 'Alarms', 'root', 0),
('telemedDataSel', 'telemed', 'Data Sel', 'root', 0),
('telemedMessages', 'telemed', 'Messages', 'root', 0),
('mainRefresh', 'main', 'Refresh', 'root', 40);
