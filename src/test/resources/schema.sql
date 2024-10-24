CREATE TABLE `countries` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `leagues` (
  `id` int NOT NULL AUTO_INCREMENT,
  `country_id` int DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`country_id`) REFERENCES `countries`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `clubs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `league_id` int DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`league_id`) REFERENCES `leagues`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `players` (
  `id` int NOT NULL AUTO_INCREMENT,
  `club_id` int DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `number` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`club_id`) REFERENCES `clubs`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `seasons` (
  `id` int NOT NULL,
  `name` varchar(10) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `current` tinyint DEFAULT 1,
  PRIMARY KEY (`id`)
);

CREATE TABLE `game_results` (
  `id` int NOT NULL AUTO_INCREMENT,
  `home_club_id` int DEFAULT NULL,
  `away_club_id` int DEFAULT NULL,
  `home_score` int DEFAULT NULL,
  `away_score` int DEFAULT NULL,
  `winner_club_id` int DEFAULT NULL,
  `league_id` int DEFAULT NULL,
  `game_date` date DEFAULT NULL,
  `season_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`home_club_id`) REFERENCES `clubs`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`away_club_id`) REFERENCES `clubs`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`winner_club_id`) REFERENCES `clubs`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`league_id`) REFERENCES `leagues`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`season_id`) REFERENCES `seasons`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `player_game_stats` (
  `id` int NOT NULL AUTO_INCREMENT,
  `player_id` int DEFAULT NULL,
  `club_id` int DEFAULT NULL,
  `number` int DEFAULT NULL,
  `starter` tinyint DEFAULT 1,
  `goals` int DEFAULT NULL,
  `assists` int DEFAULT NULL,
  `own_goals` int DEFAULT NULL,
  `minutes` int DEFAULT NULL,
  `yellow_cards` int DEFAULT NULL,
  `red_cards` int DEFAULT NULL,
  `game_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`player_id`) REFERENCES `players`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`club_id`) REFERENCES `clubs`(`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (`game_id`) REFERENCES `game_results`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
);





