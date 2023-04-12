package io.game.web;

import io.game.core.user.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/gw/router")
public class LoginAndRegController {
    @Resource
    private IUserService userService;

    @GetMapping(params = "method=user.login")
    public String login() {

        String data = userService.byObjectId(9L);
        System.out.println(data);
        data = userService.byObjectId(9L);

        System.out.println(data);

        return "login";
    }
}
