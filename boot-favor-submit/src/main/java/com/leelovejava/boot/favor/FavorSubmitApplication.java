package com.leelovejava.boot.favor;

import com.leelovejava.boot.favor.service.CacheKeyGenerator;
import com.leelovejava.boot.favor.service.LockKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * @author Levin
 */
@SpringBootApplication
public class FavorSubmitApplication {

    public static void main(String[] args) {

        SpringApplication.run(FavorSubmitApplication.class, args);

    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }

}