--liquibase formatted sql

--changeset short-url-service:1
CREATE TABLE `s_short_url` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `short_id` varchar(40) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '短链ID',
  `source_url_hash` char(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '原始链接的MD5 HASH',
  `source_url` varchar(500) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '跳转的原始链接',
  `is_delete` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否删除（0否，1是）',
  `gmt_modify` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shortid` (`short_id`) USING BTREE,
  KEY `idx_urlhash` (`source_url_hash`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='短链接数据表';