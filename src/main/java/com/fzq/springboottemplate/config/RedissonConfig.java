//package com.fzq.springboottemplate.config;
//
//import lombok.Data;
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//// remove this config if not use，spring would try to inject
///**
// * Redisson config
// */
//@Configuration
//@ConfigurationProperties(prefix = "spring.data.redis")
//@Data
//public class RedissonConfig {
//
//    private String host;
//
//    private String port;
//
//    @Bean
//    public RedissonClient redissonClient() {
//        // 1. Create config object
//        Config config = new Config();
//        String redisAddress = String.format("redis://%s:%s", host, port);
//        config.useSingleServer().setAddress(redisAddress).setDatabase(3);
//        // use "rediss://" for SSL connection
//
//        // 2. Create Redisson instance
//
//        // Sync and Async API
//        RedissonClient redisson = Redisson.create(config);
//        return redisson;
//    }
//}
