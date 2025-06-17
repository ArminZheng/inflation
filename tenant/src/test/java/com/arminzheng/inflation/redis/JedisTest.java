package com.arminzheng.inflation.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class JedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {
        redisTemplate.opsForValue().set("hello", "world");
        redisTemplate.opsForHash().put("hash", "key", "value");
        redisTemplate.opsForList().leftPush("list", "key1", "value1");
        redisTemplate.opsForSet().add("set", "value1", "value2");
        redisTemplate.opsForZSet().add("zset", "value1", 0.1);
        // redisTemplate.opsForGeo().add("point1", new Point(1.0, 1.0), new Point(2.0, 2.0));
        // redisTemplate.opsForHyperLogLog().add("hyperloglog");
    }
}
