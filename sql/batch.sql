drop table if exists `member`;
CREATE TABLE `member` (
                          `id` bigint NOT NULL  COMMENT 'id',
                          `mobile` varchar(11)  COMMENT '手机号',
                          PRIMARY KEY (`id`),
    unique key `mobile_unique`(`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员';

