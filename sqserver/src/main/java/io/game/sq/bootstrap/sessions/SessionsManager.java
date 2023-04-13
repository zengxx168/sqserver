/*
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
 */
package io.game.sq.bootstrap.sessions;

import io.game.sq.bootstrap.protocols.Command;
import io.game.sq.bootstrap.sessions.domain.ChannelId;
import io.game.sq.bootstrap.sessions.domain.Session;
import io.game.sq.bootstrap.sessions.domain.SessionAttr;
import io.game.sq.bootstrap.sessions.domain.SessionState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashMapLong;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 用户 session 管理器
 * <pre>
 *     对所有用户Session的管理，统计在线用户等
 * </pre>
 *
 * @author zengxx
 * @date 2022-01-11
 */
@Slf4j
@Component("sessionManager")
public class SessionsManager {
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /**
     * key : 玩家 id
     * value : Session
     */
    private NonBlockingHashMapLong<Session> sessions = new NonBlockingHashMapLong<>();
    /**
     * key : channelId
     * value : Session
     */
    private Map<ChannelId, Session> connections = new NonBlockingHashMap<>();

    /**
     * 获取 Session
     *
     * @param ctx ctx
     * @return Session
     */
    public Session getSession(ChannelHandlerContext ctx) throws RuntimeException {
        return this.getSession(ctx.channel());
    }

    /**
     * 获取 Session
     *
     * @param channel channel
     * @return Session
     */
    public Session getSession(Channel channel) throws RuntimeException {
        Session session = channel.attr(SessionAttr.session).get();
        if (Objects.isNull(session)) {
            throw new RuntimeException("session 不存在，请先加入");
        }
        return session;
    }

    /**
     * true 用户存在
     *
     * @param user 用户id
     * @return true 用户存在
     */
    public boolean existUser(long user) {
        return this.sessions.containsKey(user);
    }

    /**
     * 获取 Session
     *
     * @param user user
     * @return Session
     */
    public Session getSession(long user) throws RuntimeException {
        Session session = this.sessions.get(user);
        if (Objects.isNull(session)) {
            throw new RuntimeException("session 不存在，请先登录在使用此方法");
        }
        return session;
    }

    public Session getSession(ChannelId channelId) throws RuntimeException {
        Session session = this.connections.get(channelId);
        if (Objects.isNull(session)) {
            return null;
        }
        return session;
    }

    /**
     * 加入到 session 管理
     *
     * @param ctx ctx
     */
    public void add(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        if (channel.hasAttr(SessionAttr.playerId)) {
            return;
        }

        Session session = new Session(channel);
        ChannelId channelId = session.channelId();
        this.connections.putIfAbsent(channelId, session);
        this.channelGroup.add(channel);
    }

    /**
     * 设置 channel 的 userId
     *
     * @param channelId channelId
     * @param user
     * userId
     * @return true 设置成功
     */
    public Session addUser(ChannelId channelId, long user) {
        Session session = this.getSession(channelId);
        if (Objects.isNull(session)) {
            return null;
        }
        if (Boolean.FALSE.equals(session.isActive())) {
            removeSession(session);
            return null;
        }

        session.setUser(user);
        this.sessions.put(user, session);

        // 上线通知
        if (session.isVerify()) {
            into(session);
        }
        return session;
    }

    /**
     * 移除 Session
     *
     * @param session session
     */
    public void removeSession(Session session) {
        Objects.requireNonNull(session);
        if (session.getState() == SessionState.QUIT) {
            return;
        }

        //修改状态
        Channel channel = session.getChannel();
        if (session.getState() == SessionState.ACTIVE && session.isVerify()) {
            session.setState(SessionState.QUIT);
            this.quit(session);
        }

        long userId = session.getUser();
        ChannelId channelId = session.channelId();
        this.sessions.remove(userId);
        this.connections.remove(channelId);
        this.channelGroup.remove(channel);
        // 关闭用户的连接
        channel.close();
    }

    /**
     * 当前在线人数
     *
     * @return 当前在线人数
     */
    public long count() {
        return this.channelGroup.size();
    }

    /**
     * 全员消息广播
     * 消息类型 ExternalMessage
     *
     * @param msg 消息
     */
    public void broadcast(Object msg) {
        channelGroup.writeAndFlush(msg);
    }

    /**
     * 指定成员发送消息
     * @param cmd
     * @param to
     */
    public void sendMessage(Command cmd, long to) {
        Session session = this.getSession(to);
        if (null != session) {
            session.getChannel().writeAndFlush(cmd);
        }
    }

    /**
     * 上线通知。注意：只有进行身份验证通过的，才会触发此方法
     *
     * @param session session
     */
    private void into(Session session) {

    }

    /**
     * 离线通知。注意：只有进行身份验证通过的，才会触发此方法
     *
     * @param session session
     */
    private void quit(Session session) {

    }
}
