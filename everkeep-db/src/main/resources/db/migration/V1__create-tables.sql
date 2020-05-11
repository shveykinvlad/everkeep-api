CREATE SEQUENCE IF NOT EXISTS note_id_seq;

CREATE TABLE note (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    text TEXT
);

CREATE SEQUENCE IF NOT EXISTS user_id_seq;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS role_id_seq;

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
);