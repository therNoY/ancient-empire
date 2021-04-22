CREATE TABLE game_room  (
  `room_id` varchar(4) NOT NULL COMMENT '房间号',
  `map_id` varchar(64) NULL COMMENT '选择的地图',
  `room_name` varchar(255) NULL COMMENT '房间名字',
  `player_count` tinyint NULL COMMENT '玩家数',
  `join_count` tinyint(255) NULL COMMENT '加入玩家数',
  `room_owner` int(255) NULL COMMENT '房主',
  `creater` int(255) NULL COMMENT '创建玩家',
  `create_time` datetime NULL COMMENT '创建时间',
  PRIMARY KEY (`room_id`)
);




CREATE TABLE `user_join_room`  (
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `room_id` varchar(4) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '房间号',
  PRIMARY KEY (`user_id`) USING BTREE
);





/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : 127.0.0.1:3306
 Source Schema         : ancient_empire

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 21/04/2021 18:55:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_map
-- ----------------------------
DROP TABLE IF EXISTS `user_map`;
CREATE TABLE `user_map`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `units_string` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `region_string` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `map_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `map_row` tinyint(12) NULL DEFAULT NULL,
  `map_column` tinyint(12) NULL DEFAULT NULL,
  `create_user_id` int(12) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `un_save` tinyint(1) NULL DEFAULT NULL,
  `template_id` int(12) NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_record
-- ----------------------------
DROP TABLE IF EXISTS `user_record`;
CREATE TABLE `user_record`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `template_id` int(12) NULL DEFAULT NULL,
  `map_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `max_pop` tinyint(12) NULL DEFAULT NULL,
  `game_map_string` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `army_list_string` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `tomb_list_string` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `record_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_user_id` int(12) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `un_save` tinyint(1) NULL DEFAULT NULL,
  `curr_army_index` tinyint(12) NULL DEFAULT NULL,
  `curr_player` int(12) NULL DEFAULT NULL,
  `current_round` tinyint(12) NULL DEFAULT NULL,
  `curr_unit_uuid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `curr_region_index` int(12) NULL DEFAULT NULL,
  `curr_point_row` int(12) NULL DEFAULT NULL,
  `curr_point_column` int(12) NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

