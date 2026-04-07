package com.example.login.controller;

import com.example.login.service.CacheService;
import com.example.login.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/redis")
public class RedisController {
    
    @Autowired
    private CacheService cacheService;
    
    /**
     * 查询Redis中的缓存信息
     * @param key 缓存键
     * @return 缓存信息
     */
    @GetMapping("/info")
    public ResponseDTO getRedisInfo(@RequestParam(required = false) String key) {
        Map<String, Object> result = new HashMap<>();
        
        if (key != null && !key.isEmpty()) {
            // 查询指定键的缓存
            Object value = cacheService.get(key);
            result.put("key", key);
            result.put("value", value);
            result.put("exists", cacheService.exists(key));
        } else {
            // 返回Redis连接状态
            result.put("status", "Redis连接正常");
            result.put("message", "请使用key参数指定要查询的缓存键");
        }
        
        return ResponseDTO.success(result);
    }
    
    /**
     * 清除指定键的缓存
     * @param key 缓存键
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public ResponseDTO clearRedisCache(@RequestParam String key) {
        cacheService.delete(key);
        return ResponseDTO.success("缓存已清除");
    }
    
    /**
     * 清除所有缓存
     * @return 操作结果
     */
    @DeleteMapping("/clear-all")
    public ResponseDTO clearAllRedisCache() {
        // 清除常用的缓存键
        String[] cacheKeys = {
            "products:list",
            "categories:list",
            "cart:user1",
            "cart:testuser"
        };
        
        for (String key : cacheKeys) {
            cacheService.delete(key);
        }
        
        return ResponseDTO.success("缓存已全部清除");
    }
}
