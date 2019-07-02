package com.alibaba.easyexcel.test.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.time.LocalDateTime;

/**
 * @author jiangbing(江冰)
 * @date 2019/5/9
 * @time 下午2:07
 * @discription 写入Excel模型对象
 **/
public class WriteModel2 extends BaseRowModel {

    @ExcelProperty(value = "订单号", index = 0)
    private String orderNo;

    @ExcelProperty(value = "创建人", index = 1)
    private String name;

    @ExcelProperty(value = "创建时间", index = 2)
    private LocalDateTime createTime;


    public WriteModel2() {
    }

    public WriteModel2(String orderNo, String name, LocalDateTime createTime) {
        this.orderNo = orderNo;
        this.name = name;
        this.createTime = createTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}