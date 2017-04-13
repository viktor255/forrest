-- schema-javadb.sql
-- DDL commands for JavaDB/Derby
CREATE TABLE pots (
  ID       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  COL      INT,
  ROW      INT,
  CAPACITY INT,
  NOTE     VARCHAR(255)
);

-- CREATE TABLE trees (
--   id       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
--   fullname VARCHAR(50),
--   address  VARCHAR(150),
--   phone    VARCHAR(20),
--   email    VARCHAR(50)
-- );

-- CREATE TABLE leases (
--   id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
--   bookId      INT REFERENCES books (id)
--     ON DELETE CASCADE,
--   customerId  INT REFERENCES customers (id)
--     ON DELETE CASCADE,
--   startDate   DATE,
--   expectedEnd DATE,
--   realEnd     DATE
-- );