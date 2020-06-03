package com.leelovejava.boot.statemachine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 订单实体
 *
 * @author leelovejava
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	
	/**
	 * 订单ID
	 */
	private Integer id;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 订单收货地址
	 */
	private String address;
	
	/**
	 * 订单手机号
	 */
	private String phoneNum;
	
	/**
	 * 订单状态
	 */
	private String state;
}