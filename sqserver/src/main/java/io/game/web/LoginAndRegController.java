package io.game.web;

import io.game.core.user.service.IUserService;
import io.game.web.domain.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gw/router")
public class LoginAndRegController {
    @Resource
    private IUserService userService;

    @PostMapping(params = {"method=user.login", "v=1.0.0"})
    public Object login(String username, String password) {
        ApiResponse response = new ApiResponse();
        String data = userService.byObjectId(9L);
        System.out.println(data);
        data = userService.byObjectId(9L);

        System.out.println(data);

        return response;
    }
}
