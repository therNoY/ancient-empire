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

 Date: 13/07/2021 09:34:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ability
-- ----------------------------
DROP TABLE IF EXISTS `ability`;
CREATE TABLE `ability`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '能力类型',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '能力名称',
  `description` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '能力说明',
  `buff_id` int(11) NOT NULL COMMENT '引用buff',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单位信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for ae_sequence
-- ----------------------------
DROP TABLE IF EXISTS `ae_sequence`;
CREATE TABLE `ae_sequence`  (
  `table_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sequence` int(255) NULL DEFAULT NULL,
  PRIMARY KEY (`table_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buff
-- ----------------------------
DROP TABLE IF EXISTS `buff`;
CREATE TABLE `buff`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'buff类型',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'buff名称',
  `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'buff说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'buff信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for game_room
-- ----------------------------
DROP TABLE IF EXISTS `game_room`;
CREATE TABLE `game_room`  (
  `room_id` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '房间号',
  `map_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '选择的地图',
  `room_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '房间名字',
  `player_count` tinyint(4) NULL DEFAULT NULL COMMENT '玩家数',
  `join_count` tinyint(4) NULL DEFAULT NULL COMMENT '加入玩家数',
  `room_owner` int(11) NULL DEFAULT NULL COMMENT '房主',
  `creater` int(11) NULL DEFAULT NULL COMMENT '创建玩家',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `game_type` tinyint(64) NULL DEFAULT NULL,
  `ob_enable` tinyint(64) NULL DEFAULT NULL,
  `map_config` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`room_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限值',
  `uri` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端资源路径',
  `status` int(1) NULL DEFAULT NULL COMMENT '启用状态；0->禁用；1->启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '权限表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for region_mes
-- ----------------------------
DROP TABLE IF EXISTS `region_mes`;
CREATE TABLE `region_mes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地图类型',
  `name` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地形的名称',
  `buff` tinyint(3) NULL DEFAULT 0 COMMENT '增加的防御力',
  `restore` tinyint(4) NULL DEFAULT 0 COMMENT '恢复',
  `tax` tinyint(3) NULL DEFAULT 0 COMMENT '每回合收的金币',
  `deplete` tinyint(1) NULL DEFAULT 1 COMMENT '消耗的移动力',
  `purify` tinyint(2) NULL DEFAULT NULL COMMENT '是否净化',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '无' COMMENT '描述',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '创建者Id',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用 0 不启用 1 启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '地形信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for region_template_relation
-- ----------------------------
DROP TABLE IF EXISTS `region_template_relation`;
CREATE TABLE `region_template_relation`  (
  `region_id` int(11) NOT NULL COMMENT '地形Id',
  `template_id` int(11) NOT NULL COMMENT '模板Id',
  PRIMARY KEY (`region_id`, `template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `status` int(1) NULL DEFAULT 1 COMMENT '启用状态：0->禁用；1->启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for role_permission_relation
-- ----------------------------
DROP TABLE IF EXISTS `role_permission_relation`;
CREATE TABLE `role_permission_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NULL DEFAULT NULL,
  `permission_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色权限表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for unit_ability
-- ----------------------------
DROP TABLE IF EXISTS `unit_ability`;
CREATE TABLE `unit_ability`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `unit_id` int(11) NOT NULL COMMENT '单位Id',
  `ability_id` int(11) NOT NULL COMMENT '能力Id',
  `use_ability_animation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用能力的动画',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 246 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '能力信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for unit_level_mes
-- ----------------------------
DROP TABLE IF EXISTS `unit_level_mes`;
CREATE TABLE `unit_level_mes`  (
  `unit_id` int(11) NOT NULL COMMENT '单位Id',
  `level` int(1) NOT NULL DEFAULT 0 COMMENT '等级',
  `min_attack` int(4) NULL DEFAULT 0 COMMENT '最低攻击力',
  `max_attack` int(4) NULL DEFAULT 0 COMMENT '最高攻击力',
  `max_life` tinyint(2) UNSIGNED NULL DEFAULT 100 COMMENT '最大生命值',
  `physical_defense` tinyint(3) NULL DEFAULT 0 COMMENT '护甲',
  `magic_defense` tinyint(3) NULL DEFAULT 0 COMMENT '魔法防御',
  `speed` tinyint(2) NULL DEFAULT 0 COMMENT '移动力',
  PRIMARY KEY (`unit_id`, `level`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单位等级信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for unit_mes
-- ----------------------------
DROP TABLE IF EXISTS `unit_mes`;
CREATE TABLE `unit_mes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '单位类型',
  `version` int(16) NULL DEFAULT NULL COMMENT '版本',
  `status` tinyint(255) NULL DEFAULT 1 COMMENT '是否发布',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '单位名称',
  `attack_type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '攻击类型 物理 魔法',
  `price` int(3) NOT NULL COMMENT '购买价格',
  `min_attach_range` tinyint(1) NULL DEFAULT NULL COMMENT '最近的攻击距离',
  `max_attach_range` tinyint(1) NULL DEFAULT NULL COMMENT '最远的攻击距离',
  `population` tinyint(1) NULL DEFAULT NULL COMMENT '所占人口',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '单位描述',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '创建者Id',
  `tradable` tinyint(4) NULL DEFAULT 1 COMMENT '是否是可以购买的0 不可 1可以',
  `promotion` tinyint(255) NULL DEFAULT NULL COMMENT '是否是晋升单位，晋升单位无法购买',
  `img_index` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位图片的索引',
  `enable` tinyint(4) NULL DEFAULT NULL COMMENT '是否启用',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单位信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for unit_show
-- ----------------------------
DROP TABLE IF EXISTS `unit_show`;
CREATE TABLE `unit_show`  (
  `unit_id` int(11) NOT NULL,
  `choose_animation` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '选择单位的动画',
  `end_animation` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '结束回合的动画',
  `dead_animation` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '死亡的动画',
  PRIMARY KEY (`unit_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for unit_template_relation
-- ----------------------------
DROP TABLE IF EXISTS `unit_template_relation`;
CREATE TABLE `unit_template_relation`  (
  `unit_id` int(11) NOT NULL COMMENT '单位ID',
  `temp_id` int(11) NOT NULL COMMENT '模板ID',
  `version` int(16) NULL DEFAULT NULL,
  PRIMARY KEY (`unit_id`, `temp_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for unit_transfer
-- ----------------------------
DROP TABLE IF EXISTS `unit_transfer`;
CREATE TABLE `unit_transfer`  (
  `unit_id` int(11) NOT NULL COMMENT '转职之前的单位',
  `transfer_unit_id` int(11) NOT NULL COMMENT '转职后的单位Id',
  `transfer_animation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '转职的动画',
  `order` tinyint(255) NULL DEFAULT NULL COMMENT '顺序',
  `use` tinyint(255) NULL DEFAULT NULL COMMENT '是否启动',
  PRIMARY KEY (`unit_id`, `transfer_unit_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for upload_file_log
-- ----------------------------
DROP TABLE IF EXISTS `upload_file_log`;
CREATE TABLE `upload_file_log`  (
  `file_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `file_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件真实名字',
  `temp_id` int(11) NULL DEFAULT NULL COMMENT '模板Id',
  `upload_user` int(255) NULL DEFAULT NULL COMMENT '上传人',
  `upload_time` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  `file_size` int(11) NULL DEFAULT NULL COMMENT '文件大小',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `icon` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号(小程序用户)',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '帐号启用状态：0->禁用；1->启用',
  `login_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录类型 1H5 2微信',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_join_room
-- ----------------------------
DROP TABLE IF EXISTS `user_join_room`;
CREATE TABLE `user_join_room`  (
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `room_id` varchar(4) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '房间号',
  `join_army` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `join_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log`  (
  `log_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `exec_time` int(11) NULL DEFAULT NULL,
  `service_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `invoke_method` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `trigger_user_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_map
-- ----------------------------
DROP TABLE IF EXISTS `user_map`;
CREATE TABLE `user_map`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `map_type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `version` int(16) NOT NULL,
  `template_id` int(12) NOT NULL,
  `units_string` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `region_string` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `map_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `map_row` tinyint(12) NULL DEFAULT NULL,
  `map_column` tinyint(12) NULL DEFAULT NULL,
  `create_user_id` int(12) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `un_save` tinyint(1) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `share` tinyint(255) NULL DEFAULT 1 COMMENT '是否共享',
  `status` tinyint(255) NULL DEFAULT NULL COMMENT '状态(删除 草稿 )',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_map_attention
-- ----------------------------
DROP TABLE IF EXISTS `user_map_attention`;
CREATE TABLE `user_map_attention`  (
  `map_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `map_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `map_start` tinyint(255) NULL DEFAULT NULL COMMENT '地图的评分',
  `map_comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论',
  `download_time` datetime(0) NULL DEFAULT NULL COMMENT '下载时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间(版本)',
  PRIMARY KEY (`map_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_map_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `user_map_operation_log`;
CREATE TABLE `user_map_operation_log`  (
  `log_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `request` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `trigger_user_id` int(11) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `exec_time` int(11) NULL DEFAULT NULL,
  `service_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `invoke_method` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`log_id`) USING BTREE
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

-- ----------------------------
-- Table structure for user_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `user_role_relation`;
CREATE TABLE `user_role_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户Id',
  `role_id` int(11) NULL DEFAULT 2 COMMENT '角色Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户和角色表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_setting
-- ----------------------------
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting`  (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `map_init_row` tinyint(3) NULL DEFAULT 15 COMMENT '创建地图的初始化行',
  `map_init_column` tinyint(3) NULL DEFAULT 15 COMMENT '创建地图的初始化列',
  `map_init_temp_id` int(11) NULL DEFAULT 40 COMMENT '初始化地图的RegionId',
  `simple_drawing` tinyint(1) NULL DEFAULT 1 COMMENT '是否开启优化绘图',
  `bg_music` tinyint(255) UNSIGNED NOT NULL COMMENT '声音的大小',
  `language` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '语言',
  `map_init_region_type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户个性化设置表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_temp_attention
-- ----------------------------
DROP TABLE IF EXISTS `user_temp_attention`;
CREATE TABLE `user_temp_attention`  (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `template_id` int(11) NOT NULL COMMENT '模板ID',
  `template_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板类型',
  `template_start` tinyint(255) NULL DEFAULT NULL COMMENT '评价',
  `template_comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论',
  `create_time` datetime(6) NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`user_id`, `template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_template
-- ----------------------------
DROP TABLE IF EXISTS `user_template`;
CREATE TABLE `user_template`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板类型相同版本类型相同',
  `user_id` int(11) NOT NULL COMMENT '所属用户',
  `template_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板名称',
  `template_desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板描述',
  `attach_experience` tinyint(255) NULL DEFAULT NULL COMMENT '攻击产生的经验',
  `counterattack_experience` tinyint(255) NULL DEFAULT NULL COMMENT '反击产生的经验',
  `kill_experience` tinyint(255) NULL DEFAULT NULL COMMENT '杀死敌军产生的经验',
  `antikill_experience` tinyint(255) NULL DEFAULT NULL COMMENT '反杀产生的经验',
  `attach_animation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '攻击的动画',
  `summon_animation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '召唤坟墓的动画',
  `dead_animation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位死亡的动画',
  `levelup_animation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '升级动画',
  `derivative_id` int(11) NULL DEFAULT NULL COMMENT '衍生物Id',
  `square_width` tinyint(3) UNSIGNED NULL DEFAULT 24 COMMENT '方块的宽度',
  `square_height` tinyint(4) NULL DEFAULT 24 COMMENT '方块的高度',
  `attach_model` tinyint(255) UNSIGNED NULL DEFAULT 2 COMMENT '攻击模式(1 固定 2 随机)',
  `unit_max_level` tinyint(255) NULL DEFAULT 4 COMMENT '单位的最大等级',
  `promotion_max_num` tinyint(4) NULL DEFAULT 5 COMMENT '可以晋升的最大数量',
  `promotion_level` tinyint(255) NULL DEFAULT 3 COMMENT '晋升的最小等级',
  `promotion_mode` tinyint(255) NULL DEFAULT 1 COMMENT '晋升的模式(1用户选择 2随机 3固定)',
  `shared` int(255) NULL DEFAULT 1 COMMENT '是否共享',
  `status` tinyint(2) NULL DEFAULT 1 COMMENT '状态',
  `version` int(16) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user_unit_attention
-- ----------------------------
DROP TABLE IF EXISTS `user_unit_attention`;
CREATE TABLE `user_unit_attention`  (
  `user_id` int(11) NOT NULL,
  `unit_id` int(11) NOT NULL,
  `unit_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unit_start` tinyint(255) NULL DEFAULT NULL,
  `unit_commend` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `download_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `unit_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
