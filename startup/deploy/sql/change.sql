delimiter $$
drop procedure IF EXISTS executUpdateSql $$
create procedure executUpdateSql()
begin
	IF not exists(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE()  AND table_name = 'game_room' AND column_name = 'game_type')
	THEN
	  alter table game_room add game_type tinyint(64);
	END IF;
end $$
call executUpdateSql() $$
drop procedure IF EXISTS executUpdateSql $$
delimiter ;