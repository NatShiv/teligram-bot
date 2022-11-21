--liquibase formatted sql

--changeset Natali:19.11.2022-001.create_table


CREATE TABLE notification
(
    id                   BIGSERIAL NOT NULL,
    message_notification TEXT not null ,
    data_notification    TIMESTAMP WITHOUT TIME ZONE,
    user_chat_id         BIGINT,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);
ALTER TABLE notification
    ADD CONSTRAINT unique_notification
        UNIQUE (message_notification,data_notification,user_chat_id);