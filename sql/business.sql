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
    `row` char(2) NOT NULL COMMENT '排号|01, 02',
    `col` char(1) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
    `seat_type` char(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `carriage_seat_index` int NOT NULL COMMENT '同车厢座位号',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    primary key (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位';
