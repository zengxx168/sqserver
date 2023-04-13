package io.game.sq.bootstrap.processors;

import io.game.sq.bootstrap.netty.NettyRequestProcessor;
import io.game.sq.bootstrap.protocols.Command;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component("connectProcessor")
public class ConnectProcessor implements NettyRequestProcessor {

    @Override
    public Command processRequest(ChannelHandlerContext ctx, Command request) throws Exception {
        return null;
    }
}