/*
 * # cangling.com . zengxx
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.game.bootstrap.sessions.domain;

import io.game.bootstrap.protocols.Command;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户的唯一 session 信息
 * 与 channel 是 1:1 的关系，可取到对应的 user、channel 等信息
 *
 * @author zengxx
 * @date 2022-03-15
 */
@Getter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session implements IUser {
    private AtomicInteger count = new AtomicInteger(1);

    /** user channel */
    final Channel channel;
    /** userId */
    long user;
    /**
     * 所属Appid
     */
    @Setter
    int appid;
    /** 所有区服 */
    @Setter
    long serverId;
    /** 用户状态 */
    @Setter
    SessionState state;

    public Session(Channel channel) {
        this.channel = channel;
        this.state = SessionState.ACTIVE;
    }

    /**
     * 设置当前用户（玩家）的 id
     * <pre>
     *     当设置好玩家 id ，也表示着已经身份验证了（表示登录过了）。
     * </pre>
     *
     * @param user userId
     */
    public void setUser(long user) {
        this.user = user;
        this.channel.attr(SessionAttr.playerId).set(user);
    }

    @Override
    public long getId() {
        return user;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    /**
     * 获取玩家ip
     *
     * @return 获取玩家ip
     */
    public String getIp() {
        if (Boolean.FALSE.equals(isActive())) {
            return "";
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        return inetSocketAddress.getHostString();
    }

    @Override
    public int getMapId() {
        return 0;
    }

    @Override
    public void sendMessage(Command command) {
        this.channel.writeAndFlush(command);
    }

    public ChannelId channelId() {
        String channelId = this.getChannelId();
        return new ChannelId(channelId);
    }

    public <T> Attribute<T> attr(AttributeKey<T> key, T object) {
        Attribute<T> attr = this.channel.attr(key);
        attr.set(object);
        return attr;
    }

    public <T> T attr(AttributeKey<T> key) {
        Attribute<T> attr = this.channel.attr(key);
        return attr.get();
    }

    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.channel.hasAttr(key);
    }

    /**
     * 是否进行身份验证
     *
     * @return true 已经身份验证了，表示登录过了。
     */
    public boolean isVerify() {
        return this.channel.hasAttr(SessionAttr.playerId);
    }

    public boolean isActive() {
        return Objects.nonNull(channel) && channel.isActive();
    }

    private String getChannelId() {
        return this.channel.id().asLongText();
    }

    public int getRequestId() {
        if (count.get() > 65530) {
            count.set(1);
        }
        return count.getAndIncrement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Session that)) {
            return false;
        }

        return user == that.user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
