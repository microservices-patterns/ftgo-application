USE eventuate;

DROP Table IF Exists message;
DROP Table IF Exists received_messages;

CREATE TABLE message (
  id VARCHAR(767) PRIMARY KEY,
  destination VARCHAR(1000) NOT NULL,
  headers VARCHAR(1000) NOT NULL,
  payload VARCHAR(1000) NOT NULL,
  published SMALLINT DEFAULT 0
);

CREATE INDEX message_published_idx ON message(published, id);

CREATE TABLE received_messages (
  consumer_id VARCHAR(767),
  message_id VARCHAR(767),
  PRIMARY KEY(consumer_id, message_id)
);

