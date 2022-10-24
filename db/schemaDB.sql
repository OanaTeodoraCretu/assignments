-- MySQL dump 10.13  Distrib 5.5.53, for osx10.8 (i386)
--
-- Host: localhost    Database: schedulesdb
-- ------------------------------------------------------
-- Server version	5.5.53-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE `schedulesdb`;
USE `schedulesdb`;

--
-- Table structure for table `exceptions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exceptions` (
  `exception_id` int(11) NOT NULL AUTO_INCREMENT,
  `for_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `new_start_time` time DEFAULT NULL,
  `new_end_time` time DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`exception_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exceptions`
--

LOCK TABLES `exceptions` WRITE;
/*!40000 ALTER TABLE `exceptions` DISABLE KEYS */;
INSERT INTO `exceptions` VALUES (4,'2024-03-19 22:00:00','10:00:00','10:15:00',0),(10,'2022-10-24 17:33:43','10:00:00','12:00:00',0),(11,'2023-10-21 00:06:02','10:00:00','11:15:00',1);
/*!40000 ALTER TABLE `exceptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `frequencyintervals`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frequencyintervals` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `frequencyintervals`
--

LOCK TABLES `frequencyintervals` WRITE;
/*!40000 ALTER TABLE `frequencyintervals` DISABLE KEYS */;
INSERT INTO `frequencyintervals` VALUES (1,'Sunday'),(2,'Monday'),(4,'Tuesday'),(8,'Wednesday'),(16,'Thursday'),(32,'Friday'),(64,'Saturday');
/*!40000 ALTER TABLE `frequencyintervals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `frequencytypes`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frequencytypes` (
  `frequencytype_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`frequencytype_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `frequencytypes`
--

LOCK TABLES `frequencytypes` WRITE;
/*!40000 ALTER TABLE `frequencytypes` DISABLE KEYS */;
INSERT INTO `frequencytypes` VALUES (1,'once','the event happens one time'),(2,'daily','the event happens every day'),(3,'weekly','the event happens one or more than one day pe'),(4,'monthly ','the event happens in a certain day of month'),(5,'yearly','the event happens in a certain day of year');
/*!40000 ALTER TABLE `frequencytypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offenders`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offenders` (
  `offender_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  PRIMARY KEY (`offender_id`),
  UNIQUE KEY `offender_id_UNIQUE` (`offender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offenders`
--

LOCK TABLES `offenders` WRITE;
/*!40000 ALTER TABLE `offenders` DISABLE KEYS */;
INSERT INTO `offenders` VALUES (1,'John','Doe');
/*!40000 ALTER TABLE `offenders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offenderszones`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offenderszones` (
  `offender_id` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offenderszones`
--

LOCK TABLES `offenderszones` WRITE;
/*!40000 ALTER TABLE `offenderszones` DISABLE KEYS */;
INSERT INTO `offenderszones` VALUES (1,1);
/*!40000 ALTER TABLE `offenderszones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedules`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schedules` (
  `schedule_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `name` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedules`
--

LOCK TABLES `schedules` WRITE;
/*!40000 ALTER TABLE `schedules` DISABLE KEYS */;
INSERT INTO `schedules` VALUES (29,'2024-03-19 22:00:00','2024-06-23 21:00:00','spring',1);
/*!40000 ALTER TABLE `schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduleszones`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduleszones` (
  `zone_id` bigint(20) DEFAULT NULL,
  `schedule_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduleszones`
--

LOCK TABLES `scheduleszones` WRITE;
/*!40000 ALTER TABLE `scheduleszones` DISABLE KEYS */;
INSERT INTO `scheduleszones` VALUES (1,29);
/*!40000 ALTER TABLE `scheduleszones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `series`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `series` (
  `series_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_hour` time NOT NULL,
  `end_hour` time NOT NULL,
  `freq_type_id` int(11) NOT NULL,
  `freq_interval_id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `exception_id` int(11) DEFAULT NULL,
  `repeat_interval_value` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`series_id`),
  KEY `freq_type_id_idx` (`freq_type_id`),
  KEY `series_schedule_FK_idx` (`schedule_id`),
  CONSTRAINT `series_frequencytypes_FK` FOREIGN KEY (`freq_type_id`) REFERENCES `frequencytypes` (`frequencytype_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `series`
--

LOCK TABLES `series` WRITE;
/*!40000 ALTER TABLE `series` DISABLE KEYS */;
INSERT INTO `series` VALUES (19,'10:00:00','11:00:00',2,10,29,4,1),(20,'11:00:00','12:00:00',3,10,29,10,1);
/*!40000 ALTER TABLE `series` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zones`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zones` (
  `zone_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`zone_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zones`
--

LOCK TABLES `zones` WRITE;
/*!40000 ALTER TABLE `zones` DISABLE KEYS */;
INSERT INTO `zones` VALUES (1,'center');
/*!40000 ALTER TABLE `zones` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-10-25  1:15:23
