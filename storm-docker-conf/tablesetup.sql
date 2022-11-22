CREATE DATABASE IF NOT EXISTS portals;
use portals;



CREATE TABLE urls (
 url VARCHAR(512),
 status VARCHAR(16) DEFAULT 'DISCOVERED',
 nextfetchdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 metadata TEXT,
 bucket SMALLINT DEFAULT 0,
 host VARCHAR(128),
 id char(32),
 event_id char(32),
 PRIMARY KEY(id)
);

ALTER TABLE urls ADD INDEX b (`bucket`);
ALTER TABLE urls ADD INDEX t (`nextfetchdate`);
ALTER TABLE urls ADD INDEX h (`host`);
ALTER TABLE urls ADD INDEX u (`url`);
ALTER TABLE urls ADD INDEX e (`event_id`);

DROP TABLE IF EXISTS `input_jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `input_jobs` (
  `event_id` char(32) DEFAULT NULL,
  `reqdate` datetime DEFAULT NULL,
  `url` varchar(512) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `trace_url` varchar(255) DEFAULT NULL,
  `id` char(32) DEFAULT NULL,
  `meta` text,
  `capdate` datetime DEFAULT NULL,
  `status` varchar(16) DEFAULT NULL,
  `warc_file` varchar(255) DEFAULT NULL,
  UNIQUE KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

ALTER TABLE input_jobs  ADD INDEX uj (`url`);
ALTER TABLE input_jobs  ADD INDEX tj (`reqdate`);


