/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.bootstrap.netty;

import cn.hutool.system.SystemUtil;
import io.game.bootstrap.RemotingServer;
import io.game.bootstrap.heart.IdleHook;
import io.game.bootstrap.heart.IdleHookDefault;
import io.game.bootstrap.protocols.Command;
import io.game.bootstrap.protocols.Heart;
import io.game.bootstrap.serializer.DataCodecKit;
import io.game.bootstrap.sessions.SessionsManager;
import io.game.bootstrap.sessions.domain.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Administrator
 * @version $Id: NettyRemotingServer.java 2021年6月15日 下午2:16:24 $
 */
@Slf4j
public class NettyRemotingServer extends NettyRemotingAbstract implements RemotingServer {
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final EventLoopGroup bizGroup = new NioEventLoopGroup(1024);

    private final int timeoutSeconds = 60;
    private final int SO_BUF_SIZE = 128 * 1024;
    /**
     * 默认数据包最大 1MB
     */
    int packageMaxSize = 1024 * 1024;
    private int port = 22020;

    /**
     * 心跳钩子事件回调， 如果对触发心跳有特殊的业务，用户可以重写这个接口
     */
    private IdleHook idleHook = new IdleHookDefault();
    private NettyServerHandler serverHandler = new NettyServerHandler();
    @Setter
    private SessionsManager sessionManager;

    public NettyRemotingServer(int port) {
        this.port = port;
        this.serverBootstrap = new ServerBootstrap();
        if (isLinux()) {
            this.bossGroup = new EpollEventLoopGroup(1);
            this.workerGroup = new EpollEventLoopGroup(128);
        } else {
            this.bossGroup = new NioEventLoopGroup(1);
            this.workerGroup = new NioEventLoopGroup(8);
        }
    }

    @Override
    public void start() {
        this.serverBootstrap.group(this.bossGroup, this.workerGroup)
                .channel(isLinux() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, Integer.MAX_VALUE).option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_SNDBUF, SO_BUF_SIZE).option(ChannelOption.SO_RCVBUF, SO_BUF_SIZE)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("idleStateHandler", new IdleStateHandler(0, 0, timeoutSeconds))
                                // 数据包长度 = 长度域的值 + lengthFieldOffset + lengthFieldLength + lengthAdjustment。
                                .addLast(new LengthFieldBasedFrameDecoder(packageMaxSize,
                                        // 长度字段的偏移量， 从 0 开始
                                        0,
                                        // 字段的长度, 如果使用的是 int   ，占用4位；（消息头用的 byteBuf.writeInt   来记录长度的）
                                        4,
                                        // 要添加到长度字段值的补偿值：长度调整值 = 内容字段偏移量 - 长度字段偏移量 - 长度字段的字节数
                                        0,
                                        // 跳过的初始字节数： 跳过0位; (跳过消息头的 0 位长度)
                                        0))
                                // tcp socket 编解码
                                .addLast("codec", new NettyCodecSocket())
                                .addLast("idleHandler", new IdleHandler(idleHook))
                                .addLast(bizGroup, serverHandler);
                    }
                });

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("broker client 启动游戏Socket服务， 端口: {}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("启用失败", e);
        }
    }

    @Override
    public void registerProcessor(int requestCode, NettyRequestProcessor processor) {
        this.processorTable.put(requestCode, processor);
    }

    @Override
    public void shutdown() {
        try {
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("NettyRemotingServer shutdown exception", e);
        }
    }

    private boolean isLinux() {
        return SystemUtil.getOsInfo().isLinux();
    }

    @ChannelHandler.Sharable
    class NettyServerHandler extends SimpleChannelInboundHandler<Command> {
        private ExecutorService executor = Executors.newFixedThreadPool(1024);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Command command) {
            if (1003 == command.getCmd()) {
                processMessageReceived(ctx, command);
                return;
            }

            executor.submit(() -> {
                processMessageReceived(ctx, command);
            });
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            // 从 session 管理中移除
            Session session = sessionManager.getSession(ctx);
            if (null != session && ctx.channel().id() == session.getChannel().id()) {
                sessionManager.removeSession(session);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            // 加入到 session 管理
            sessionManager.add(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 从 session 管理中移除
            Session session = sessionManager.getSession(ctx);
            if (null != session && ctx.channel().id() == session.getChannel().id()) {
                sessionManager.removeSession(session);
            }
        }
    }

    /**
     * 心跳 handler
     *
     * @author zengxx
     * @date 2022-03-13
     */
    @ChannelHandler.Sharable
    class IdleHandler extends ChannelInboundHandlerAdapter {
        /** 心跳事件回调 */
        final IdleHook idleHook;
        final Heart heart = new Heart();

        public IdleHandler(IdleHook idleHook) {
            this.idleHook = idleHook;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // 心跳处理 1003
            Command command = (Command) msg;
            int cmd = command.getCmd();
            if (cmd == 1003) {
                heart.setT((int) (System.currentTimeMillis() / 1000));
                command.setData(DataCodecKit.encode(heart));
                ctx.writeAndFlush(command);
                return;
            }

            // 不是心跳请求. 交给下一个业务处理 (handler) , 下一个业务指的是你编排 handler 时的顺序
            ctx.fireChannelRead(msg);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent event) {
                boolean close = true;
                var userSession = sessionManager.getSession(ctx);
                // 执行用户定义的心跳回调事件处理
                if (Objects.nonNull(idleHook)) {
                    close = idleHook.callback(ctx, event, userSession);
                }
                // close ctx
                if (close) {
                    sessionManager.removeSession(userSession);
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
