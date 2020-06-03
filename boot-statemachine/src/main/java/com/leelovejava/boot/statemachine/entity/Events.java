package com.leelovejava.boot.statemachine.entity;

/**
 * 事件
 *
 * @author leelovejava
 */
public enum Events {
    /**
     * 支付
     * 支付事件PAY会触发状态从待支付UNPAID状态到待收货WAITING_FOR_RECEIVE状态的迁移
     */
    PAY,
    /**
     * 收货
     * 收货事件RECEIVE会触发状态从待收货WAITING_FOR_RECEIVE状态到结束DONE状态的迁移
     */
    RECEIVE
}