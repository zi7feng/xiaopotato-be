DROP DATABASE TAPI;

CREATE DATABASE IF NOT EXISTS TAPI;

USE TAPI;

-- Interface information
CREATE TABLE IF NOT EXISTS InterfaceInfo
(
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    name VARCHAR(256) NOT NULL COMMENT 'name',
    description VARCHAR(256) NULL COMMENT 'description',
    url VARCHAR(512) NOT NULL COMMENT 'interface url',
    request_params TEXT NOT NULL COMMENT 'request parameters',
    request_header TEXT NULL COMMENT 'request header',
    response_header TEXT NULL COMMENT 'response header',
    status INT DEFAULT 0 NOT NULL COMMENT 'interface status (0 - closed, 1 - open)',
    method VARCHAR(256) NOT NULL COMMENT 'request method',
    user_id BIGINT NOT NULL COMMENT 'creator user id',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'creation time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT 'deletion status (0 - not deleted, 1 - deleted)',
    PRIMARY KEY (id)
) COMMENT 'interface information';
