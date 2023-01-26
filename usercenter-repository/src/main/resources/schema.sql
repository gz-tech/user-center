-- ----------------------------
-- Table structure for UserInfo
-- ----------------------------

DROP TABLE IF EXISTS `user_base_info`;
CREATE TABLE `cloud-user`.`user_base_info`
(
    `id`                    BIGINT       NOT NULL COMMENT 'Id',
    `user_name`             VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'User name',
    `phone_number`          VARCHAR(45)  NOT NULL DEFAULT '' COMMENT 'Phone number',
    `security_phone_number` VARCHAR(45)  NOT NULL DEFAULT '' COMMENT 'Security phone number',
    `gender`                TINYINT      NOT NULL DEFAULT 0 COMMENT '0 male 1 famale',
    `country_code`          VARCHAR(45)  NOT NULL DEFAULT '' COMMENT 'Country code',
    `country_name`          VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Country name',
    `del_flag`              TINYINT      NOT NULL DEFAULT 0 COMMENT '0 valid 1 deleted',
    `create_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create_time',
    `update_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    PRIMARY KEY (`id`)
);
ALTER TABLE `cloud-user`.`user_base_info`
    ADD COLUMN `password` VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Password' AFTER `security_phone_number`;


-- 4月7号-fat
ALTER TABLE `cloud-user`.`oauth_refresh_token`
    ADD COLUMN `device_name` VARCHAR(255) NOT NULL AFTER `model`,
    ADD COLUMN `device_type` TINYINT      NOT NULL DEFAULT -1 COMMENT '设备类型0-PC, 1-手机,2-平板,3-移动web,4-car,5-vr,-1=未知' AFTER `device_name`;

-- 4月11日-dev refreshToken 4096
alter table `cloud-user`.`oauth_refresh_token`
    modify column refresh_token varchar (4096);

-- 4月19日-dev
ALTER TABLE `cloud-user`.`user_third_party_account`
    ADD INDEX `idx_userid` (`user_id` ASC) VISIBLE;

-- 4月28日-dev
ALTER TABLE `cloud-user`.`oauth_client_detail`
    ADD COLUMN `code_validity` int NOT NULL DEFAULT 300 COMMENT '授权码有效时间（秒）' AFTER `refresh_token_validity`;
