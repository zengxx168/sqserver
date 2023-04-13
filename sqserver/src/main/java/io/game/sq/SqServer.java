package io.game.sq;

import io.game.sq.bootstrap.RemotingServer;
import io.game.sq.bootstrap.netty.NettyRemotingServer;
import io.game.sq.bootstrap.processors.ConnectProcessor;
import io.game.sq.bootstrap.processors.PingreqProcessor;
import io.game.sq.bootstrap.processors.PublishProcessor;
import io.game.sq.bootstrap.sessions.SessionsManager;
import io.game.sq.config.AppConfig;
import io.game.sq.httpsrv.HttpServer;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SqServer {
    @Setter
    private int port = 22020;
    private RemotingServer remotingServer;

    @Resource
    private SessionsManager sessionManager;
    @Resource
    private ConnectProcessor connectProcessor;
    @Resource
    private PublishProcessor publishProcessor;
    @Resource
    private PingreqProcessor pingreqProcessor;

    public void startup() {
        remotingServer = new NettyRemotingServer(port);
        remotingServer.setSessionManager(sessionManager);
        remotingServer.start();

        //注册处理器
        this.registerProcessor();
    }

    private void registerProcessor() {
        remotingServer.registerProcessor(0, publishProcessor);
        remotingServer.registerProcessor(100001, connectProcessor);
        remotingServer.registerProcessor(1003, pingreqProcessor);
    }

    @PreDestroy
    public void stopServer() {
        remotingServer.shutdown();
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        //启动http服务
        HttpServer httpServer = context.getBean(HttpServer.class);
        httpServer.setPort(8080);
        httpServer.start();

        //启动broker Socket服务
        SqServer sqServer = context.getBean(SqServer.class);
        sqServer.startup();
        System.out.println("启动成功.........");
    }
}