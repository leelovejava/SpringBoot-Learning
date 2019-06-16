package com.leelovejava.boot.mysql.config;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

/**
 * 自定义分库算法
 */
public class DbShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    private static final String DB_NAME_PREFIX = "pay_";

    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<String> shardingValue) {
        // 每个商户的订单表独立且以商户名为后缀
        String targetTable = DB_NAME_PREFIX + shardingValue.getValue();
        if (availableTargetNames.contains(targetTable)) {
            return targetTable;
        }
        throw new UnsupportedOperationException("无法判定的值: " + shardingValue.getValue());
    }
}