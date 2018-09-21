package com.didispace.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt的配置配置信息类
 */
@ConfigurationProperties(prefix = "audience")
@Component
public class AudienceConfig {
    /**
     * 签发者id
     */
    private String clientId;
    /**
     * base64加密
     */
    private String base64Secret;
    /**
     * 签发者名称
     */
    private String name;
    /**
     * 过期时间 分钟
     */
    private int expiresSecond;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBase64Secret() {
        return base64Secret;
    }

    public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExpiresSecond() {
        return expiresSecond;
    }

    public void setExpiresSecond(int expiresSecond) {
        this.expiresSecond = expiresSecond;
    }
}
