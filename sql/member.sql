drop table if exists `member`;
CREATE TABLE `member` (
                          `id` bigint NOT NULL  COMMENT 'id',
                          `mobile` varchar(11)  COMMENT '手机号',
                          PRIMARY KEY (`id`),
    unique key `mobile_unique`(`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员';

drop table if exists `passenger`;
create table `passenger`(
    `id` bigint not null comment 'id',
    `member_id` bigint not null comment '会员id',
    `name` varchar(20) not null comment '姓名',
    `id_card` varchar(18) not null comment '身份证',
    `type` char(1) not null comment '旅客类型|枚举[PassengerTypeEnum]',
    `create_time` datetime(3) comment '新增时间',
    `update_time` datetime(3) comment '修改时间',
    primary key (`id`),
    index `member_id_index` (`member_id`)
) engine = innodb default charset = utf8mb4 comment='乘车人';

drop table if exists `ticket`;
create table `ticket`(
    `id` bigint not null comment 'id',
    `member_id` bigint not null comment '会员id',
    `passenger_id` bigint not null comment '乘客id',
    `passenger_name` varchar(20) comment '乘客姓名',
    `train_date` date not null comment '日期',
    `train_code` varchar(20) not null comment '车次编号',
    `carriage_index` int not null comment '厢序',
    `seat_row` varchar(20) NOT NULL COMMENT '排号|01, 02',
    `seat_col` varchar(20) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
    `start_station` varchar(20) NOT NULL COMMENT '出发站',
    `start_time` time NOT NULL COMMENT '出发时间',
    `end_station` varchar(20) NOT NULL COMMENT '到达站',
    `end_time` time NOT NULL COMMENT '到达时间',
    `seat_type` char(1) not null comment '座位类型|枚举[SeatTypeEnum]',
    `create_time` datetime(3) comment '新增时间',
    `update_time` datetime(3) comment '修改时间',
    primary key (`id`),
    index `member_id_index` (`member_id`)
)engine = innodb default charset = utf8mb4 comment='车票';

create table `undo_log`(
                           `id` bigint(20) not null auto_increment,
                           `branch_id` bigint(20) not null,
                           `xid` varchar(100) not null,
                           `context` varchar(128) not null,
                           `rollback_info` longblob not null,
                           `log_status` int(11) not null,
                           `log_created` datetime not null,
                           `log_modified` datetime not null,
                           `ext` varchar(100) default null,
                           primary key(`id`),
                           unique key `ux_undo_log`(`xid`,`branch_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AT事务回滚日志表';
