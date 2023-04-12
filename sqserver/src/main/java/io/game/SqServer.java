package io.game;

import io.game.core.user.service.IUserService;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("sqServer")
public class SqServer {
    private static String basePackage = "io.game";

    @Setter
    private int port = 22020;

    @Resource
    private IUserService userService;

    public void startup() {
        // TODO

        String data = userService.byObjectId(9L);
        System.out.println(data);
        data = userService.byObjectId(9L);

        System.out.println(data);
    }

    public static SqServer newBuilder() {
        ApplicationContext context = new AnnotationConfigApplicationContext(basePackage);
        return context.getBean(SqServer.class);
    }

    public static void main(String[] args) {
        int port = 22020;
        SqServer sqServer = SqServer.newBuilder();
        sqServer.setPort(port);
        sqServer.startup();
        System.out.println("启动成功!");
    }
}