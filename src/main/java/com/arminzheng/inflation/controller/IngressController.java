package com.arminzheng.inflation.controller;

import java.util.Collection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngressController {

    @Autowired
    private ObjectProvider<CacheManager> cacheManager;

    @GetMapping("ping")
    public String ping() {
        cacheManager.orderedStream()
                .map(CacheManager::getCacheNames)
                .flatMap(Collection::stream).forEach(System.out::println);
        return "pong";
    }

}
