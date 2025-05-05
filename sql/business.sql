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
    `start_time` varchar(20) NOT NULL COMMENT '出发时间',
    `end` varchar(20) NOT NULL COMMENT '终点站',
    `end_pinyin` varchar(50) NOT NULL COMMENT '终点站拼音',
    `end_time` varchar(20) NOT NULL COMMENT '到站时间',
    `create_time` datetime(3) COMMENT '新增时间',
    `update_time` datetime(3) COMMENT '修改时间',
    PRIMARY KEY (`id`),
    unique key `code_unique`(`code`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车次';