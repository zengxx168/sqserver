package io.game;

import io.game.core.user.service.IUserService;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Resource;

@PrincipleSpringBootApplication(scanBasePackages = "io.game")
public class SqServer {
    private static String basePackage = "io.game";

    private static int port = 8080;
    private static final String CONTEXT_PATH = "/";


    @Bean("dispatcherServlet")
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        //提供可供自定义编辑的dispatcherServlet，此处可自行添加其他逻辑
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        return dispatcherServlet;
    }

    public void startup() {
        // TODO


    }

    public static SqServer newBuilder() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(basePackage);
        return applicationContext.getBean(SqServer.class);
    }

    public static void main(String[] args) {
//        SqServer sqServer = SqServer.newBuilder();
//        sqServer.startup();

        TomcatServer.run(SqServer.class);

        System.out.println("启动成功!");
    }
}