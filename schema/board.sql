CREATE TABLE `BOARD`
(
    `ID`                 BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '게시판 아이디',
    `TITLE`				 text NOT NULL COMMENT '게시판 제목',
    `CONTENT`            text NOT NULL COMMENT '게시판 내용',
    `CREATED_AT`         DATETIME NOT NULL DEFAULT current_timestamp() COMMENT '생성일',
    `UPDATED_AT`         DATETIME          DEFAULT current_timestamp() ON UPDATE current_timestamp () COMMENT '수정일',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;