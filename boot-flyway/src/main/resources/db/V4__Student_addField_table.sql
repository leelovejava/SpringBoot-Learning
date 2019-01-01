alter table student add store_num int(11) null COMMENT '到店次数';
alter table student add last_store_date datetime null COMMENT '上次到店时间';
alter table student add enter_images_url VARCHAR(128) null COMMENT '入库照片';