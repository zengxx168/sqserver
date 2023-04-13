/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.sq.bootstrap.processors;

import io.game.sq.bootstrap.netty.NettyRequestProcessor;
import io.game.sq.bootstrap.protocols.Command;
import io.game.sq.bootstrap.protocols.Heart;
import io.game.sq.bootstrap.serializer.DataCodecKit;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version $Id: PingreqProcessor.java 2021年6月15日 下午2:39:06 $
 */
@Component("pingreqProcessor")
public class PingreqProcessor implements NettyRequestProcessor {

    @Override
    public Command processRequest(ChannelHandlerContext ctx, Command command) throws Exception {
        Heart heart = new Heart();
        heart.setT((int) (System.currentTimeMillis() / 1000));
        command.setData(DataCodecKit.encode(heart));
        return command;
    }
}
