CREATE DATABASE IF NOT EXISTS portals;
use portals;



--
-- Table structure for table `urls`
--

DROP TABLE IF EXISTS `urls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `urls` (
  `url` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` varchar(16) COLLATE utf8_unicode_ci DEFAULT 'DISCOVERED',
  `nextfetchdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `metadata` text COLLATE utf8_unicode_ci,
  `bucket` smallint(6) DEFAULT '0',
  `host` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id` char(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `event_id` char(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  UNIQUE KEY `id` (`id`),
  KEY `b` (`bucket`),
  KEY `t` (`nextfetchdate`),
  KEY `h` (`host`),
  KEY `u` (`url`(255)),
  KEY `i` (`id`),
  KEY `e` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



//GRANT ALL PRIVILEGES ON urls TO 'cache'@'localhost' IDENTIFIED BY  'plenty' WITH GRANT OPTION;
//FLUSH PRIVILEGES;
//ALTER DATABASE portals  CHARACTER SET utf8 COLLATE utf8_unicode_ci;

