-- PageForge 基础数据库脚本
-- 适用版本：MySQL 5.7.40

CREATE DATABASE IF NOT EXISTS `page_forge`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `page_forge`;

-- 外包人员业务表
CREATE TABLE IF NOT EXISTS `biz_outsourced_staff` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `name` VARCHAR(64) NOT NULL COMMENT '姓名',
    `account_id` VARCHAR(64) NOT NULL COMMENT '账号 ID',
    `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1 启用，0 禁用',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者 ID',
    `modifier` VARCHAR(64) DEFAULT NULL COMMENT '修改者 ID',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '有效标识：1 有效，0 无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_biz_outsourced_staff_account_id` (`account_id`),
    KEY `idx_biz_outsourced_staff_name` (`name`)
) ENGINE = InnoDB COMMENT = '外包人员表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `role_code` VARCHAR(32) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '角色说明',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者 ID',
    `modifier` VARCHAR(64) DEFAULT NULL COMMENT '修改者 ID',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '有效标识：1 有效，0 无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE = InnoDB COMMENT = '系统角色表';

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
    `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密密码',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1 启用，0 禁用',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者 ID',
    `modifier` VARCHAR(64) DEFAULT NULL COMMENT '修改者 ID',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '有效标识：1 有效，0 无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE = InnoDB COMMENT = '系统用户表';

-- 用户角色关系表：一个用户可以关联多个角色
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色 ID',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者 ID',
    `modifier` VARCHAR(64) DEFAULT NULL COMMENT '修改者 ID',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '有效标识：1 有效，0 无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_role` (`user_id`, `role_id`),
    KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE = InnoDB COMMENT = '用户角色关系表';

-- 功能表：用于控制前端菜单和按钮
CREATE TABLE IF NOT EXISTS `sys_function` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `function_code` VARCHAR(128) NOT NULL COMMENT '功能编码',
    `function_name` VARCHAR(64) NOT NULL COMMENT '功能名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '功能说明',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者 ID',
    `modifier` VARCHAR(64) DEFAULT NULL COMMENT '修改者 ID',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '有效标识：1 有效，0 无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_function_code` (`function_code`)
) ENGINE = InnoDB COMMENT = '系统功能表';

-- 角色功能关系表
CREATE TABLE IF NOT EXISTS `sys_role_function` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色 ID',
    `function_id` BIGINT UNSIGNED NOT NULL COMMENT '功能 ID',
    `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者 ID',
    `modifier` VARCHAR(64) DEFAULT NULL COMMENT '修改者 ID',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '有效标识：1 有效，0 无效',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_role_function` (`role_id`, `function_id`),
    KEY `idx_sys_role_function_function_id` (`function_id`)
) ENGINE = InnoDB COMMENT = '角色功能关系表';

-- 初始化角色
INSERT INTO `sys_role` (`role_code`, `role_name`, `description`, `creator`)
VALUES
    ('ADMIN', '管理员', '拥有全部功能', 'system'),
    ('USER', '普通用户', '拥有普通用户功能', 'system')
ON DUPLICATE KEY UPDATE
    `role_name` = VALUES(`role_name`),
    `description` = VALUES(`description`),
    `modifier` = 'system',
    `is_valid` = 1;

-- 初始化基础功能
INSERT INTO `sys_function`
    (`function_code`, `function_name`, `description`, `creator`)
VALUES
    ('system:profile:view', '个人信息查看', '查看当前用户个人信息', 'system'),
    ('system:user:list', '用户权限查询', '查看用户及其角色', 'system'),
    ('system:user:assign-role', '用户角色分配', '给用户分配一个或多个角色', 'system'),
    ('system:role:list', '角色查询', '查看角色及其功能', 'system'),
    ('system:role:create', '角色新增', '新增系统角色', 'system'),
    ('system:role:update', '角色修改', '修改系统角色信息', 'system'),
    ('system:role:assign-function', '角色功能分配', '给角色分配功能', 'system'),
    ('system:function:list', '功能查询', '查看系统功能', 'system'),
    ('system:function:create', '功能新增', '新增菜单或按钮功能', 'system'),
    ('system:function:update', '功能修改', '修改菜单或按钮功能', 'system'),
    ('staff:outsourced:list', '外包人员查询', '查看外包人员列表和详情', 'system'),
    ('staff:outsourced:create', '外包人员新增', '新增外包人员', 'system'),
    ('staff:outsourced:update', '外包人员修改', '修改外包人员信息和状态', 'system'),
    ('staff:outsourced:delete', '外包人员删除', '删除外包人员', 'system'),
    ('staff:outsourced:import', '外包人员导入', '导入外包人员数据', 'system'),
    ('staff:outsourced:export', '外包人员导出', '导出外包人员数据', 'system')
ON DUPLICATE KEY UPDATE
    `function_name` = VALUES(`function_name`),
    `description` = VALUES(`description`),
    `modifier` = 'system',
    `is_valid` = 1;

-- 管理员关联全部功能
INSERT INTO `sys_role_function`
    (`role_id`, `function_id`, `creator`)
SELECT role_data.`id`, function_data.`id`, 'system'
FROM `sys_role` role_data
CROSS JOIN `sys_function` function_data
WHERE role_data.`role_code` = 'ADMIN'
ON DUPLICATE KEY UPDATE
    `modifier` = 'system',
    `is_valid` = 1;

-- 普通用户关联基础功能
INSERT INTO `sys_role_function`
    (`role_id`, `function_id`, `creator`)
SELECT role_data.`id`, function_data.`id`, 'system'
FROM `sys_role` role_data
CROSS JOIN `sys_function` function_data
WHERE role_data.`role_code` = 'USER'
  AND function_data.`function_code` = 'system:profile:view'
ON DUPLICATE KEY UPDATE
    `modifier` = 'system',
    `is_valid` = 1;

-- 管理员账号模板：请先生成 BCrypt 密文，再取消注释并替换密码
-- INSERT INTO `sys_user`
--     (`username`, `password`, `nickname`, `creator`)
-- VALUES (
--     'admin',
--     '<BCrypt 密文>',
--     '管理员',
--     'system'
-- );
--
-- INSERT INTO `sys_user_role` (`user_id`, `role_id`, `creator`)
-- SELECT user_data.`id`, role_data.`id`, 'system'
-- FROM `sys_user` user_data
-- CROSS JOIN `sys_role` role_data
-- WHERE user_data.`username` = 'admin'
--   AND role_data.`role_code` = 'ADMIN';
