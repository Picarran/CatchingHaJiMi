-- SQL schema for 点格子困猫 (MySQL 5.7+)
CREATE DATABASE IF NOT EXISTS catchinghajimi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE catchinghajimi;

CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `game_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `game_uuid` VARCHAR(36) NOT NULL,
  `start_time` DATETIME,
  `last_update_time` DATETIME,
  `clicks` INT DEFAULT 0,
  `state_json` JSON,
  `result` ENUM('ONGOING','WIN','LOSE') DEFAULT 'ONGOING',
  `duration_seconds` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX (`user_id`),
  INDEX (`game_uuid`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);
