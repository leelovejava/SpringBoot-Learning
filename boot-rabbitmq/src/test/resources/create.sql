-- 派件表
CREATE TABLE `platoon`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orderId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '订单id',
  `takeout_userId` int(11) DEFAULT NULL COMMENT '配送员id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '派件表' ROW_FORMAT = Dynamic;

-- 订单表
CREATE TABLE `order_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '消费者名称',
  `order_money` int(11) DEFAULT NULL COMMENT '订单金额',
  `orderId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '订单id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;