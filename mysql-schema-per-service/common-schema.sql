create database ftgoconsumerservice;
GRANT ALL PRIVILEGES ON ftgoconsumerservice.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database ftgoorderservice;
GRANT ALL PRIVILEGES ON ftgoorderservice.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database ftgokitchenservice;
GRANT ALL PRIVILEGES ON ftgokitchenservice.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database ftgorestaurantservice;
GRANT ALL PRIVILEGES ON ftgorestaurantservice.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database ftgoaccountingservice;
GRANT ALL PRIVILEGES ON ftgoaccountingservice.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database ftgoorderhistoryservice;
GRANT ALL PRIVILEGES ON ftgoorderhistoryservice.* TO 'mysqluser'@'%' WITH GRANT OPTION;

USE eventuate;

DROP Table IF Exists saga_instance_participants;
DROP Table IF Exists saga_instance;
DROP Table IF Exists saga_lock_table;
DROP Table IF Exists saga_stash_table;

CREATE TABLE saga_instance_participants (
  saga_type VARCHAR(100) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  destination VARCHAR(100) NOT NULL,
  resource VARCHAR(100) NOT NULL,
  PRIMARY KEY(saga_type, saga_id, destination, resource)
);


CREATE TABLE saga_instance(
  saga_type VARCHAR(100) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  state_name VARCHAR(100) NOT NULL,
  last_request_id VARCHAR(100),
  end_state INT(1),
  compensating INT(1),
  saga_data_type VARCHAR(1000) NOT NULL,
  saga_data_json VARCHAR(1000) NOT NULL,
  PRIMARY KEY(saga_type, saga_id)
);

create table saga_lock_table(
  target VARCHAR(100) PRIMARY KEY,
  saga_type VARCHAR(100) NOT NULL,
  saga_Id VARCHAR(100) NOT NULL
);

create table saga_stash_table(
  message_id VARCHAR(100) PRIMARY KEY,
  target VARCHAR(100) NOT NULL,
  saga_type VARCHAR(100) NOT NULL,
  saga_id VARCHAR(100) NOT NULL,
  message_headers VARCHAR(1000) NOT NULL,
  message_payload VARCHAR(1000) NOT NULL
);
