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
package io.game.sq.bootstrap.heart;

import io.game.sq.bootstrap.protocols.Command;
import io.game.sq.bootstrap.protocols.Heart;
import io.game.sq.bootstrap.serializer.DataCodecKit;
import io.game.sq.bootstrap.sessions.domain.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认的心跳事件回调，只做简单打印
 *
 * @author zengxx
 * @date 2022-03-14
 */
@Slf4j
public class IdleHookDefault implements IdleHook {
    final Heart heart = new Heart();

    @Override
    public boolean callback(ChannelHandlerContext ctx, IdleStateEvent event, Session session) {
        IdleState state = event.state();
        if (state == IdleState.READER_IDLE) {
            /* 读超时 */
            log.debug("READER_IDLE 读超时");
        } else if (state == IdleState.WRITER_IDLE) {
            /* 写超时 */
            log.debug("WRITER_IDLE 写超时");
        } else if (state == IdleState.ALL_IDLE) {
            /* 总超时 */
            log.debug("ALL_IDLE 总超时");
        }

        Command command = new Command();
        command.setId(session.getRequestId());
        command.setCmd(1003); //心跳码
        command.setCode(10004); //长时间无心跳
        heart.setT((int) (System.currentTimeMillis() / 1000));
        command.setData(DataCodecKit.encode(heart));
        // 通知客户端，触发了心跳事件
        ctx.writeAndFlush(command);
        return true;
    }
}
