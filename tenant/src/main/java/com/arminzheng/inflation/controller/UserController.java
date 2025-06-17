package com.arminzheng.inflation.controller;

import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.UserPO;
import com.arminzheng.inflation.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.shaded.com.google.common.collect.Lists;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final ObjectProvider<UserService> userService;
    private final ObjectProvider<CacheManager> cacheManagers;

    @GetMapping("all")
    @Cacheable(value = "cache5s", cacheManager = "redisCacheManager", key = "'allUsers'")
    public List<UserDTO> allUsers() {
        return userService.orderedStream()
                .map(UserService::allUsers).filter(e -> e != null && !e.isEmpty())
                .flatMap(List::stream).collect(Collectors.toList());
    }
    @GetMapping("find/{id}")
    public UserPO findById(@PathVariable Long id) {
        return userService.orderedStream().findFirst().get().findById(id);
    }
    @GetMapping("test")
    public List<UserPO> test() {
        return userService.orderedStream().findFirst().get().test();
    }

    @GetMapping("statistics")
    public List<Map<String, Long>> statistics() {
        List<Map<String, Long>> statistics = Lists.newArrayList();
        cacheManagers.orderedStream().forEach(
                cacheManager -> {
                    Cache cache = cacheManager.getCache("cache5s");
                    if (!(cache instanceof RedisCache redisCache)) {
                        return;
                    }
                    // 获取统计信息
                    long hitCount = redisCache.getStatistics().getHits();     // 命中次数
                    long missCount = redisCache.getStatistics().getMisses();  // 未命中次数
                    long puts = redisCache.getStatistics().getPuts();         // 写入次数
                    long gets = redisCache.getStatistics().getGets(); // 读取次数 (hits + misses)
                    long deletes = redisCache.getStatistics().getDeletes();   // 删除次数
                    log.info("Cache Stats - Hits: {}, Misses: {}, Puts: {}, Gets: {}, Deletes: {}",
                            hitCount, missCount, puts, gets, deletes);
                    Map<String, Long> map = Map.of("hitCount", hitCount, "missCount", missCount,
                            "puts", puts, "gets", gets, "deletes", deletes);
                    statistics.add(map);
                }
        );
        return statistics;
    }
}
