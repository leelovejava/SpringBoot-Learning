package com.leelovejava.boot.neo4j.config;

import cn.hutool.core.util.IdUtil;
import org.neo4j.ogm.id.IdStrategy;

/**
 * 自定义主键策略
 *
 * @author leelovejava
 * @date 2020/11/5 17:41
 **/
public class CustomIdStrategy implements IdStrategy {
    @Override
    public Object generateId(Object o) {
        return IdUtil.fastUUID();
    }
}
