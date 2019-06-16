package com.leelovejava.boot.mysql.model;

import java.util.Date;

/**
 * 用户对象
 * @author tianhao
 */
public class OrderModel implements java.io.Serializable{
    /**
     * 用户编号
     */
    private int userId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单时间
     */
    private Date orderTime;
    /**
     * 商户名称
     */
    private String merchant;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
}
