CREATE TABLE `USER`
(
    `ID`         BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '유저 아이디',
    `USERNAME`   VARCHAR(50)  NOT NULL COMMENT '유저 이름',
    `PASSWORD`   VARCHAR(255) NOT NULL COMMENT '유저 비밀번호',
    `CREATED_AT` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    `UPDATED_AT` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;