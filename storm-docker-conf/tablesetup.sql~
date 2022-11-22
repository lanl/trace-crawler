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



