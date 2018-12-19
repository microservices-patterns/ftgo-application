USE eventuate;

CREATE TABLE cdc_monitoring (reader_id BIGINT PRIMARY KEY, last_time BIGINT);
CREATE TABLE offset_store(client_name VARCHAR(255) NOT NULL PRIMARY KEY, serialized_offset VARCHAR(255));
ALTER TABLE message ADD creation_time BIGINT;
ALTER TABLE received_messages ADD creation_time BIGINT;
