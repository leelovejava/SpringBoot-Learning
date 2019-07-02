package site.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 * @author www.exception.site(exception 教程网))
 * @date 2019/1/30
 * @time 11:11
 * @discription
 **/
@Configuration
@ConditionalOnClass(GirlFriendService.class)
@EnableConfigurationProperties(GirlFriendServiceProperties.class)
public class GirlFriendAutoConfiguration {

    /**
     * @Configuration: 标注类为一个配置类，让 spring 去扫描它；
     * @ConditionalOnClass：条件注解，只有在 classpath 路径下存在指定 class 文件时，才会实例化 Bean；
     * @EnableConfigurationProperties：使指定配置类生效；
     * @Bean: 创建一个实例类注入到 Spring Ioc 容器中；
     * @ConditionalOnMissingBean：条件注解，意思是，仅当 Ioc 容器不存在指定类型的 Bean 时，才会创建 Bean
     * @return
     */

    @Bean
    @ConditionalOnMissingBean
    public GirlFriendService girlFriendService() {
        return new GirlFriendServiceImpl();
    }

    /**
     * 新增 spring.factories 文件
     * 在 resources 目录下创建名为 META-INF 的目录，并新建文件 spring.factories
     * Spring Boot 会在启动时，自动会去查找指定文件 /META-INF/spring.factories，若有，就会根据配置的类的全路径去自动化配置
     */
}