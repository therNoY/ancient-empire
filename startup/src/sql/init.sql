CREATE TABLE game_room  (
  `room_id` varchar NOT NULL COMMENT '房间号',
  `map_id` varchar NULL COMMENT '选择的地图',
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