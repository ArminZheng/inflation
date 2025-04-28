package com.arminzheng.inflation.config;

import com.arminzheng.inflation.model.User;
import com.arminzheng.inflation.repository.UserRepository;
import com.github.javafaker.Faker;
import java.time.Duration;
import java.util.Locale;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableConfigurationProperties(InfluxDBProperties.class)
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));
            // check whether empty
            if (userRepository.count() == 0) {
                // generate fake 100 data
                for (int i = 0; i < 100; i++) {
                    User user = new User();
                    user.setName(faker.name().fullName());
                    user.setEmail(faker.internet().emailAddress());
                    userRepository.save(user);
                }
            }
        };
    }

    @Bean("redisCacheManager")
    RedisCacheManager cacheManager(
            ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
            RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(SerializationPair.fromSerializer(
                        new JdkSerializationRedisSerializer(resourceLoader.getClassLoader())));
        RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
                .enableStatistics()
                .cacheDefaults(config);
        redisCacheManagerBuilderCustomizers.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofSeconds(5));
        return builder -> builder.withCacheConfiguration("cache5s", cacheConfiguration);
    }

}
