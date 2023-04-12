package io.game;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class TomcatServer {

    public static ConfigurableApplicationContext run(Class config) {
        //创建annotation形式的spring web容器，继承自AbstractApplicationContext
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(config);
        applicationContext.refresh();//Spring创建容器的核心方法
        startTomcat(applicationContext);
        return applicationContext;
    }

    /**
     * Tomcat启动方法，主要都是添加tomcat启动所需的配置
     *
     * @param applicationContext
     */
    private static void startTomcat(AnnotationConfigWebApplicationContext applicationContext) {
        Tomcat tomcat = new Tomcat();
        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");
        Connector connector = new Connector();
        connector.setPort(8081);
        Engine engine = new StandardEngine();
        engine.setDefaultHost("localhost");
        Host host = new StandardHost();
        host.setName("localhost");
        String contextPath = "";
        Context context = new StandardContext();
        context.setPath(contextPath);
        context.addLifecycleListener(new Tomcat.FixContextListener());
        host.addChild(context);
        engine.addChild(host);
        service.setContainer(engine);
        service.addConnector(connector);
        //创建dispatcherServlet，并将spring容器bean添加到servlet中
        //配置servlet映射
        tomcat.addServlet(contextPath, "dispatcher","org.springframework.web.servlet.DispatcherServlet");
        context.addServletMappingDecoded("/*", "dispatcher");
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
