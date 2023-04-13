/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.sq.bootstrap.netty;

import io.game.sq.bootstrap.protocols.Command;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version $Id: NettyRemotingAbstract.java 2021年6月15日 下午2:16:38 $
 */
@Slf4j
public abstract class NettyRemotingAbstract {
    protected final Map<Integer/* request code */, NettyRequestProcessor> processorTable = new HashMap<Integer, NettyRequestProcessor>(8);

    public void processMessageReceived(ChannelHandlerContext ctx, Command cmd) {
        try {
            NettyRequestProcessor processor = this.processorTable.get(cmd.getCmd());
            if (null == processor) {
                processor = this.processorTable.get(0);
            }
            Command response = processor.processRequest(ctx, cmd);
            if (null != response) {
                ctx.writeAndFlush(response);
            } else if (ctx.channel().isActive()) {
                ctx.flush();
            }
        } catch (Exception e) {
            log.error("process request exception,cmd:" + cmd.getCmd(), e);
        }
    }
}
