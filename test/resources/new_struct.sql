-- MySQL dump 10.13  Distrib 5.7.13, for osx10.9 (x86_64)
--
-- Host: mac-29608.shire.nla.gov.au    Database: amberdb
-- ------------------------------------------------------
-- Server version	5.5.42

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

--
-- Table structure for table `alias`
--

DROP TABLE IF EXISTS `alias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alias` (
  `id` bigint(20) DEFAULT NULL,
  `alias` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cameradata`
--

DROP TABLE IF EXISTS `cameradata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cameradata` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `exposureTime` varchar(17) DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `whiteBalance` varchar(18) DEFAULT NULL,
  `standardId` text,
  `language` varchar(35) DEFAULT NULL,
  `lens` varchar(27) DEFAULT NULL,
  `title` text,
  `focalLenth` varchar(8) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `contributor` text,
  `isoSpeedRating` varchar(5) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `coverage` text,
  `bibId` varchar(9) DEFAULT NULL,
  `meteringMode` varchar(23) DEFAULT NULL,
  `creator` text,
  `coordinates` text,
  `fileSource` varchar(26) DEFAULT NULL,
  `otherTitle` text,
  `holdingNumber` text,
  `exposureProgram` varchar(19) DEFAULT NULL,
  `exposureFNumber` varchar(5) DEFAULT NULL,
  `publisher` text,
  `scaleEtc` text,
  `exposureMode` varchar(15) DEFAULT NULL,
  `focalLength` varchar(8) DEFAULT NULL,
  KEY `cameradata_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cameradata_history`
--

DROP TABLE IF EXISTS `cameradata_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cameradata_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `exposureTime` varchar(17) DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `whiteBalance` varchar(18) DEFAULT NULL,
  `standardId` text,
  `language` varchar(35) DEFAULT NULL,
  `lens` varchar(27) DEFAULT NULL,
  `title` text,
  `focalLenth` varchar(8) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `contributor` text,
  `isoSpeedRating` varchar(5) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `coverage` text,
  `bibId` varchar(9) DEFAULT NULL,
  `meteringMode` varchar(23) DEFAULT NULL,
  `creator` text,
  `coordinates` text,
  `fileSource` varchar(26) DEFAULT NULL,
  `otherTitle` text,
  `holdingNumber` text,
  `exposureProgram` varchar(19) DEFAULT NULL,
  `exposureFNumber` varchar(5) DEFAULT NULL,
  `publisher` text,
  `scaleEtc` text,
  `exposureMode` varchar(15) DEFAULT NULL,
  `focalLength` varchar(8) DEFAULT NULL,
  KEY `cameradata_history_id` (`id`),
  KEY `cameradata_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carrier_algorithm`
--

DROP TABLE IF EXISTS `carrier_algorithm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carrier_algorithm` (
  `linkId` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `carrierId` bigint(11) DEFAULT NULL,
  `algorithmId` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`linkId`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contributor`
--

DROP TABLE IF EXISTS `contributor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contributor` (
  `id` bigint(20) DEFAULT NULL,
  `value` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `copy`
--

DROP TABLE IF EXISTS `copy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `copy` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `extent` text,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `standardId` text,
  `language` varchar(35) DEFAULT NULL,
  `title` text,
  `holdingId` varchar(7) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `dateCreated` datetime DEFAULT NULL,
  `contributor` text,
  `timedStatus` varchar(9) DEFAULT NULL,
  `copyType` varchar(9) DEFAULT NULL,
  `alias` text,
  `copyStatus` varchar(4) DEFAULT NULL,
  `copyRole` varchar(3) DEFAULT NULL,
  `manipulation` varchar(44) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `algorithm` varchar(8) DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `creator` text,
  `otherNumbers` varchar(2) DEFAULT NULL,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `materialType` varchar(12) DEFAULT NULL,
  `commentsExternal` text,
  `coordinates` text,
  `creatorStatement` text,
  `classification` text,
  `currentVersion` varchar(3) DEFAULT NULL,
  `commentsInternal` text,
  `bestCopy` varchar(1) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `holdingNumber` text,
  `series` text,
  `publisher` text,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  `dcmCopyPid` varchar(37) DEFAULT NULL,
  KEY `copy_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `copy_desc`
--

DROP TABLE IF EXISTS `copy_desc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `copy_desc` (
  `id` bigint(20) NOT NULL,
  `copy_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `exposure_program` text,
  `exposure_time` text,
  `focal_lenth` text,
  `iso_speed_rating` text,
  `lens` text,
  `metering_mode` text,
  `type` text,
  `exposure_mode` text,
  `white_balance` text,
  `exposure_fnumber` text,
  `file_source` text,
  `focal_length` text,
  `australian_content` bigint(20) DEFAULT NULL,
  `bib_id` text,
  `coordinates` text,
  `coverage` text,
  `encoding_level` text,
  `extent` text,
  `holding_id` text,
  `holding_number` text,
  `language` text,
  `local_system_number` text,
  `other_title` text,
  `publisher` text,
  `record_source` text,
  `scale_etc` text,
  `standard_id` text,
  `title` text,
  `contributor` text,
  `creator` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `copy_history`
--

DROP TABLE IF EXISTS `copy_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `copy_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `extent` text,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `standardId` text,
  `language` varchar(35) DEFAULT NULL,
  `title` text,
  `holdingId` varchar(7) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `dateCreated` datetime DEFAULT NULL,
  `contributor` text,
  `timedStatus` varchar(9) DEFAULT NULL,
  `copyType` varchar(9) DEFAULT NULL,
  `alias` text,
  `copyStatus` varchar(4) DEFAULT NULL,
  `copyRole` varchar(3) DEFAULT NULL,
  `manipulation` varchar(44) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `algorithm` varchar(8) DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `creator` text,
  `otherNumbers` varchar(2) DEFAULT NULL,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `materialType` varchar(12) DEFAULT NULL,
  `commentsExternal` text,
  `coordinates` text,
  `creatorStatement` text,
  `classification` text,
  `currentVersion` varchar(3) DEFAULT NULL,
  `commentsInternal` text,
  `bestCopy` varchar(1) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `holdingNumber` text,
  `series` text,
  `publisher` text,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  `dcmCopyPid` varchar(37) DEFAULT NULL,
  KEY `copy_history_id` (`id`),
  KEY `copy_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dup_edge_to_be_removed`
--

DROP TABLE IF EXISTS `dup_edge_to_be_removed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dup_edge_to_be_removed` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `v_out` bigint(20) DEFAULT NULL,
  `v_in` bigint(20) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `edge_order` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dup_property_to_be_removed`
--

DROP TABLE IF EXISTS `dup_property_to_be_removed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dup_property_to_be_removed` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` char(3) DEFAULT NULL,
  `value` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dup_vertex_to_be_removed`
--

DROP TABLE IF EXISTS `dup_vertex_to_be_removed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dup_vertex_to_be_removed` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `durations`
--

DROP TABLE IF EXISTS `durations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `durations` (
  `id` bigint(20) DEFAULT NULL,
  `copy_id` bigint(20) DEFAULT NULL,
  `role` char(5) DEFAULT NULL,
  `duration_secs` bigint(20) DEFAULT NULL,
  `carrier` varchar(200) DEFAULT NULL
) ENGINE=MEMORY DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `eadfeature`
--

DROP TABLE IF EXISTS `eadfeature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eadfeature` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `records` text,
  `featureType` varchar(15) DEFAULT NULL,
  `fields` varchar(19) DEFAULT NULL,
  `featureId` varchar(39) DEFAULT NULL,
  KEY `eadfeature_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `eadfeature_history`
--

DROP TABLE IF EXISTS `eadfeature_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eadfeature_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `records` text,
  `featureType` varchar(15) DEFAULT NULL,
  `fields` varchar(19) DEFAULT NULL,
  `featureId` varchar(39) DEFAULT NULL,
  KEY `eadfeature_history_id` (`id`),
  KEY `eadfeature_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `eadwork`
--

DROP TABLE IF EXISTS `eadwork`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eadwork` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `occupation` text,
  `materialFromMultipleSources` tinyint(1) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `displayTitlePage` tinyint(1) DEFAULT NULL,
  `subject` text,
  `sendToIlms` tinyint(1) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `sensitiveMaterial` varchar(3) DEFAULT NULL,
  `repository` varchar(30) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `arrangement` text,
  `dcmAltPi` varchar(52) DEFAULT NULL,
  `folderNumber` varchar(50) DEFAULT NULL,
  `collectionNumber` varchar(49) DEFAULT NULL,
  `west` varchar(1) DEFAULT NULL,
  `totalDuration` varchar(10) DEFAULT NULL,
  `workCreatedDuringMigration` tinyint(1) DEFAULT NULL,
  `relatedMaterial` text,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `findingAidNote` text,
  `collection` varchar(7) DEFAULT NULL,
  `dcmWorkPid` varchar(31) DEFAULT NULL,
  `otherTitle` text,
  `classification` text,
  `commentsInternal` text,
  `immutable` varchar(12) DEFAULT NULL,
  `folder` text,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `nextStep` varchar(4) DEFAULT NULL,
  `publisher` text,
  `subType` varchar(7) DEFAULT NULL,
  `copyingPublishing` text,
  `scaleEtc` text,
  `startDate` datetime DEFAULT NULL,
  `tempHolding` varchar(2) DEFAULT NULL,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `access` text,
  `allowHighResdownload` tinyint(1) DEFAULT NULL,
  `south` varchar(1) DEFAULT NULL,
  `isMissingPage` tinyint(1) DEFAULT NULL,
  `restrictionsOnAccess` text,
  `north` varchar(1) DEFAULT NULL,
  `scopeContent` text,
  `representativeId` varchar(26) DEFAULT NULL,
  `standardId` text,
  `accessConditions` varchar(13) DEFAULT NULL,
  `title` text,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `eadUpdateReviewRequired` varchar(1) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `digitalStatusDate` datetime DEFAULT NULL,
  `east` varchar(1) DEFAULT NULL,
  `bibliography` text,
  `contributor` text,
  `provenance` text,
  `moreIlmsDetailsRequired` tinyint(1) DEFAULT NULL,
  `subUnitType` varchar(23) DEFAULT NULL,
  `rights` text,
  `uniformTitle` text,
  `rdsAcknowledgementType` varchar(7) DEFAULT NULL,
  `alias` text,
  `recordSource` varchar(8) DEFAULT NULL,
  `dateRangeInAS` varchar(9) DEFAULT NULL,
  `coverage` text,
  `bibId` varchar(9) DEFAULT NULL,
  `summary` text,
  `creator` text,
  `preferredCitation` text,
  `coordinates` text,
  `creatorStatement` text,
  `folderType` varchar(31) DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `holdingNumber` text,
  `form` varchar(19) DEFAULT NULL,
  `series` text,
  `rdsAcknowledgementReceiver` text,
  `constraint1` text,
  `digitalStatus` varchar(18) DEFAULT NULL,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  KEY `eadwork_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `eadwork_history`
--

DROP TABLE IF EXISTS `eadwork_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eadwork_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `occupation` text,
  `materialFromMultipleSources` tinyint(1) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `displayTitlePage` tinyint(1) DEFAULT NULL,
  `subject` text,
  `sendToIlms` tinyint(1) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `sensitiveMaterial` varchar(3) DEFAULT NULL,
  `repository` varchar(30) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `arrangement` text,
  `dcmAltPi` varchar(52) DEFAULT NULL,
  `folderNumber` varchar(50) DEFAULT NULL,
  `collectionNumber` varchar(49) DEFAULT NULL,
  `west` varchar(1) DEFAULT NULL,
  `totalDuration` varchar(10) DEFAULT NULL,
  `workCreatedDuringMigration` tinyint(1) DEFAULT NULL,
  `relatedMaterial` text,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `findingAidNote` text,
  `collection` varchar(7) DEFAULT NULL,
  `dcmWorkPid` varchar(31) DEFAULT NULL,
  `otherTitle` text,
  `classification` text,
  `commentsInternal` text,
  `immutable` varchar(12) DEFAULT NULL,
  `folder` text,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `nextStep` varchar(4) DEFAULT NULL,
  `publisher` text,
  `subType` varchar(7) DEFAULT NULL,
  `copyingPublishing` text,
  `scaleEtc` text,
  `startDate` datetime DEFAULT NULL,
  `tempHolding` varchar(2) DEFAULT NULL,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `access` text,
  `allowHighResdownload` tinyint(1) DEFAULT NULL,
  `south` varchar(1) DEFAULT NULL,
  `isMissingPage` tinyint(1) DEFAULT NULL,
  `restrictionsOnAccess` text,
  `north` varchar(1) DEFAULT NULL,
  `scopeContent` text,
  `representativeId` varchar(26) DEFAULT NULL,
  `standardId` text,
  `accessConditions` varchar(13) DEFAULT NULL,
  `title` text,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `eadUpdateReviewRequired` varchar(1) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `digitalStatusDate` datetime DEFAULT NULL,
  `east` varchar(1) DEFAULT NULL,
  `bibliography` text,
  `contributor` text,
  `provenance` text,
  `moreIlmsDetailsRequired` tinyint(1) DEFAULT NULL,
  `subUnitType` varchar(23) DEFAULT NULL,
  `rights` text,
  `uniformTitle` text,
  `rdsAcknowledgementType` varchar(7) DEFAULT NULL,
  `alias` text,
  `recordSource` varchar(8) DEFAULT NULL,
  `dateRangeInAS` varchar(9) DEFAULT NULL,
  `coverage` text,
  `bibId` varchar(9) DEFAULT NULL,
  `summary` text,
  `creator` text,
  `preferredCitation` text,
  `coordinates` text,
  `creatorStatement` text,
  `folderType` varchar(31) DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `holdingNumber` text,
  `form` varchar(19) DEFAULT NULL,
  `series` text,
  `rdsAcknowledgementReceiver` text,
  `constraint1` text,
  `digitalStatus` varchar(18) DEFAULT NULL,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  KEY `eadwork_history_id` (`id`),
  KEY `eadwork_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edge`
--

DROP TABLE IF EXISTS `edge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `edge` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `v_out` bigint(20) DEFAULT NULL,
  `v_in` bigint(20) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `edge_order` bigint(20) DEFAULT NULL,
  UNIQUE KEY `unique_edge` (`id`,`txn_start`),
  KEY `edge_in_idx` (`v_in`),
  KEY `edge_out_idx` (`v_out`),
  KEY `edge_txn_end_idx` (`txn_end`),
  KEY `edge_label_idx` (`label`),
  KEY `edge_in_traversal_idx` (`txn_end`,`v_in`,`label`,`edge_order`,`v_out`),
  KEY `edge_out_traversal_idx` (`txn_end`,`v_out`,`label`,`edge_order`,`v_in`),
  KEY `edge_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exists_on_history`
--

DROP TABLE IF EXISTS `exists_on_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exists_on_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `v_out` bigint(20) DEFAULT NULL,
  `v_in` bigint(20) DEFAULT NULL,
  `edge_order` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `fileName` text,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `software` varchar(33) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `standardId` text,
  `language` varchar(35) DEFAULT NULL,
  `mimeType` text,
  `title` text,
  `holdingId` varchar(7) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `contributor` text,
  `checksum` varchar(40) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `coverage` text,
  `bibId` varchar(9) DEFAULT NULL,
  `creator` text,
  `checksumGenerationDate` datetime DEFAULT NULL,
  `coordinates` text,
  `encoding` varchar(10) DEFAULT NULL,
  `holdingNumber` text,
  `fileSize` bigint(20) DEFAULT NULL,
  `blobId` bigint(20) DEFAULT NULL,
  `checksumType` varchar(4) DEFAULT NULL,
  `publisher` text,
  `compression` varchar(9) DEFAULT NULL,
  `device` varchar(44) DEFAULT NULL,
  `fileFormat` varchar(20) DEFAULT NULL,
  KEY `file_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_history`
--

DROP TABLE IF EXISTS `file_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `fileName` text,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `software` varchar(33) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `standardId` text,
  `language` varchar(35) DEFAULT NULL,
  `mimeType` text,
  `title` text,
  `holdingId` varchar(7) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `contributor` text,
  `checksum` varchar(40) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `coverage` text,
  `bibId` varchar(9) DEFAULT NULL,
  `creator` text,
  `checksumGenerationDate` datetime DEFAULT NULL,
  `coordinates` text,
  `encoding` varchar(10) DEFAULT NULL,
  `holdingNumber` text,
  `fileSize` bigint(20) DEFAULT NULL,
  `blobId` bigint(20) DEFAULT NULL,
  `checksumType` varchar(4) DEFAULT NULL,
  `publisher` text,
  `compression` varchar(9) DEFAULT NULL,
  `device` varchar(44) DEFAULT NULL,
  `fileFormat` varchar(20) DEFAULT NULL,
  KEY `file_history_id` (`id`),
  KEY `file_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geocoding`
--

DROP TABLE IF EXISTS `geocoding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geocoding` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `mapDatum` varchar(6) DEFAULT NULL,
  `latitude` varchar(33) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `longitude` varchar(31) DEFAULT NULL,
  KEY `geocoding_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `geocoding_history`
--

DROP TABLE IF EXISTS `geocoding_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `geocoding_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `mapDatum` varchar(6) DEFAULT NULL,
  `latitude` varchar(33) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `longitude` varchar(31) DEFAULT NULL,
  KEY `geocoding_history_id` (`id`),
  KEY `geocoding_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `id_generator`
--

DROP TABLE IF EXISTS `id_generator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `id_generator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28981277 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `imagefile`
--

DROP TABLE IF EXISTS `imagefile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `imagefile` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `fileName` text,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `software` varchar(33) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `mimeType` text,
  `resolution` varchar(37) DEFAULT NULL,
  `manufacturerSerialNumber` varchar(12) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `resolutionUnit` varchar(4) DEFAULT NULL,
  `imageWidth` int(11) DEFAULT NULL,
  `manufacturerMake` varchar(27) DEFAULT NULL,
  `manufacturerModelName` varchar(41) DEFAULT NULL,
  `encoding` varchar(10) DEFAULT NULL,
  `deviceSerialNumber` varchar(20) DEFAULT NULL,
  `fileSize` bigint(20) DEFAULT NULL,
  `bitDepth` varchar(8) DEFAULT NULL,
  `publisher` text,
  `compression` varchar(9) DEFAULT NULL,
  `device` varchar(44) DEFAULT NULL,
  `imageLength` int(11) DEFAULT NULL,
  `colourSpace` varchar(15) DEFAULT NULL,
  `standardId` text,
  `title` text,
  `australianContent` tinyint(1) DEFAULT NULL,
  `contributor` text,
  `checksum` varchar(40) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `coverage` text,
  `orientation` varchar(36) DEFAULT NULL,
  `creator` text,
  `colourProfile` varchar(9) DEFAULT NULL,
  `checksumGenerationDate` datetime DEFAULT NULL,
  `applicationDateCreated` varchar(19) DEFAULT NULL,
  `coordinates` text,
  `creatorStatement` text,
  `fileFormatVersion` varchar(3) DEFAULT NULL,
  `dateDigitised` varchar(19) DEFAULT NULL,
  `holdingNumber` text,
  `application` text,
  `series` text,
  `blobId` bigint(20) DEFAULT NULL,
  `softwareSerialNumber` varchar(10) DEFAULT NULL,
  `checksumType` varchar(4) DEFAULT NULL,
  `location` text,
  `fileFormat` varchar(20) DEFAULT NULL,
  KEY `imagefile_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `imagefile_history`
--

DROP TABLE IF EXISTS `imagefile_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `imagefile_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `fileName` text,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `software` varchar(33) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `mimeType` text,
  `resolution` varchar(37) DEFAULT NULL,
  `manufacturerSerialNumber` varchar(12) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `resolutionUnit` varchar(4) DEFAULT NULL,
  `imageWidth` int(11) DEFAULT NULL,
  `manufacturerMake` varchar(27) DEFAULT NULL,
  `manufacturerModelName` varchar(41) DEFAULT NULL,
  `encoding` varchar(10) DEFAULT NULL,
  `deviceSerialNumber` varchar(20) DEFAULT NULL,
  `fileSize` bigint(20) DEFAULT NULL,
  `bitDepth` varchar(8) DEFAULT NULL,
  `publisher` text,
  `compression` varchar(9) DEFAULT NULL,
  `device` varchar(44) DEFAULT NULL,
  `imageLength` int(11) DEFAULT NULL,
  `colourSpace` varchar(15) DEFAULT NULL,
  `standardId` text,
  `title` text,
  `australianContent` tinyint(1) DEFAULT NULL,
  `contributor` text,
  `checksum` varchar(40) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `coverage` text,
  `orientation` varchar(36) DEFAULT NULL,
  `creator` text,
  `colourProfile` varchar(9) DEFAULT NULL,
  `checksumGenerationDate` datetime DEFAULT NULL,
  `applicationDateCreated` varchar(19) DEFAULT NULL,
  `coordinates` text,
  `creatorStatement` text,
  `fileFormatVersion` varchar(3) DEFAULT NULL,
  `dateDigitised` varchar(19) DEFAULT NULL,
  `holdingNumber` text,
  `application` text,
  `series` text,
  `blobId` bigint(20) DEFAULT NULL,
  `softwareSerialNumber` varchar(10) DEFAULT NULL,
  `checksumType` varchar(4) DEFAULT NULL,
  `location` text,
  `fileFormat` varchar(20) DEFAULT NULL,
  KEY `imagefile_history_id` (`id`),
  KEY `imagefile_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `indexed_txns`
--

DROP TABLE IF EXISTS `indexed_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `indexed_txns` (
  `txn` bigint(11) DEFAULT NULL,
  KEY `indexed_txns_idx` (`txn`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iptc`
--

DROP TABLE IF EXISTS `iptc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iptc` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `province` varchar(3) DEFAULT NULL,
  `city` varchar(23) DEFAULT NULL,
  KEY `iptc_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iptc_history`
--

DROP TABLE IF EXISTS `iptc_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iptc_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `province` varchar(3) DEFAULT NULL,
  `city` varchar(23) DEFAULT NULL,
  KEY `iptc_history_id` (`id`),
  KEY `iptc_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `is_part_of_history`
--

DROP TABLE IF EXISTS `is_part_of_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `is_part_of_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `v_out` bigint(20) DEFAULT NULL,
  `v_in` bigint(20) DEFAULT NULL,
  `edge_order` bigint(20) DEFAULT NULL,
  KEY `is_part_of_history_start_end` (`txn_start`,`txn_end`),
  KEY `is_part_of_history_id_start_end` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `list`
--

DROP TABLE IF EXISTS `list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `list` (
  `name` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `deleted` varchar(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lookups`
--

DROP TABLE IF EXISTS `lookups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lookups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `value` varchar(2000) DEFAULT NULL,
  `deleted` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `lookups_id_idx` (`id`,`deleted`),
  KEY `lookups_name_idx` (`name`,`deleted`),
  KEY `lookups_name_code_idx` (`name`,`code`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=1303 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lookups_bak`
--

DROP TABLE IF EXISTS `lookups_bak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lookups_bak` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `deleted` varchar(1) DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lookups_pre_release`
--

DROP TABLE IF EXISTS `lookups_pre_release`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lookups_pre_release` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `deleted` varchar(1) DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `page`
--

DROP TABLE IF EXISTS `page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `page` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `extent` text,
  `notes` varchar(30) DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `occupation` text,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `materialFromMultipleSources` tinyint(1) DEFAULT NULL,
  `displayTitlePage` tinyint(1) DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `subject` text,
  `sendToIlms` tinyint(1) DEFAULT NULL,
  `vendorId` varchar(7) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `sensitiveMaterial` varchar(3) DEFAULT NULL,
  `repository` varchar(30) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `dcmAltPi` varchar(52) DEFAULT NULL,
  `west` varchar(1) DEFAULT NULL,
  `workCreatedDuringMigration` tinyint(1) DEFAULT NULL,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `commentsExternal` text,
  `firstPart` varchar(27) DEFAULT NULL,
  `findingAidNote` text,
  `collection` varchar(7) DEFAULT NULL,
  `dcmWorkPid` varchar(31) DEFAULT NULL,
  `otherTitle` text,
  `classification` text,
  `localSystemno` varchar(7) DEFAULT NULL,
  `commentsInternal` text,
  `acquisitionStatus` varchar(7) DEFAULT NULL,
  `immutable` varchar(12) DEFAULT NULL,
  `restrictionType` varchar(0) DEFAULT NULL,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `ilmsSentDateTime` datetime DEFAULT NULL,
  `publisher` text,
  `nextStep` varchar(4) DEFAULT NULL,
  `subType` varchar(7) DEFAULT NULL,
  `scaleEtc` text,
  `startDate` datetime DEFAULT NULL,
  `tempHolding` varchar(2) DEFAULT NULL,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `tilePosition` varchar(2) DEFAULT NULL,
  `sortIndex` varchar(28) DEFAULT NULL,
  `allowHighResdownload` tinyint(1) DEFAULT NULL,
  `south` varchar(1) DEFAULT NULL,
  `restrictionsOnAccess` text,
  `isMissingPage` tinyint(1) DEFAULT NULL,
  `north` varchar(1) DEFAULT NULL,
  `standardId` text,
  `representativeId` varchar(26) DEFAULT NULL,
  `scopeContent` text,
  `accessConditions` varchar(13) DEFAULT NULL,
  `edition` text,
  `alternativeTitle` varchar(20) DEFAULT NULL,
  `title` text,
  `acquisitionCategory` varchar(19) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `eadUpdateReviewRequired` varchar(1) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `digitalStatusDate` datetime DEFAULT NULL,
  `east` varchar(1) DEFAULT NULL,
  `contributor` text,
  `moreIlmsDetailsRequired` tinyint(1) DEFAULT NULL,
  `subUnitType` varchar(23) DEFAULT NULL,
  `uniformTitle` text,
  `rights` text,
  `alias` text,
  `rdsAcknowledgementType` varchar(7) DEFAULT NULL,
  `issueDate` datetime DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `coverage` text,
  `summary` text,
  `creator` text,
  `sensitiveReason` varchar(42) DEFAULT NULL,
  `coordinates` text,
  `creatorStatement` text,
  `interactiveIndexAvailable` tinyint(1) DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `holdingNumber` text,
  `form` varchar(19) DEFAULT NULL,
  `series` text,
  `rdsAcknowledgementReceiver` text,
  `constraint1` text,
  `digitalStatus` varchar(18) DEFAULT NULL,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  `depositType` text,
  `parentConstraint` varchar(35) DEFAULT NULL,
  KEY `page_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `page_history`
--

DROP TABLE IF EXISTS `page_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `page_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `extent` text,
  `notes` varchar(30) DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `occupation` text,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `materialFromMultipleSources` tinyint(1) DEFAULT NULL,
  `displayTitlePage` tinyint(1) DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `subject` text,
  `sendToIlms` tinyint(1) DEFAULT NULL,
  `vendorId` varchar(7) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `sensitiveMaterial` varchar(3) DEFAULT NULL,
  `repository` varchar(30) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `dcmAltPi` varchar(52) DEFAULT NULL,
  `west` varchar(1) DEFAULT NULL,
  `workCreatedDuringMigration` tinyint(1) DEFAULT NULL,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `commentsExternal` text,
  `firstPart` varchar(27) DEFAULT NULL,
  `findingAidNote` text,
  `collection` varchar(7) DEFAULT NULL,
  `dcmWorkPid` varchar(31) DEFAULT NULL,
  `otherTitle` text,
  `classification` text,
  `localSystemno` varchar(7) DEFAULT NULL,
  `commentsInternal` text,
  `acquisitionStatus` varchar(7) DEFAULT NULL,
  `immutable` varchar(12) DEFAULT NULL,
  `restrictionType` varchar(0) DEFAULT NULL,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `ilmsSentDateTime` datetime DEFAULT NULL,
  `publisher` text,
  `nextStep` varchar(4) DEFAULT NULL,
  `subType` varchar(7) DEFAULT NULL,
  `scaleEtc` text,
  `startDate` datetime DEFAULT NULL,
  `tempHolding` varchar(2) DEFAULT NULL,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `tilePosition` varchar(2) DEFAULT NULL,
  `sortIndex` varchar(28) DEFAULT NULL,
  `allowHighResdownload` tinyint(1) DEFAULT NULL,
  `south` varchar(1) DEFAULT NULL,
  `restrictionsOnAccess` text,
  `isMissingPage` tinyint(1) DEFAULT NULL,
  `north` varchar(1) DEFAULT NULL,
  `standardId` text,
  `representativeId` varchar(26) DEFAULT NULL,
  `scopeContent` text,
  `accessConditions` varchar(13) DEFAULT NULL,
  `edition` text,
  `alternativeTitle` varchar(20) DEFAULT NULL,
  `title` text,
  `acquisitionCategory` varchar(19) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `eadUpdateReviewRequired` varchar(1) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `digitalStatusDate` datetime DEFAULT NULL,
  `east` varchar(1) DEFAULT NULL,
  `contributor` text,
  `moreIlmsDetailsRequired` tinyint(1) DEFAULT NULL,
  `subUnitType` varchar(23) DEFAULT NULL,
  `uniformTitle` text,
  `rights` text,
  `alias` text,
  `rdsAcknowledgementType` varchar(7) DEFAULT NULL,
  `issueDate` datetime DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `coverage` text,
  `summary` text,
  `creator` text,
  `sensitiveReason` varchar(42) DEFAULT NULL,
  `coordinates` text,
  `creatorStatement` text,
  `interactiveIndexAvailable` tinyint(1) DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `holdingNumber` text,
  `form` varchar(19) DEFAULT NULL,
  `series` text,
  `rdsAcknowledgementReceiver` text,
  `constraint1` text,
  `digitalStatus` varchar(18) DEFAULT NULL,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  `depositType` text,
  `parentConstraint` varchar(35) DEFAULT NULL,
  KEY `page_history_id` (`id`),
  KEY `page_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `page_most_recent`
--

DROP TABLE IF EXISTS `page_most_recent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `page_most_recent` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `party`
--

DROP TABLE IF EXISTS `party`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `party` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(47) DEFAULT NULL,
  `suppressed` tinyint(1) DEFAULT NULL,
  `orgUrl` text,
  `logoUrl` varchar(17) DEFAULT NULL,
  KEY `party_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `party_history`
--

DROP TABLE IF EXISTS `party_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `party_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(47) DEFAULT NULL,
  `suppressed` tinyint(1) DEFAULT NULL,
  `orgUrl` text,
  `logoUrl` varchar(17) DEFAULT NULL,
  KEY `party_history_id` (`id`),
  KEY `party_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property`
--

DROP TABLE IF EXISTS `property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` char(3) DEFAULT NULL,
  `value` blob,
  UNIQUE KEY `unique_prop` (`id`,`txn_start`,`name`),
  KEY `property_txn_end_idx` (`txn_end`),
  KEY `property_name` (`name`),
  KEY `property_value_idx` (`value`(512)),
  KEY `name_val_idx` (`name`,`value`(512)),
  KEY `p_id_index` (`id`),
  KEY `p_id_name_index` (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_cameradata_ids`
--

DROP TABLE IF EXISTS `property_all_cameradata_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_cameradata_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_cameradata_txns`
--

DROP TABLE IF EXISTS `property_all_cameradata_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_cameradata_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_copy_ids`
--

DROP TABLE IF EXISTS `property_all_copy_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_copy_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_copy_txns`
--

DROP TABLE IF EXISTS `property_all_copy_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_copy_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_eadfeature_ids`
--

DROP TABLE IF EXISTS `property_all_eadfeature_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_eadfeature_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_eadfeature_txns`
--

DROP TABLE IF EXISTS `property_all_eadfeature_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_eadfeature_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_eadwork_ids`
--

DROP TABLE IF EXISTS `property_all_eadwork_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_eadwork_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_eadwork_txns`
--

DROP TABLE IF EXISTS `property_all_eadwork_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_eadwork_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_file_ids`
--

DROP TABLE IF EXISTS `property_all_file_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_file_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_file_txns`
--

DROP TABLE IF EXISTS `property_all_file_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_file_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_geocoding_ids`
--

DROP TABLE IF EXISTS `property_all_geocoding_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_geocoding_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_geocoding_txns`
--

DROP TABLE IF EXISTS `property_all_geocoding_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_geocoding_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_imagefile_ids`
--

DROP TABLE IF EXISTS `property_all_imagefile_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_imagefile_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_imagefile_txns`
--

DROP TABLE IF EXISTS `property_all_imagefile_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_imagefile_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_iptc_ids`
--

DROP TABLE IF EXISTS `property_all_iptc_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_iptc_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_iptc_txns`
--

DROP TABLE IF EXISTS `property_all_iptc_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_iptc_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_page_ids`
--

DROP TABLE IF EXISTS `property_all_page_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_page_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_page_txns`
--

DROP TABLE IF EXISTS `property_all_page_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_page_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_party_ids`
--

DROP TABLE IF EXISTS `property_all_party_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_party_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_party_txns`
--

DROP TABLE IF EXISTS `property_all_party_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_party_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_section_ids`
--

DROP TABLE IF EXISTS `property_all_section_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_section_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_section_txns`
--

DROP TABLE IF EXISTS `property_all_section_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_section_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_soundfile_ids`
--

DROP TABLE IF EXISTS `property_all_soundfile_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_soundfile_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_soundfile_txns`
--

DROP TABLE IF EXISTS `property_all_soundfile_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_soundfile_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_tag_ids`
--

DROP TABLE IF EXISTS `property_all_tag_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_tag_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_tag_txns`
--

DROP TABLE IF EXISTS `property_all_tag_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_tag_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_work`
--

DROP TABLE IF EXISTS `property_all_work`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_work` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_work_ids`
--

DROP TABLE IF EXISTS `property_all_work_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_work_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_all_work_txns`
--

DROP TABLE IF EXISTS `property_all_work_txns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_all_work_txns` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current`
--

DROP TABLE IF EXISTS `property_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `pc_id_index` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_cameradata`
--

DROP TABLE IF EXISTS `property_current_cameradata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_cameradata` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_cameradata_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_cameradata_ids`
--

DROP TABLE IF EXISTS `property_current_cameradata_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_cameradata_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_copy`
--

DROP TABLE IF EXISTS `property_current_copy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_copy` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_copy_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_copy_ids`
--

DROP TABLE IF EXISTS `property_current_copy_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_copy_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_eadfeature`
--

DROP TABLE IF EXISTS `property_current_eadfeature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_eadfeature` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_eadfeature_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_eadfeature_ids`
--

DROP TABLE IF EXISTS `property_current_eadfeature_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_eadfeature_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_eadwork`
--

DROP TABLE IF EXISTS `property_current_eadwork`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_eadwork` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_eadwork_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_eadwork_ids`
--

DROP TABLE IF EXISTS `property_current_eadwork_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_eadwork_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_file`
--

DROP TABLE IF EXISTS `property_current_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_file` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_file_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_file_ids`
--

DROP TABLE IF EXISTS `property_current_file_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_file_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_geocoding`
--

DROP TABLE IF EXISTS `property_current_geocoding`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_geocoding` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_geocoding_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_geocoding_ids`
--

DROP TABLE IF EXISTS `property_current_geocoding_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_geocoding_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_imageFile_ids`
--

DROP TABLE IF EXISTS `property_current_imageFile_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_imageFile_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_imagefile`
--

DROP TABLE IF EXISTS `property_current_imagefile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_imagefile` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_imagefile_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_iptc`
--

DROP TABLE IF EXISTS `property_current_iptc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_iptc` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_iptc_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_iptc_ids`
--

DROP TABLE IF EXISTS `property_current_iptc_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_iptc_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_page`
--

DROP TABLE IF EXISTS `property_current_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_page` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_page_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_page_ids`
--

DROP TABLE IF EXISTS `property_current_page_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_page_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_party`
--

DROP TABLE IF EXISTS `property_current_party`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_party` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_party_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_party_ids`
--

DROP TABLE IF EXISTS `property_current_party_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_party_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_section`
--

DROP TABLE IF EXISTS `property_current_section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_section` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_section_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_section_ids`
--

DROP TABLE IF EXISTS `property_current_section_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_section_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_soundfile`
--

DROP TABLE IF EXISTS `property_current_soundfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_soundfile` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_soundfile_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_soundfile_ids`
--

DROP TABLE IF EXISTS `property_current_soundfile_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_soundfile_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_tag`
--

DROP TABLE IF EXISTS `property_current_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_tag` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_tag_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_tag_ids`
--

DROP TABLE IF EXISTS `property_current_tag_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_tag_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_work`
--

DROP TABLE IF EXISTS `property_current_work`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_work` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `type` char(3) CHARACTER SET latin1 DEFAULT NULL,
  `value` blob,
  KEY `property_current_work_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `property_current_work_ids`
--

DROP TABLE IF EXISTS `property_current_work_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_current_work_ids` (
  `id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `restrictions_on_access`
--

DROP TABLE IF EXISTS `restrictions_on_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `restrictions_on_access` (
  `id` bigint(20) DEFAULT NULL,
  `value` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `section`
--

DROP TABLE IF EXISTS `section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `section` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `creator` text,
  `accessConditions` varchar(13) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `abstract` text,
  `advertising` tinyint(1) DEFAULT NULL,
  `title` text,
  `printedPageNumber` varchar(14) DEFAULT NULL,
  `captions` varchar(255) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `illustrated` tinyint(1) DEFAULT NULL,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `metsId` varchar(8) DEFAULT NULL,
  `subType` varchar(7) DEFAULT NULL,
  `constraint1` text,
  KEY `section_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `section_history`
--

DROP TABLE IF EXISTS `section_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `section_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `creator` text,
  `accessConditions` varchar(13) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `abstract` text,
  `advertising` tinyint(1) DEFAULT NULL,
  `title` text,
  `printedPageNumber` varchar(14) DEFAULT NULL,
  `captions` varchar(255) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `illustrated` tinyint(1) DEFAULT NULL,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `metsId` varchar(8) DEFAULT NULL,
  `subType` varchar(7) DEFAULT NULL,
  `constraint1` text,
  KEY `section_history_id` (`id`),
  KEY `section_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `series`
--

DROP TABLE IF EXISTS `series`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `series` (
  `id` bigint(20) DEFAULT NULL,
  `value` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sess_edge`
--

DROP TABLE IF EXISTS `sess_edge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sess_edge` (
  `s_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `v_out` bigint(20) DEFAULT NULL,
  `v_in` bigint(20) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `edge_order` bigint(20) DEFAULT NULL,
  `state` char(3) DEFAULT NULL,
  KEY `sess_edge_idx` (`s_id`),
  KEY `sess_edge_sis_idx` (`s_id`,`id`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sess_property`
--

DROP TABLE IF EXISTS `sess_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sess_property` (
  `s_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` char(3) DEFAULT NULL,
  `value` blob,
  KEY `sess_property_s_id` (`s_id`),
  KEY `sess_property_id_idx` (`id`),
  KEY `sess_property_sis_idx` (`s_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sess_vertex`
--

DROP TABLE IF EXISTS `sess_vertex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sess_vertex` (
  `s_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `state` char(3) DEFAULT NULL,
  KEY `sess_vertex_idx` (`s_id`),
  KEY `sess_vertex_sis_idx` (`s_id`,`id`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `soundfile`
--

DROP TABLE IF EXISTS `soundfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soundfile` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `fileName` text,
  `software` varchar(33) DEFAULT NULL,
  `thickness` varchar(11) DEFAULT NULL,
  `channel` varchar(3) DEFAULT NULL,
  `bitrate` varchar(3) DEFAULT NULL,
  `mimeType` text,
  `durationType` varchar(7) DEFAULT NULL,
  `speed` varchar(10) DEFAULT NULL,
  `duration` varchar(12) DEFAULT NULL,
  `toolId` varchar(13) DEFAULT NULL,
  `checksum` varchar(40) DEFAULT NULL,
  `soundField` varchar(9) DEFAULT NULL,
  `fileContainer` varchar(3) DEFAULT NULL,
  `brand` varchar(27) DEFAULT NULL,
  `surface` varchar(20) DEFAULT NULL,
  `equalisation` varchar(4) DEFAULT NULL,
  `encoding` varchar(10) DEFAULT NULL,
  `codec` varchar(4) DEFAULT NULL,
  `fileSize` bigint(20) DEFAULT NULL,
  `reelSize` varchar(12) DEFAULT NULL,
  `carrierCapacity` varchar(7) DEFAULT NULL,
  `bitDepth` varchar(8) DEFAULT NULL,
  `blobId` bigint(20) DEFAULT NULL,
  `checksumType` varchar(4) DEFAULT NULL,
  `samplingRate` varchar(5) DEFAULT NULL,
  `compression` varchar(9) DEFAULT NULL,
  `fileFormat` varchar(20) DEFAULT NULL,
  KEY `soundfile_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `soundfile_history`
--

DROP TABLE IF EXISTS `soundfile_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soundfile_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `fileName` text,
  `software` varchar(33) DEFAULT NULL,
  `thickness` varchar(11) DEFAULT NULL,
  `channel` varchar(3) DEFAULT NULL,
  `bitrate` varchar(3) DEFAULT NULL,
  `mimeType` text,
  `durationType` varchar(7) DEFAULT NULL,
  `speed` varchar(10) DEFAULT NULL,
  `duration` varchar(12) DEFAULT NULL,
  `toolId` varchar(13) DEFAULT NULL,
  `checksum` varchar(40) DEFAULT NULL,
  `soundField` varchar(9) DEFAULT NULL,
  `fileContainer` varchar(3) DEFAULT NULL,
  `brand` varchar(27) DEFAULT NULL,
  `surface` varchar(20) DEFAULT NULL,
  `equalisation` varchar(4) DEFAULT NULL,
  `encoding` varchar(10) DEFAULT NULL,
  `codec` varchar(4) DEFAULT NULL,
  `fileSize` bigint(20) DEFAULT NULL,
  `reelSize` varchar(12) DEFAULT NULL,
  `carrierCapacity` varchar(7) DEFAULT NULL,
  `bitDepth` varchar(8) DEFAULT NULL,
  `blobId` bigint(20) DEFAULT NULL,
  `checksumType` varchar(4) DEFAULT NULL,
  `samplingRate` varchar(5) DEFAULT NULL,
  `compression` varchar(9) DEFAULT NULL,
  `fileFormat` varchar(20) DEFAULT NULL,
  KEY `soundfile_history_id` (`id`),
  KEY `soundfile_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stage_edge`
--

DROP TABLE IF EXISTS `stage_edge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stage_edge` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_new` bigint(20) DEFAULT NULL,
  `v_out` bigint(20) DEFAULT NULL,
  `v_in` bigint(20) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `edge_order` bigint(20) DEFAULT NULL,
  `state` char(3) DEFAULT NULL,
  KEY `stage_edge_id_idx` (`id`),
  KEY `stage_edge_stage_idx` (`state`),
  KEY `stage_edge_txn_new_idx` (`txn_new`),
  KEY `stage_edge_combined_state_new_idx` (`txn_new`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stage_property`
--

DROP TABLE IF EXISTS `stage_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stage_property` (
  `id` bigint(20) DEFAULT NULL,
  `txn_new` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` char(3) DEFAULT NULL,
  `value` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stage_vertex`
--

DROP TABLE IF EXISTS `stage_vertex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stage_vertex` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_new` bigint(20) DEFAULT NULL,
  `state` char(3) DEFAULT NULL,
  KEY `stage_vertex_combined_txn_new_state_idx` (`txn_new`,`state`),
  KEY `stage_vertex_combined_state_new_idx` (`txn_new`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(47) DEFAULT NULL,
  KEY `tag_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag_history`
--

DROP TABLE IF EXISTS `tag_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(47) DEFAULT NULL,
  KEY `tag_history_id` (`id`),
  KEY `tag_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `toolCategoryMap`
--

DROP TABLE IF EXISTS `toolCategoryMap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `toolCategoryMap` (
  `id` int(11) DEFAULT NULL,
  `category` varchar(1) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `newid` int(11) DEFAULT NULL,
  KEY `toolCategoryMap_idx` (`id`,`newid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `toolTypeMap`
--

DROP TABLE IF EXISTS `toolTypeMap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `toolTypeMap` (
  `id` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `newid` int(11) DEFAULT NULL,
  KEY `toolTypeMap_idx` (`id`,`newid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tools`
--

DROP TABLE IF EXISTS `tools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tools` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `resolution` varchar(255) DEFAULT NULL,
  `notes` varchar(4000) DEFAULT NULL,
  `serialNumber` varchar(255) DEFAULT NULL,
  `toolTypeId` int(11) DEFAULT NULL,
  `toolCategoryId` int(11) DEFAULT NULL,
  `materialTypeId` int(11) DEFAULT NULL,
  `commitTime` varchar(20) DEFAULT NULL,
  `commitUser` varchar(50) DEFAULT NULL,
  `deleted` varchar(1) DEFAULT 'N',
  KEY `tools_id_idx` (`id`,`deleted`),
  KEY `tools_name_idx` (`name`,`deleted`),
  KEY `tools_tool_type_idx` (`toolTypeId`,`deleted`),
  KEY `tools_tool_category_idx` (`toolCategoryId`,`deleted`),
  KEY `tools_material_type_idx` (`materialTypeId`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `id` bigint(20) DEFAULT NULL,
  `user` varchar(100) DEFAULT NULL,
  `operation` text,
  `time` bigint(20) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txn_index_run`
--

DROP TABLE IF EXISTS `txn_index_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txn_index_run` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `txn_first` bigint(20) NOT NULL DEFAULT '0',
  `txn_last` bigint(20) NOT NULL DEFAULT '0',
  `started` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ended` datetime DEFAULT NULL,
  `objects_modified` int(11) DEFAULT '0',
  `objects_deleted` int(11) DEFAULT '0',
  `status` char(1) NOT NULL DEFAULT 'r',
  `errors` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=122289 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txni_indexing_log`
--

DROP TABLE IF EXISTS `txni_indexing_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txni_indexing_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proc_id` bigint(20) NOT NULL DEFAULT '0',
  `started` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ended` datetime DEFAULT NULL,
  `objects_indexed` bigint(20) DEFAULT '0',
  `objects_deleted` bigint(20) DEFAULT '0',
  `objects_closed` bigint(20) DEFAULT '0',
  `status` char(1) NOT NULL DEFAULT 'r',
  `errors` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txni_last_staged_txn`
--

DROP TABLE IF EXISTS `txni_last_staged_txn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txni_last_staged_txn` (
  `txn` bigint(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txni_schema_version`
--

DROP TABLE IF EXISTS `txni_schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txni_schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `txni_schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txni_staging`
--

DROP TABLE IF EXISTS `txni_staging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txni_staging` (
  `id` bigint(20) NOT NULL,
  `action` char(1) DEFAULT NULL,
  `working` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txni_staging_bg`
--

DROP TABLE IF EXISTS `txni_staging_bg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txni_staging_bg` (
  `id` bigint(20) NOT NULL,
  `action` char(1) DEFAULT NULL,
  `working` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `txni_staging_log`
--

DROP TABLE IF EXISTS `txni_staging_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `txni_staging_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `started` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ended` datetime DEFAULT NULL,
  `txn_first` bigint(20) NOT NULL DEFAULT '0',
  `txn_last` bigint(20) NOT NULL DEFAULT '0',
  `objects_updated` bigint(20) DEFAULT '0',
  `objects_deleted` bigint(20) DEFAULT '0',
  `objects_closed` bigint(20) DEFAULT '0',
  `status` char(1) NOT NULL DEFAULT 'r',
  `errors` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vertex`
--

DROP TABLE IF EXISTS `vertex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vertex` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  UNIQUE KEY `unique_vert` (`id`,`txn_start`),
  KEY `vert_txn_end_idx` (`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work`
--

DROP TABLE IF EXISTS `work`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `occupation` text,
  `endDate` datetime DEFAULT NULL,
  `displayTitlePage` tinyint(1) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `hasRepresentation` varchar(1) DEFAULT NULL,
  `totalDuration` varchar(10) DEFAULT NULL,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `firstPart` varchar(27) DEFAULT NULL,
  `additionalTitle` text,
  `dcmWorkPid` varchar(31) DEFAULT NULL,
  `classification` text,
  `commentsInternal` text,
  `restrictionType` varchar(0) DEFAULT NULL,
  `ilmsSentDateTime` datetime DEFAULT NULL,
  `subType` varchar(7) DEFAULT NULL,
  `scaleEtc` text,
  `startDate` datetime DEFAULT NULL,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `tilePosition` varchar(2) DEFAULT NULL,
  `allowHighResdownload` tinyint(1) DEFAULT NULL,
  `south` varchar(1) DEFAULT NULL,
  `restrictionsOnAccess` text,
  `preservicaType` text,
  `north` varchar(1) DEFAULT NULL,
  `accessConditions` varchar(13) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `eadUpdateReviewRequired` varchar(1) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `moreIlmsDetailsRequired` tinyint(1) DEFAULT NULL,
  `rights` text,
  `genre` varchar(11) DEFAULT NULL,
  `deliveryUrl` varchar(25) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `sheetCreationDate` text,
  `creator` text,
  `sheetName` varchar(32) DEFAULT NULL,
  `coordinates` text,
  `creatorStatement` text,
  `additionalCreator` text,
  `folderType` varchar(31) DEFAULT NULL,
  `eventNote` text,
  `interactiveIndexAvailable` tinyint(1) DEFAULT NULL,
  `startChild` varchar(17) DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `holdingNumber` text,
  `publicNotes` text,
  `series` text,
  `constraint1` text,
  `notes` varchar(30) DEFAULT NULL,
  `catalogueUrl` varchar(35) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `materialFromMultipleSources` tinyint(1) DEFAULT NULL,
  `subject` text,
  `sendToIlms` tinyint(1) DEFAULT NULL,
  `vendorId` varchar(7) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `sensitiveMaterial` varchar(3) DEFAULT NULL,
  `dcmAltPi` varchar(52) DEFAULT NULL,
  `folderNumber` varchar(50) DEFAULT NULL,
  `west` varchar(1) DEFAULT NULL,
  `html` text,
  `preservicaId` text,
  `redocworksReason` varchar(20) DEFAULT NULL,
  `workCreatedDuringMigration` tinyint(1) DEFAULT NULL,
  `author` text,
  `commentsExternal` text,
  `findingAidNote` text,
  `collection` varchar(7) DEFAULT NULL,
  `otherTitle` text,
  `imageServerUrl` varchar(48) DEFAULT NULL,
  `localSystemno` varchar(7) DEFAULT NULL,
  `acquisitionStatus` varchar(7) DEFAULT NULL,
  `reorderType` varchar(8) DEFAULT NULL,
  `immutable` varchar(12) DEFAULT NULL,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `nextStep` varchar(4) DEFAULT NULL,
  `publisher` text,
  `additionalSeries` text,
  `tempHolding` varchar(2) DEFAULT NULL,
  `sortIndex` varchar(28) DEFAULT NULL,
  `isMissingPage` tinyint(1) DEFAULT NULL,
  `standardId` text,
  `representativeId` varchar(26) DEFAULT NULL,
  `edition` text,
  `reorder` varchar(1) DEFAULT NULL,
  `title` text,
  `acquisitionCategory` varchar(19) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `digitalStatusDate` datetime DEFAULT NULL,
  `east` varchar(1) DEFAULT NULL,
  `contributor` text,
  `publicationCategory` varchar(11) DEFAULT NULL,
  `ingestJobId` bigint(20) DEFAULT NULL,
  `subUnitType` varchar(23) DEFAULT NULL,
  `uniformTitle` text,
  `alias` text,
  `rdsAcknowledgementType` varchar(7) DEFAULT NULL,
  `issueDate` datetime DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `coverage` text,
  `summary` text,
  `additionalContributor` text,
  `sendToIlmsDateTime` varchar(10) DEFAULT NULL,
  `sensitiveReason` varchar(42) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `form` varchar(19) DEFAULT NULL,
  `rdsAcknowledgementReceiver` text,
  `digitalStatus` varchar(18) DEFAULT NULL,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  `sprightlyUrl` varchar(39) DEFAULT NULL,
  `depositType` text,
  `parentConstraint` varchar(35) DEFAULT NULL,
  KEY `work_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work_902502_backup`
--

DROP TABLE IF EXISTS `work_902502_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_902502_backup` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) DEFAULT NULL,
  `txn_end` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` char(3) DEFAULT NULL,
  `value` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work_desc`
--

DROP TABLE IF EXISTS `work_desc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_desc` (
  `id` bigint(20) NOT NULL,
  `work_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  `feature_id` text,
  `feature_type` text,
  `fields` text,
  `records` text,
  `type` text,
  `latitude` text,
  `longitude` text,
  `map_datum` text,
  `timestamp` datetime DEFAULT NULL,
  `city` text,
  `province` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `work_history`
--

DROP TABLE IF EXISTS `work_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_history` (
  `id` bigint(20) DEFAULT NULL,
  `txn_start` bigint(20) NOT NULL DEFAULT '0',
  `txn_end` bigint(20) NOT NULL DEFAULT '0',
  `extent` text,
  `dcmDateTimeUpdated` datetime DEFAULT NULL,
  `localSystemNumber` varchar(39) DEFAULT NULL,
  `occupation` text,
  `endDate` datetime DEFAULT NULL,
  `displayTitlePage` tinyint(1) DEFAULT NULL,
  `holdingId` varchar(7) DEFAULT NULL,
  `hasRepresentation` varchar(1) DEFAULT NULL,
  `totalDuration` varchar(10) DEFAULT NULL,
  `dcmDateTimeCreated` datetime DEFAULT NULL,
  `firstPart` varchar(27) DEFAULT NULL,
  `additionalTitle` text,
  `dcmWorkPid` varchar(31) DEFAULT NULL,
  `classification` text,
  `commentsInternal` text,
  `restrictionType` varchar(0) DEFAULT NULL,
  `ilmsSentDateTime` datetime DEFAULT NULL,
  `subType` varchar(7) DEFAULT NULL,
  `scaleEtc` text,
  `startDate` datetime DEFAULT NULL,
  `dcmRecordUpdater` varchar(26) DEFAULT NULL,
  `tilePosition` varchar(2) DEFAULT NULL,
  `allowHighResdownload` tinyint(1) DEFAULT NULL,
  `south` varchar(1) DEFAULT NULL,
  `restrictionsOnAccess` text,
  `preservicaType` text,
  `north` varchar(1) DEFAULT NULL,
  `accessConditions` varchar(13) DEFAULT NULL,
  `internalAccessConditions` varchar(10) DEFAULT NULL,
  `eadUpdateReviewRequired` varchar(1) DEFAULT NULL,
  `australianContent` tinyint(1) DEFAULT NULL,
  `moreIlmsDetailsRequired` tinyint(1) DEFAULT NULL,
  `rights` text,
  `genre` varchar(11) DEFAULT NULL,
  `deliveryUrl` varchar(25) DEFAULT NULL,
  `recordSource` varchar(8) DEFAULT NULL,
  `sheetCreationDate` text,
  `creator` text,
  `sheetName` varchar(32) DEFAULT NULL,
  `coordinates` text,
  `creatorStatement` text,
  `additionalCreator` text,
  `folderType` varchar(31) DEFAULT NULL,
  `eventNote` text,
  `interactiveIndexAvailable` tinyint(1) DEFAULT NULL,
  `startChild` varchar(17) DEFAULT NULL,
  `bibLevel` varchar(9) DEFAULT NULL,
  `holdingNumber` text,
  `publicNotes` text,
  `series` text,
  `constraint1` text,
  `notes` varchar(30) DEFAULT NULL,
  `catalogueUrl` varchar(35) DEFAULT NULL,
  `encodingLevel` varchar(47) DEFAULT NULL,
  `materialFromMultipleSources` tinyint(1) DEFAULT NULL,
  `subject` text,
  `sendToIlms` tinyint(1) DEFAULT NULL,
  `vendorId` varchar(7) DEFAULT NULL,
  `allowOnsiteAccess` tinyint(1) DEFAULT NULL,
  `language` varchar(35) DEFAULT NULL,
  `sensitiveMaterial` varchar(3) DEFAULT NULL,
  `dcmAltPi` varchar(52) DEFAULT NULL,
  `folderNumber` varchar(50) DEFAULT NULL,
  `west` varchar(1) DEFAULT NULL,
  `html` text,
  `preservicaId` text,
  `redocworksReason` varchar(20) DEFAULT NULL,
  `workCreatedDuringMigration` tinyint(1) DEFAULT NULL,
  `author` text,
  `commentsExternal` text,
  `findingAidNote` text,
  `collection` varchar(7) DEFAULT NULL,
  `otherTitle` text,
  `imageServerUrl` varchar(48) DEFAULT NULL,
  `localSystemno` varchar(7) DEFAULT NULL,
  `acquisitionStatus` varchar(7) DEFAULT NULL,
  `reorderType` varchar(8) DEFAULT NULL,
  `immutable` varchar(12) DEFAULT NULL,
  `copyrightPolicy` varchar(31) DEFAULT NULL,
  `nextStep` varchar(4) DEFAULT NULL,
  `publisher` text,
  `additionalSeries` text,
  `tempHolding` varchar(2) DEFAULT NULL,
  `sortIndex` varchar(28) DEFAULT NULL,
  `isMissingPage` tinyint(1) DEFAULT NULL,
  `standardId` text,
  `representativeId` varchar(26) DEFAULT NULL,
  `edition` text,
  `reorder` varchar(1) DEFAULT NULL,
  `title` text,
  `acquisitionCategory` varchar(19) DEFAULT NULL,
  `subUnitNo` text,
  `expiryDate` datetime DEFAULT NULL,
  `digitalStatusDate` datetime DEFAULT NULL,
  `east` varchar(1) DEFAULT NULL,
  `contributor` text,
  `publicationCategory` varchar(11) DEFAULT NULL,
  `ingestJobId` bigint(20) DEFAULT NULL,
  `subUnitType` varchar(23) DEFAULT NULL,
  `uniformTitle` text,
  `alias` text,
  `rdsAcknowledgementType` varchar(7) DEFAULT NULL,
  `issueDate` datetime DEFAULT NULL,
  `bibId` varchar(9) DEFAULT NULL,
  `coverage` text,
  `summary` text,
  `additionalContributor` text,
  `sendToIlmsDateTime` varchar(10) DEFAULT NULL,
  `sensitiveReason` varchar(42) DEFAULT NULL,
  `carrier` varchar(17) DEFAULT NULL,
  `form` varchar(19) DEFAULT NULL,
  `rdsAcknowledgementReceiver` text,
  `digitalStatus` varchar(18) DEFAULT NULL,
  `dcmRecordCreator` varchar(14) DEFAULT NULL,
  `sprightlyUrl` varchar(39) DEFAULT NULL,
  `depositType` text,
  `parentConstraint` varchar(35) DEFAULT NULL,
  KEY `work_history_id` (`id`),
  KEY `work_history_txn_id` (`id`,`txn_start`,`txn_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'amberdb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-07-22 15:22:29
