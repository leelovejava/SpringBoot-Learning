package com.leelovejava.boot.statemachine.entity;

/**
 * 状态
 *
 * @author leelovejava
 */
public enum States {
    /**
     * 待支付
     */
    UNPAID,
    /**
     * 待收货
     */
    WAITING_FOR_RECEIVE,
    /**
     * 结束
     */
    DONE;
}