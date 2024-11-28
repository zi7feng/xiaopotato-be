CREATE DATABASE IF NOT EXISTS POTATO;

USE POTATO;

# SET FOREIGN_KEY_CHECKS = 0;
#
# DROP TABLE IF EXISTS POTATO.Notification;
# DROP TABLE IF EXISTS POTATO.Postcomment;
# DROP TABLE IF EXISTS POTATO.Comment;
# DROP TABLE IF EXISTS POTATO.Userfollow;
# DROP TABLE IF EXISTS POTATO.Saves;
# DROP TABLE IF EXISTS POTATO.Likes;
# DROP TABLE IF EXISTS POTATO.Usertag;
# DROP TABLE IF EXISTS POTATO.Posttag;
# DROP TABLE IF EXISTS POTATO.UserPost;
# DROP TABLE IF EXISTS POTATO.Tag;
# DROP TABLE IF EXISTS POTATO.Post;
# DROP TABLE IF EXISTS POTATO.User;
#
# SET FOREIGN_KEY_CHECKS = 1;


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
    is_delete      TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Is Deleted? (1 - Deleted)'
)
    COMMENT 'User Table';

CREATE TABLE IF NOT EXISTS POTATO.Post
(
    id             BIGINT AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    post_title     VARCHAR(256)                           NOT NULL COMMENT 'Post Title',
    post_content   VARCHAR(8192)                         NOT NULL COMMENT 'Post Content',
    post_image     VARCHAR(1024)                          NOT NULL COMMENT 'post image',
    post_genre     VARCHAR(256)                           NOT NULL COMMENT 'post genre',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    is_delete      TINYINT      DEFAULT 0                 NOT NULL COMMENT 'Is Deleted? (1 - Deleted)',
    image_width BIGINT DEFAULT 0 NOT NULL,
    image_height BIGINT DEFAULT 0 NOT NULL
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

CREATE TABLE IF NOT EXISTS POTATO.Tag
(
    id             BIGINT AUTO_INCREMENT COMMENT 'Unique identifier for the tag' PRIMARY KEY,
    content        VARCHAR(256) NOT NULL COMMENT 'Content of the tag, typically a keyword or label',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Timestamp when the tag was created',
    update_time    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp when the tag was last updated',
    is_delete      TINYINT DEFAULT 0 NOT NULL COMMENT 'Indicates if the tag has been deleted (0 - Not deleted, 1 - Deleted)'
)
    COMMENT 'Tag Table: stores tags or keywords associated with posts or other entities';

CREATE TABLE IF NOT EXISTS POTATO.Posttag
(
    post_id BIGINT NOT NULL COMMENT 'Post ID',
    tag_id BIGINT NOT NULL COMMENT 'Tag ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_posttag FOREIGN KEY (post_id) REFERENCES POTATO.Post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tag_posttag FOREIGN KEY (tag_id) REFERENCES POTATO.Tag(id) ON DELETE CASCADE ON UPDATE CASCADE
)
    COMMENT 'Post-Tag Relationship Table: stores the many-to-many relationship between posts and tags';

CREATE TABLE IF NOT EXISTS POTATO.Usertag
(
    user_id BIGINT NOT NULL COMMENT 'User ID',
    tag_id BIGINT NOT NULL COMMENT 'Tag ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    PRIMARY KEY (user_id, tag_id),
    CONSTRAINT fk_user_usertag FOREIGN KEY (user_id) REFERENCES POTATO.User(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tag_usertag FOREIGN KEY (tag_id) REFERENCES POTATO.Tag(id) ON DELETE CASCADE ON UPDATE CASCADE
)
    COMMENT 'User-Tag Relationship Table: stores the relationship between users and their custom tags';

CREATE TABLE IF NOT EXISTS  POTATO.Likes
(
    user_id BIGINT NOT NULL COMMENT 'User ID',
    post_id BIGINT NOT NULL COMMENT 'Post ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    CONSTRAINT fk_user_like FOREIGN KEY (user_id) REFERENCES POTATO.User(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_post_like FOREIGN KEY (post_id) REFERENCES POTATO.Post(id) ON DELETE CASCADE ON UPDATE CASCADE

);

CREATE TABLE IF NOT EXISTS  POTATO.Saves
(
    user_id BIGINT NOT NULL COMMENT 'User ID',
    post_id BIGINT NOT NULL COMMENT 'Post ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create Time',
    CONSTRAINT fk_user_save FOREIGN KEY (user_id) REFERENCES POTATO.User(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_post_save FOREIGN KEY (post_id) REFERENCES POTATO.Post(id) ON DELETE CASCADE ON UPDATE CASCADE

);

CREATE TABLE IF NOT EXISTS Userfollow (
      follower_id BIGINT NOT NULL COMMENT 'ID of the user who is following',
      followed_id BIGINT NOT NULL COMMENT 'ID of the user being followed',
      create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Timestamp when the follow action was created',
      PRIMARY KEY (follower_id, followed_id),
      CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES User(id) ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT fk_followed FOREIGN KEY (followed_id) REFERENCES User(id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT 'User Follow Relationship Table';


CREATE TABLE Comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Timestamp when the follow action was created',
    parent_id BIGINT DEFAULT NULL,
    INDEX idx_parent_id (parent_id),
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES Comment(comment_id) ON DELETE CASCADE


);

CREATE TABLE Postcomment (
    comment_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    FOREIGN KEY (comment_id) REFERENCES Comment(comment_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE,
    UNIQUE (comment_id, post_id)
);


CREATE TABLE Notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              source_id BIGINT,
                              first_name VARCHAR(255),
                              last_name VARCHAR(255),
                              account VARCHAR(255),
                              avatar VARCHAR(255),
                              notification_type VARCHAR(50),
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Timestamp when the follow action was created',
                              is_read TINYINT DEFAULT 0,
                              is_push TINYINT DEFAULT 0,
                              FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE POTATO.Email (
                              email_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              from_user VARCHAR(255) NOT NULL COMMENT 'From user ID',
                              to_user   VARCHAR(255) NOT NULL COMMENT 'To user',
                              subject      VARCHAR(255) NOT NULL COMMENT 'Subject of E-mail',
                              content      VARCHAR(8192) NOT NULL COMMENT 'Content of E-mail',
                              create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Timestamp when the follow action was created',
                              is_delete      TINYINT DEFAULT 0 NOT NULL COMMENT 'Indicates if the tag has been deleted (0 - Not deleted, 1 - Deleted)'
);


