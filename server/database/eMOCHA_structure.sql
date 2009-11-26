-- phpMyAdmin SQL Dump
-- version 3.1.2deb1ubuntu0.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 26, 2009 at 11:23 AM
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

-- --------------------------------------------------------

--
-- Table structure for table `form_data`
--

CREATE TABLE IF NOT EXISTS `form_data` (
  `ID` int(11) NOT NULL auto_increment,
  `ts` int(11) NOT NULL,
  `xml` text NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=209 ;

-- --------------------------------------------------------

--
-- Table structure for table `phone`
--

CREATE TABLE IF NOT EXISTS `phone` (
  `ID` int(11) unsigned NOT NULL auto_increment,
  `imei` varchar(32) NOT NULL COMMENT 'IMEI code of the phone',
  `imei_md5` varchar(32) NOT NULL COMMENT 'MD5 hash of the IMEI code',
  `validated` tinyint(1) NOT NULL COMMENT 'phones are inactive until activated in the control panel',
  `last_connect_ts` int(11) NOT NULL COMMENT 'timestamp of last phone call to the server',
  `pwd` varchar(64) NOT NULL COMMENT 'password using the mysql PASSWORD function, to be entered in the phone',
  `gps` varchar(128) NOT NULL COMMENT 'last phone position',
  `comments` varchar(200) NOT NULL COMMENT 'maybe an alias or the name of the owner of the phone',
  `creation_ts` int(11) NOT NULL COMMENT 'timestamp of phone''s first call to the server',
  `creation_ip` varchar(15) NOT NULL COMMENT 'phone''s IP address when it was added to this table',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `imei` (`imei`),
  UNIQUE KEY `imei_md5` (`imei_md5`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COMMENT='list of phones wanting to connect to api.php' AUTO_INCREMENT=14 ;

-- --------------------------------------------------------

--
-- Table structure for table `sdcard`
--

CREATE TABLE IF NOT EXISTS `sdcard` (
  `path` varchar(512) NOT NULL COMMENT 'path of a file in the sdcard folder',
  `ts` int(11) NOT NULL COMMENT 'timestamp of last modification',
  `size` int(11) NOT NULL COMMENT 'file size in bytes',
  `md5` varchar(32) NOT NULL COMMENT 'md5 hash of the file',
  PRIMARY KEY  (`path`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='content of the sdcard folder, files will be sent to phones';

-- --------------------------------------------------------

--
-- Table structure for table `sys_group`
--

CREATE TABLE IF NOT EXISTS `sys_group` (
  `name` varchar(32) NOT NULL COMMENT 'group name, using lowercase and underscores',
  `description` varchar(255) NOT NULL COMMENT 'short explanation',
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='User groups that can access the tools';

-- --------------------------------------------------------

--
-- Table structure for table `sys_menu`
--

CREATE TABLE IF NOT EXISTS `sys_menu` (
  `ID` varchar(32) NOT NULL COMMENT 'lowercase group name, no spaces',
  `name` varchar(32) NOT NULL COMMENT 'readable group name',
  `groups` varchar(1024) NOT NULL COMMENT 'comma separated list of allowed group IDs',
  `order` int(11) NOT NULL COMMENT 'number used for sorting',
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sys_session`
--

CREATE TABLE IF NOT EXISTS `sys_session` (
  `session_id` varchar(32) NOT NULL,
  `usr` varchar(32) NOT NULL,
  `ts` int(11) NOT NULL,
  `menuHTML` varchar(10000) NOT NULL,
  `submenuHTML` varchar(10000) NOT NULL,
  PRIMARY KEY  (`session_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Valid sessions for users using the tools';

-- --------------------------------------------------------

--
-- Table structure for table `sys_submenu`
--

CREATE TABLE IF NOT EXISTS `sys_submenu` (
  `ID` varchar(32) NOT NULL COMMENT 'submenu lowercase name, no spaces',
  `parentID` varchar(32) NOT NULL COMMENT 'ID of a menu',
  `name` varchar(32) NOT NULL COMMENT 'readable submenu name',
  `groups` varchar(1024) NOT NULL COMMENT 'comma separated list of allowed group IDs',
  `order` int(11) NOT NULL COMMENT 'number used for sorting',
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sys_user`
--

CREATE TABLE IF NOT EXISTS `sys_user` (
  `usr` varchar(32) NOT NULL COMMENT 'name used for log in',
  `alias` varchar(32) NOT NULL COMMENT 'readable name or nickname',
  `pwd` varchar(64) NOT NULL COMMENT 'password using the mysql PASSWORD function',
  `groups` text NOT NULL COMMENT 'comma separated list of groups',
  `lang` varchar(2) NOT NULL default 'DE' COMMENT 'two character upper case language code',
  PRIMARY KEY  (`usr`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Users who can access the tools';

-- --------------------------------------------------------

--
-- Table structure for table `uploaded_data`
--

CREATE TABLE IF NOT EXISTS `uploaded_data` (
  `ID` int(11) NOT NULL auto_increment,
  `usrID` int(11) NOT NULL,
  `folderName` varchar(128) default NULL,
  `ts` int(11) NOT NULL,
  `xml` text NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `usr_folder` (`usrID`,`folderName`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=39 ;
