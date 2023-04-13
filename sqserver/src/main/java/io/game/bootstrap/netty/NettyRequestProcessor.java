/**
 * Copyright (c) 2011-2019 All Rights Reserved.
 */
package io.game.bootstrap.netty;

import io.game.bootstrap.protocols.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zengxx
 * @version $Id: NettyRequestProcessor.java 2019年12月14日 下午1:34:22 $
 */
public interface NettyRequestProcessor {
    public static Logger log = LoggerFactory.getLogger(NettyRequestProcessor.class);

    /**
     * 消息处理类
     *
     * @param ctx
     * @param request
     * @throws Exception
     */
    Command processRequest(ChannelHandlerContext ctx, Command request) throws Exception;

}
