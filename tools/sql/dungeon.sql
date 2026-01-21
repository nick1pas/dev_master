-- ----------------------------
-- Table structure for `dungeon`
-- ----------------------------
DROP TABLE IF EXISTS `dungeon`;
CREATE TABLE `dungeon` (
  `dungid` tinyint(4) DEFAULT NULL,
  `ipaddr` varchar(80) DEFAULT NULL,
  `lastjoin` bigint(20) unsigned NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
