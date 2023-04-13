/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.sq.bootstrap.processors;

import io.game.sq.bootstrap.ApiMethod;
import io.game.sq.bootstrap.annotation.Api;
import io.game.sq.bootstrap.netty.NettyRequestProcessor;
import io.game.sq.bootstrap.protocols.Command;
import io.game.sq.bootstrap.protocols.FloatMsg;
import io.game.sq.bootstrap.serializer.DataCodecKit;
import io.game.sq.bootstrap.serializer.DataCodecKit2;
import io.game.sq.bootstrap.sessions.SessionsManager;
import io.game.sq.bootstrap.sessions.domain.Session;
import io.game.sq.bootstrap.sessions.domain.SessionAttr;
import io.game.sq.bootstrap.sessions.mgt.ThreadContext;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.jctools.maps.NonBlockingHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Administrator
 * @version $Id: PublishProcessor.java 2021年6月15日 下午2:40:01 $
 */
@Component("publishProcessor")
public class PublishProcessor implements NettyRequestProcessor, BeanPostProcessor {
    private final String basePackage = "io.game";
    final Map<Integer, ApiMethod> tables = new NonBlockingHashMap<>(128);
    @Resource
    private SessionsManager sessionsManager;

    @Override
    public Command processRequest(ChannelHandlerContext ctx, Command command) throws Exception {
        final long playerId = SessionAttr.playerId(ctx.channel());
        Session session = sessionsManager.getSession(playerId);
        if (null == session || playerId != session.getId()) {
            ctx.close();
            return null;
        }

        ThreadContext.bind(session);
        ThreadContext.ctx(ctx);

        ApiMethod api = tables.get(command.getCmd());
        if (null == api) {
            return null;
        }

        Object result = null;
        Method method = api.getMethod();
        try {
            if (Void.class.isAssignableFrom(api.getCls())) {
                result = method.invoke(api.getTarget());
            } else {
                result = method.invoke(api.getTarget(), DataCodecKit2.decode(command.getData(), api.getCls()));
            }
        } catch (Exception e) {
            log.error("请求处理异常：{}", command.getCode(), e);
            return null;
        }

        if (null != result) {
            if (result instanceof FloatMsg) {
                command.setCode(11001);
            }
            command.setData(DataCodecKit.encode(result));
            command.setId(session.getRequestId());
            return command;
        }
        return null;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (!clazz.getPackageName().startsWith(basePackage)) {
            return bean;
        }

        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            Api m = AnnotationUtils.getAnnotation(method, Api.class);
            if (m != null) {
                var bulider = ApiMethod.builder()
                        .ack(m.ack())
                        .method(method)
                        .cls(Void.class)
                        .target(bean);
                if (method.getParameterTypes().length >= 1) {
                    bulider.cls(method.getParameterTypes()[0]);
                }
                tables.put(m.value(), bulider.build());
            }
        }
        return bean;
    }
}
