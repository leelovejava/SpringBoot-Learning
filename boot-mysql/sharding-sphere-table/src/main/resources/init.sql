CREATE TABLE tb_order (
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id int(11) NOT NULL COMMENT '姓名',
  order_no varchar(32) NOT NULL COMMENT '订单号',
  order_time datetime NOT NULL COMMENT '订单时间',
  merchant varchar(16) NOT NULL COMMENT '商户名称',
  UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;