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

SELECT * FROM passenger WHERE name ='张三';

SELECT * FROM passenger WHERE id = 1913489326008832000;
