package io.game;

import io.game.config.AppConfig;
import io.game.httpsrv.HttpServer;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SqServer {
    @Setter
    private int port = 10220;

    public void startup() {
        // TODO


    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        HttpServer httpServer = context.getBean(HttpServer.class);
        httpServer.setPort(8080);
        httpServer.start();

        SqServer sqServer = context.getBean(SqServer.class);
        sqServer.startup();

        System.out.println("启动成功!");
    }
}