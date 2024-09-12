package com.fzq.tapibackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Define Redis serialization
 */
@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // Create RedisTemplate Obj
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // Set connection factory
        redisTemplate.setConnectionFactory(connectionFactory);
        //set key serializer
        redisTemplate.setKeySerializer(RedisSerializer.string());

        // Create Json Serializer
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //Set Value serializer
        redisTemplate.setValueSerializer(jsonRedisSerializer);

        return redisTemplate;
    }
}