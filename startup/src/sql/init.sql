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

alter table add


CREATE TABLE `user_join_room`  (
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `room_id` varchar(4) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '房间号',
  PRIMARY KEY (`user_id`) USING BTREE
);


delimiter $$
drop procedure IF EXISTS executUpdateSql $$
create procedure executUpdateSql()
begin
	IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'game_room' AND column_name = 'game_type')
	THEN
	  alter table game_room add game_type tinyint(64);
	END IF;

	IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'game_room' AND column_name = 'ob_enable')
	THEN
	  alter table game_room add ob_enable tinyint(64);
	END IF;

end $$
call executUpdateSql() $$
drop procedure IF EXISTS executUpdateSql $$
delimiter ;
