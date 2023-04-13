/*
 * # cangling.com . zengxx
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 */
package io.game.sq.bootstrap.sessions.domain;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 扩展属性相关
 *
 * @author zengxx
 * @date 2022-01-11
 */
public class SessionAttr {
    /** false : 没有进行身份验证 */
    public final static AttributeKey<Long> playerId = AttributeKey.valueOf("player_id");
    /**
     * 给用户绑定逻辑服
     * <pre>
     *     之后与该逻辑服有关的请求，都会分配给这个逻辑服来处理。
     *     意思是无论启动了多少个同类型的逻辑服，都会给到这个逻辑服来处理。
     * </pre>
     */
    public final static AttributeKey<Integer> serverId = AttributeKey.valueOf("server_id");
    /** 用户 session，与channel是 1:1 的关系 */
    public final static AttributeKey<Session> session = AttributeKey.valueOf("user_session");

    public static void playerId(Channel channel, long username) {
        channel.attr(playerId).set(username);
    }

    public static long playerId(Channel channel) {
        if (channel.hasAttr(playerId)) {
            return (long) channel.attr(playerId).get();
        }
        return 0;
    }
}