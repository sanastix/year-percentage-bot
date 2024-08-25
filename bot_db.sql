-- Create the database
CREATE DATABASE year_percentage_bot_db;

-- Connect to the newly created database
\c year_percentage_bot_db;

-- Create the table bot_user
CREATE TABLE bot_user (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    bot_running BOOLEAN NOT NULL
);

-- Optional: Create an index on chat_id if you plan to query based on it frequently
CREATE INDEX idx_bot_user_chat_id ON bot_user(chat_id);
