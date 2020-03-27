package com.didispace;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring Boot Configuration Annotation Processor not configured 问题解决
 * https://blog.csdn.net/liangjiabao5555/article/details/104062932
 * @author leelovejava
 */
@Data
@ConfigurationProperties(prefix = "com.didispace")
public class FooProperties {

    private String foo;

    private String databasePlatform;

}