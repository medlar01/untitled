/*
 Navicat Premium Data Transfer

 Source Server         : ams[dev]
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : 192.168.16.178:3306
 Source Schema         : seata

 Target Server Type    : MySQL
 Target Server Version : 50799
 File Encoding         : 65001

 Date: 09/08/2021 17:44:31
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_card
-- ----------------------------
DROP TABLE IF EXISTS `t_card`;
CREATE TABLE `t_card`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `amount` decimal(25, 4) NULL DEFAULT 0.0000 COMMENT '金额',
  `user_id` int(20) NULL DEFAULT NULL COMMENT '用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------
-- Records of t_card
-- ----------------------------
BEGIN;
INSERT INTO `t_card` VALUES (1, 70.0000, 1), (2, 130.0000, 2);
COMMIT;

-- ----------------------------
-- Table structure for t_record
-- ----------------------------
DROP TABLE IF EXISTS `t_record`;
CREATE TABLE `t_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `amount` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '金额',
  `from` int(20) NULL DEFAULT NULL COMMENT '扣款人',
  `to` int(20) NULL DEFAULT NULL COMMENT '收款人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;


-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` VALUES (1, '李白'), (2, '屈原');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
