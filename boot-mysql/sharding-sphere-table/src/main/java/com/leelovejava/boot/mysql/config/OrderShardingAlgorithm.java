package com.leelovejava.boot.mysql.config;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

/**
 * 自定义分表算法
 */
public class OrderShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<String> shardingValue) {
        String targetTable = "tb_order_" + shardingValue.getValue();
        if (availableTargetNames.contains(targetTable)){
            return targetTable;
        }

        throw new UnsupportedOperationException("无法判定的值: " + shardingValue.getValue());
    }
}