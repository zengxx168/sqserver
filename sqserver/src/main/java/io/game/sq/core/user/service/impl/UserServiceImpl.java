package io.game.sq.core.user.service.impl;

import io.game.sq.core.user.service.IUserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "User")
@Service("userService")
public class UserServiceImpl implements IUserService {


    @Override
    @Cacheable(key = "'user' + #id", unless = "#result == null")
    public String byObjectId(long id) {
        System.out.println("查询数据库");
        return "ok";
    }
}
