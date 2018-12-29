DROP DATABASE `player_tracker`;

CREATE DATABASE  IF NOT EXISTS `player_tracker`;
USE `player_tracker`;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `players`;

CREATE TABLE `Players` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(45) DEFAULT NULL,
  `chips` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

