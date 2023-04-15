package io.game.sq.core.user.service.impl;

import io.game.sq.core.user.domain.User;
import io.game.sq.core.user.service.IUserService;
import io.game.sq.dao.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "User")
@Service("userService")
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Cacheable(key = "'user' + #id", unless = "#result == null")
    public User byObjectId(String id) {
        System.out.println("查询数据库");
        return userMapper.selectById(id);
    }
}
