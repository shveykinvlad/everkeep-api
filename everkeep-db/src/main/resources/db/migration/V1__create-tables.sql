CREATE SEQUENCE IF NOT EXISTS note_id_seq;

CREATE TABLE note (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    text TEXT,
    priority VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS user_id_seq;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    enabled BOOLEAN
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

CREATE SEQUENCE IF NOT EXISTS verification_token_seq;

CREATE TABLE verification_token (
    id SERIAL PRIMARY KEY,
    value VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    action VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE NOT NULL
);
