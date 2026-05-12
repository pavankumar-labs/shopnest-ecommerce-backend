package com.pavankumar.shopnestecommercebackend.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String PRODUCTS = "products";
    public static final String CATEGORIES = "categories";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {


        JacksonJsonRedisSerializer<Object> serializer =
                new JacksonJsonRedisSerializer<>(
                        new ObjectMapper(),
                        Object.class
                );


        RedisSerializationContext.SerializationPair<Object> serializerPair =
                RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(serializer);

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration
                        .defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeValuesWith(serializerPair);

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(PRODUCTS, defaultConfig);

        configs.put(CATEGORIES, defaultConfig
                .entryTtl(Duration.ofHours(1)));


        return RedisCacheManager
                .builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}

