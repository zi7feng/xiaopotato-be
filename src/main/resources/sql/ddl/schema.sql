CREATE DATABASE IF NOT EXISTS POTATO;

USE POTATO;
# DROP table POTATO.User;

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
    description    VARCHAR(256)                           NULL COMMENT 'User Description',
    status         TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Account Status (0 - Normal, 1 - Blocked)',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    is_delete      TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Is Deleted? (1 - Deleted)',
    CONSTRAINT uni_userAccount UNIQUE (user_account)
)
    COMMENT 'User Table';

# DROP TABLE Post;
CREATE TABLE IF NOT EXISTS POTATO.Post
(
    id             BIGINT AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    post_title     VARCHAR(256)                           NOT NULL COMMENT 'Post Title',
    post_content   VARCHAR(256)                           NOT NULL COMMENT 'Post Content',
    post_image     VARCHAR(256)                           NOT NULL COMMENT 'post image',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    is_delete      TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Is Deleted? (1 - Deleted)'
)
    COMMENT 'Post Table';

# drop table UserPost;
CREATE TABLE IF NOT EXISTS POTATO.UserPost
(
    user_id BIGINT NOT NULL COMMENT 'User ID',
    post_id BIGINT NOT NULL COMMENT 'Post ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES POTATO.User(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES POTATO.Post(id) ON DELETE CASCADE ON UPDATE CASCADE
)
    COMMENT 'User-Post Relationship Table';
