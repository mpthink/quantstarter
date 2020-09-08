/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : okexcandles

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 08/09/2020 19:17:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bch_candles_12h
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_12h`;
CREATE TABLE `bch_candles_12h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 12小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_15m
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_15m`;
CREATE TABLE `bch_candles_15m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 15分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_1d
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_1d`;
CREATE TABLE `bch_candles_1d`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 1天K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_1h
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_1h`;
CREATE TABLE `bch_candles_1h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 1小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_1m
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_1m`;
CREATE TABLE `bch_candles_1m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 1分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_1w
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_1w`;
CREATE TABLE `bch_candles_1w`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 1周K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_2h
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_2h`;
CREATE TABLE `bch_candles_2h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 2小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_30m
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_30m`;
CREATE TABLE `bch_candles_30m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 30分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_3m
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_3m`;
CREATE TABLE `bch_candles_3m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 3分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_4h
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_4h`;
CREATE TABLE `bch_candles_4h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 4小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_5m
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_5m`;
CREATE TABLE `bch_candles_5m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 5分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bch_candles_6h
-- ----------------------------
DROP TABLE IF EXISTS `bch_candles_6h`;
CREATE TABLE `bch_candles_6h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'bch 6小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_12h
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_12h`;
CREATE TABLE `btc_candles_12h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 12小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_15m
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_15m`;
CREATE TABLE `btc_candles_15m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 15分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_1d
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_1d`;
CREATE TABLE `btc_candles_1d`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 1天K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_1h
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_1h`;
CREATE TABLE `btc_candles_1h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 1小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_1m
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_1m`;
CREATE TABLE `btc_candles_1m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 1分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_1w
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_1w`;
CREATE TABLE `btc_candles_1w`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 1周K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_2h
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_2h`;
CREATE TABLE `btc_candles_2h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 2小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_30m
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_30m`;
CREATE TABLE `btc_candles_30m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 30分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_3m
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_3m`;
CREATE TABLE `btc_candles_3m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 3分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_4h
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_4h`;
CREATE TABLE `btc_candles_4h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 4小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_5m
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_5m`;
CREATE TABLE `btc_candles_5m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 5分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for btc_candles_6h
-- ----------------------------
DROP TABLE IF EXISTS `btc_candles_6h`;
CREATE TABLE `btc_candles_6h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'BTC 6小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_12h
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_12h`;
CREATE TABLE `eth_candles_12h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 12小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_15m
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_15m`;
CREATE TABLE `eth_candles_15m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 15分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_1d
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_1d`;
CREATE TABLE `eth_candles_1d`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 1天K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_1h
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_1h`;
CREATE TABLE `eth_candles_1h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 1小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_1m
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_1m`;
CREATE TABLE `eth_candles_1m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 1分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_1w
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_1w`;
CREATE TABLE `eth_candles_1w`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 1周K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_2h
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_2h`;
CREATE TABLE `eth_candles_2h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 2小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_30m
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_30m`;
CREATE TABLE `eth_candles_30m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 30分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_3m
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_3m`;
CREATE TABLE `eth_candles_3m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 3分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_4h
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_4h`;
CREATE TABLE `eth_candles_4h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 4小时K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_5m
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_5m`;
CREATE TABLE `eth_candles_5m`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 5分钟K线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_candles_6h
-- ----------------------------
DROP TABLE IF EXISTS `eth_candles_6h`;
CREATE TABLE `eth_candles_6h`  (
  `candle_time` char(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统时间',
  `open` double NOT NULL COMMENT '开盘价格',
  `high` double NOT NULL COMMENT '最高价格',
  `low` double NOT NULL COMMENT '最低价格',
  `close` double NOT NULL COMMENT '收盘价格',
  `volume` double NOT NULL COMMENT '交易量（按张折算）',
  `currency_volume` double NOT NULL COMMENT '交易量（按币折算）',
  `gmt_create` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`candle_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'eth 6小时K线表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
