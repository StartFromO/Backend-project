package com.example.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 设置缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis连接失败，不影响业务逻辑
            System.out.println("Redis set error: " + e.getMessage());
        }
    }
    
    /**
     * 获取缓存
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // Redis连接失败，返回null
            System.out.println("Redis get error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 删除缓存
     * @param key 缓存键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            // Redis连接失败，不影响业务逻辑
            System.out.println("Redis delete error: " + e.getMessage());
        }
    }
    
    /**
     * 检查缓存是否存在
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            // Redis连接失败，返回false
            System.out.println("Redis exists error: " + e.getMessage());
            return false;
        }
    }
    /**
     * 设置缓存过期时间
     * @param key 缓存键
     * @param timeout 过期时间（秒）
     */
    public void expire(String key, long timeout) {
        try {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis连接失败，不影响业务逻辑
            System.out.println("Redis expire error: " + e.getMessage());
        }
    }
}
