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

	IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'game_room' AND column_name = 'map_config')
	THEN
	  alter table game_room add map_config varchar(1024);
	END IF;

	IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'user_setting' AND column_name = 'map_init_region_type')
	THEN
	  alter table user_setting add map_init_region_type varchar(16);
	END IF;

  -- 增加单位的version
	IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'unit_mes' AND column_name = 'version')
  	THEN
  	  alter table unit_mes add version int(16);
  END IF;

  -- 增加单位的version
  IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'user_template' AND column_name = 'version')
    THEN
      alter table user_template add version int(16);
  END IF;

  -- 增加单位的version
    IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'unit_template_relation' AND column_name = 'version')
      THEN
         alter table unit_template_relation add version int(16);
    END IF;

    -- 增加单位的version
    IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'user_map' AND column_name = 'version')
      THEN
         alter table user_map add version int(16);
    END IF;

    -- 增加单位的version
    IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'user_map' AND column_name = 'template_version')
      THEN
         alter table user_map add template_version int(16);
    END IF;

end $$
call executUpdateSql() $$
drop procedure IF EXISTS executUpdateSql $$
delimiter ;