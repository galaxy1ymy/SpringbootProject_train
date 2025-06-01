drop table if exists `station`;
CREATE TABLE `station` (
    `id` bigint NOT NULL  COMMENT 'id',
    `name` varchar(20) NOT NULL COMMENT '站名',
    `name_pinyin` varchar(50) NOT NULL COMMENT '站名拼音',
    `name_py` varchar(50) NOT NULL COMMENT '站名拼音首字母',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    PRIMARY KEY (`id`),
    unique key `name_unique`(`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站';


drop table if exists `train`;
CREATE TABLE `train` (
    `id` bigint NOT NULL  COMMENT 'id',
    `code` varchar(20) NOT NULL COMMENT '车次编号',
    `type` char(1) NOT NULL COMMENT '车次类型|枚举[TrainTypeEnum]',
    `start` varchar(20) NOT NULL COMMENT '始发站',
    `start_pinyin` varchar(50) NOT NULL COMMENT '始发站拼音',
    `start_time` time NOT NULL COMMENT '出发时间',
    `end` varchar(20) NOT NULL COMMENT '终点站',
    `end_pinyin` varchar(50) NOT NULL COMMENT '终点站拼音',
    `end_time` time NOT NULL COMMENT '到站时间',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    PRIMARY KEY (`id`),
    unique key `code_unique`(`code`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次';


drop table if exists `train_station`;
CREATE TABLE `train_station` (
    `id` bigint NOT NULL  COMMENT 'id',
    `train_code` varchar(20) NOT NULL COMMENT '车次编号',
    `index` int NOT NULL COMMENT '站序',
    `name` varchar(20) NOT NULL COMMENT '站名',
    `name_pinyin` varchar(50) NOT NULL COMMENT '站名拼音',
    `in_time` time comment '进站时间',
    `out_time` time comment '出站时间',
    `stop_time` time COMMENT '停站时长',
    `km` decimal(8, 2) NOT NULL COMMENT '里程（公里）|从上一站到本站距离',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    primary key (`id`),
    unique key `train_code_index_unique` (`train_code`, `index`),
    unique key `train_code_name_unique` (`train_code`, `name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='火车车站';


drop table if exists `train_carriage`;
CREATE TABLE `train_carriage` (
    `id` bigint NOT NULL  COMMENT 'id',
    `train_code` varchar(20) NOT NULL COMMENT '车次编号',
    `index` int NOT NULL COMMENT '厢号',
    `seat_type` char(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `seat_count` int NOT NULL COMMENT '座位数',
    `row_count` int NOT NULL COMMENT '排数',
    `col_count` int NOT NULL COMMENT '列数',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    primary key (`id`),
    unique key `train_code_index_unique` (`train_code`, `index`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='火车车厢';

drop table if exists `train_seat`;
CREATE TABLE `train_seat` (
    `id` bigint NOT NULL  COMMENT 'id',
    `train_code` varchar(20) NOT NULL COMMENT '车次编号',
    `carriage_index` int NOT NULL COMMENT '厢序',
    `row` varchar(20) NOT NULL COMMENT '排号|01, 02',
    `col` varchar(20) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
    `seat_type` varchar(20) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `carriage_seat_index` int NOT NULL COMMENT '同车厢座序',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    primary key (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位';

drop table if exists `daily_train`;
CREATE TABLE `daily_train` (
    `id` bigint NOT NULL  COMMENT 'id',
    `date` date NOT NULL COMMENT '日期',
    `code` varchar(20) NOT NULL COMMENT '车次编号',
    `type` char(1) NOT NULL COMMENT '车次类型|枚举[TrainTypeEnum]',
    `start` varchar(20) NOT NULL COMMENT '始发站',
    `start_pinyin` varchar(50) NOT NULL COMMENT '始发站拼音',
    `start_time` time NOT NULL COMMENT '出发时间',
    `end` varchar(20) NOT NULL COMMENT '终点站',
    `end_pinyin` varchar(50) NOT NULL COMMENT '终点站拼音',
    `end_time` time NOT NULL COMMENT '到站时间',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    PRIMARY KEY (`id`),
    unique key `date_code_unique`(`date`,`code`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日车次';

drop table if exists `daily_train_station`;
CREATE TABLE `daily_train_station` (
     `id` bigint NOT NULL  COMMENT 'id',
     `date` date NOT NULL COMMENT '日期',
     `train_code` varchar(20) NOT NULL COMMENT '车次编号',
     `index` int NOT NULL COMMENT '站序',
     `name` varchar(20) NOT NULL COMMENT '站名',
     `name_pinyin` varchar(50) NOT NULL COMMENT '站名拼音',
     `in_time` time comment '进站时间',
     `out_time` time comment '出站时间',
     `stop_time` time COMMENT '停站时长',
     `km` decimal(8, 2) NOT NULL COMMENT '里程（公里）|从上一站到本站距离',
     `create_time` datetime(3) COMMENT '新增时间',
     `update_time` datetime(3) COMMENT '修改时间',
     primary key (`id`),
     unique key `date_train_code_index_unique` (`date`,`train_code`, `index`),
     unique key `date_train_code_name_unique` (`date`,`train_code`, `name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日车站';

drop table if exists `daily_train_carriage`;
CREATE TABLE `daily_train_carriage` (
       `id` bigint NOT NULL  COMMENT 'id',
       `date` date NOT NULL COMMENT '日期',
       `train_code` varchar(20) NOT NULL COMMENT '车次编号',
       `index` int NOT NULL COMMENT '厢号',
       `seat_type` char(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
       `seat_count` int NOT NULL COMMENT '座位数',
       `row_count` int NOT NULL COMMENT '排数',
       `col_count` int NOT NULL COMMENT '列数',
       `create_time` datetime(3) COMMENT '新增时间',
       `update_time` datetime(3) COMMENT '修改时间',
        primary key (`id`),
        unique key `date_train_code_index_unique` (`date`,`train_code`, `index`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日车厢';

drop table if exists `daily_train_seat`;
CREATE TABLE `daily_train_seat` (
       `id` bigint NOT NULL  COMMENT 'id',
       `date` date NOT NULL COMMENT '日期',
       `train_code` varchar(20) NOT NULL COMMENT '车次编号',
       `carriage_index` int NOT NULL COMMENT '厢序',
       `row` varchar(20) NOT NULL COMMENT '排号|01, 02',
       `col` varchar(20) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
       `seat_type` varchar(20) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
       `carriage_seat_index` int NOT NULL COMMENT '同车厢座序',
        `sell` varchar(50) NOT NULL COMMENT '售卖情况|将经过的车站用01拼接，0表示可卖、1表示不可卖',
       `create_time` datetime(3) COMMENT '新增时间',
       `update_time` datetime(3) COMMENT '修改时间',
        primary key (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日座位';

drop table if exists `daily_train_ticket`;
CREATE TABLE `daily_train_ticket` (
    `id` bigint NOT NULL  COMMENT 'id',
    `date` date NOT NULL COMMENT '日期',
    `train_code` varchar(20) NOT NULL COMMENT '车次编号',
    `start` varchar(20) NOT NULL COMMENT '出发站',
    `start_pinyin` varchar(50) NOT NULL COMMENT '出发站拼音',
    `start_time` time NOT NULL COMMENT '出发时间',
    `start_index` int NOT NULL COMMENT '出发站序|本站是整个车次的第几站',
    `end` varchar(20) NOT NULL COMMENT '到达站',
    `end_pinyin` varchar(50) NOT NULL COMMENT '到达站拼音',
    `end_time` time NOT NULL COMMENT '到达时间',
    `end_index` int NOT NULL COMMENT '到达站序|本站是整个车次的第几站',
    `ydz` int not null comment '一等座余票',
    `ydz_price` decimal(8, 2) not null comment '一等座票价',
    `edz` int not null comment '二等座余票',
    `edz_price` decimal(8, 2) not null comment '二等座票价',
    `rw` int not null comment '软卧余票',
    `rw_price` decimal(8, 2) not null comment '软卧票价',
    `yw` int not null comment '硬卧余票',
    `yw_price` decimal(8, 2) not null comment '硬卧票价',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    primary key (`id`),
    unique key `date_train_code_start_end_unique` (`date`,`train_code`,`start`,`end`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余票信息';

drop table if exists `confirm_order`;
 CREATE TABLE `confirm_order` (
    `id` bigint NOT NULL  COMMENT 'id',
    `member_id` bigint NOT NULL COMMENT '会员id',
    `date` date NOT NULL COMMENT '日期',
    `train_code` varchar(20) NOT NULL COMMENT '车次编号',
    `start` varchar(20) NOT NULL COMMENT '出发站',
    `end` varchar(20) NOT NULL COMMENT '到达站',
    `daily_train_ticket_id` bigint NOT NULL COMMENT '余票ID',
    `tickets` json not null comment '车票',
    `status` char(1) NOT NULL COMMENT '订单状态|枚举[ConfirmOrderStatusEnum]',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    primary key (`id`),
     index `date_train_code_index` (`date`, `train_code`)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='确认订单';