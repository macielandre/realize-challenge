package com.example.realize.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public Object set(String key, Object value, long timeoutInMinutes) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeoutInMinutes, TimeUnit.MINUTES);
    }
}
