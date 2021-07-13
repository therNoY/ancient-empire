CREATE TABLE user_map_operation_log  (
  `log_id` varchar(32) NOT NULL,
  `request` varchar(512) NOT NULL,
  `trigger_user_id` int NULL,
  `create_time` datetime NULL,
  `exec_time` int NULL,
  `service_name` varchar(255) NULL,
  `invoke_method` varchar(32) NULL,
  PRIMARY KEY (`log_id`)
);