CREATE DATABASE IF NOT EXISTS POTATO;

USE POTATO;

CREATE TABLE IF NOT EXISTS POTATO.User
(
    id             BIGINT AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    first_name     VARCHAR(256)                           NULL COMMENT 'User First Name',
    last_name      VARCHAR(256)                           NULL COMMENT 'User Last Name',
    user_account   VARCHAR(256)                           NOT NULL COMMENT 'User Account',
    user_avatar    VARCHAR(1024)                          NULL COMMENT 'User Avatar',
    email          VARCHAR(256)                           NULL COMMENT 'Email',
    phone          VARCHAR(256)                           NULL COMMENT 'Phone',
    user_role      VARCHAR(256) DEFAULT 'user'            NOT NULL COMMENT 'User Role: user / admin',
    user_password  VARCHAR(512)                           NULL COMMENT 'User Password',
    gender         VARCHAR(256)                           NULL COMMENT 'User Gender',
    status         TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Account Status (0 - Normal, 1 - Blocked)',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    is_delete      TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Is Deleted? (1 - Deleted)',
    CONSTRAINT uni_userAccount UNIQUE (user_account)
)
    COMMENT 'User Table';
