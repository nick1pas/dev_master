/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MariaDB
 Source Server Version : 100622
 Source Host           : localhost:3306
 Source Schema         : sarada

 Target Server Type    : MariaDB
 Target Server Version : 100622
 File Encoding         : 65001

 Date: 02/11/2025 17:37:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ban_hwid_chat
-- ----------------------------
DROP TABLE IF EXISTS `ban_hwid_chat`;
CREATE TABLE `ban_hwid_chat`  (
  `objectId` int(11) NOT NULL DEFAULT 0,
  `duration` bigint(20) NOT NULL DEFAULT 0,
  `hwid` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `account_name` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `char_name` varchar(35) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`hwid`, `objectId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for banned_hwid
-- ----------------------------
DROP TABLE IF EXISTS `banned_hwid`;
CREATE TABLE `banned_hwid`  (
  `char_name` varchar(35) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `hwid` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`hwid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for daily_rewarded_players_hwid
-- ----------------------------
DROP TABLE IF EXISTS `daily_rewarded_players_hwid`;
CREATE TABLE `daily_rewarded_players_hwid`  (
  `day` int(11) NULL DEFAULT NULL,
  `hwid` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hwid_info
-- ----------------------------
DROP TABLE IF EXISTS `hwid_info`;
CREATE TABLE `hwid_info`  (
  `HWID` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `WindowsCount` int(10) UNSIGNED NOT NULL DEFAULT 1,
  `Account` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `PlayerID` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `LockType` enum('PLAYER_LOCK','ACCOUNT_LOCK','NONE') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'NONE',
  PRIMARY KEY (`HWID`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
