--liquibase formatted sql

--changeset Natali:19.11.2022-001.create_table

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE notification
(
    id                   BIGINT NOT NULL,
    message_notification TEXT not null ,
    data_notification    TIMESTAMP WITHOUT TIME ZONE,
    user_chat_id         BIGINT,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);
ALTER TABLE notification
    ADD CONSTRAINT unique_notification
        UNIQUE (message_notification,data_notification,user_chat_id);