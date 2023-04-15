package io.game.sq.web;

import io.game.sq.core.user.domain.User;
import io.game.sq.core.user.service.IUserService;
import io.game.sq.httpsrv.annotations.ApiMethod;
import io.game.sq.httpsrv.annotations.TokenType;
import io.game.sq.web.domain.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gw/router")
public class LoginAndRegController {
    @Resource
    private IUserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    @ApiMethod(TokenType.NO)
    @PostMapping(params = {"method=user.login", "v=1.0.0"})
    public Object login(String username, String password) {
        ApiResponse response = new ApiResponse();
        User data = userService.byObjectId(username);
        System.out.println(data);
        data = userService.byObjectId(username);

        redisTemplate.opsForValue().set("test", "test");
        Object v = redisTemplate.opsForValue().get("test");
        System.out.println(v);

        return response;
    }
}
