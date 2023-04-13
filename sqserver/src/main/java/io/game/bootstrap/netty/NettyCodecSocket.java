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
package io.game.bootstrap.netty;

import io.game.bootstrap.protocols.Command;
import io.game.bootstrap.serializer.DataCodecKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * tcp socket  编解码
 *
 * @author zengxx
 * @date 2022-04-13
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyCodecSocket extends MessageToMessageCodec<ByteBuf, Command> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, List<Object> out) throws Exception {
        // 【对外服】 发送消息 给 游戏客户端
        if (Objects.isNull(command)) {
            throw new Exception("The encode command is null");
        }

        // 编码器 ---> 字节数组
        byte[] data = DataCodecKit.encode(command);
        // 使用默认 buffer 。如果没有做任何配置，通常默认实现为池化的 direct （直接内存，也称为堆外内存）
        ByteBuf buffer = ctx.alloc().directBuffer(data.length + 4);
        // 消息长度
        buffer.writeInt(data.length);
        // 消息
        buffer.writeBytes(data);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //不可读与长度不足4字节，本次不读取
        if (!in.isReadable() || in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        // 消息
        byte[] data = new byte[length];
        in.readBytes(data);

        // 【对外服】 接收 游戏客户端的消息
        Command message = DataCodecKit.decode(data, Command.class);
        out.add(message);
        in.discardReadBytes();
    }
}
