package com.zhss.demo.zuul.gateway.release;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 灰度配置管理类
 *
 * @author leelovejava
 */
@Component
@Configuration
@EnableScheduling
public class GrayReleaseConfigManager {

    private Map<String, GrayReleaseConfig> grayReleaseConfigs =
            new ConcurrentHashMap<String, GrayReleaseConfig>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 1000)
    private void refreshRoute() {
        List<GrayReleaseConfig> results = jdbcTemplate.query(
                "select * from gray_release_config",
                new BeanPropertyRowMapper<>(GrayReleaseConfig.class));

        for (GrayReleaseConfig grayReleaseConfig : results) {
            grayReleaseConfigs.put(grayReleaseConfig.getPath(), grayReleaseConfig);
        }
    }

    public Map<String, GrayReleaseConfig> getGrayReleaseConfigs() {
        return grayReleaseConfigs;
    }

}
