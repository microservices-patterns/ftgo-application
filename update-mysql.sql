USE eventuate;

CREATE TABLE offset_store(client_name VARCHAR(255) NOT NULL PRIMARY KEY, serialized_offset VARCHAR(255));
ALTER TABLE message ADD creation_time BIGINT;
ALTER TABLE received_messages ADD creation_time BIGINT;
