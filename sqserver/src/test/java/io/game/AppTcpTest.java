package io.game;

import io.game.bootstrap.protocols.Command;
import io.game.bootstrap.protocols.Heart;
import io.game.bootstrap.serializer.DataCodecKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模拟App长连接
 *
 * @author zengxx
 * @date 2022-05-16
 */
public class AppTcpTest {
    private static AtomicInteger count = new AtomicInteger(1);
    static int PACKAGE_MAX_SIZE = 1024 * 1024;
    public static int socketSndbufSize = 65535;
    public static int socketRcvbufSize = 65535;
    static Bootstrap bootstrap = new Bootstrap();
    static EventLoopGroup eventLoopGroupWorker = new NioEventLoopGroup(1);
    private static Timer timer = new Timer();

    private static Channel channel;

    public static void main(String[] args) throws Exception {
        NettyEncoder encoder = new NettyEncoder();

        bootstrap.group(eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_SNDBUF, socketSndbufSize).option(ChannelOption.SO_RCVBUF, socketRcvbufSize)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(PACKAGE_MAX_SIZE, 0, 4, 0, 0));
                        pipeline.addLast(encoder, new NettyDecoder(), new IdleStateHandler(60, 0, 0), new NettyClientHandler());
                    }
                });

        ChannelFuture connect = bootstrap.connect("127.0.0.1", 22020);
        connect.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    channel = channelFuture.channel();
                }
            }
        }).sync();

        Command cmd = new Command();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                cmd.setCmd(10001);
                cmd.setCmd(1003);
                cmd.setId(count.getAndIncrement());

                Heart heart = new Heart();
                heart.setT((int) System.currentTimeMillis() / 1000);

//                UserGetReq request = new UserGetReq();
//                request.setId(cmd.getId());
//                byte[] data = DataCodecKit.encode(request);

                byte[] data = DataCodecKit.encode(heart);
                cmd.setData(data);
                if (!channel.isActive()) {
                    System.out.println("连接被关闭........");
                }
                channel.writeAndFlush(cmd);
            }
        }, 100, 1000);

    }
}

class NettyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
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
        list.add(message);
        in.discardReadBytes();
    }
}

@Slf4j
@ChannelHandler.Sharable
class NettyEncoder extends MessageToByteEncoder<Command> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
        // 【对外服】 发送消息 给 游戏客户端
        if (Objects.isNull(command)) {
            throw new Exception("The encode command is null");
        }

        try {
            byte[] data = DataCodecKit.encode(command);
            // 使用默认 buffer 。如果没有做任何配置，通常默认实现为池化的 direct （直接内存，也称为堆外内存）
            ByteBuf buffer = ctx.alloc().directBuffer(data.length + 4);
            // 消息长度
            buffer.writeInt(data.length);
            // 消息
            buffer.writeBytes(data);
            out.writeBytes(buffer);
            buffer.release();
        } catch (Exception e) {
            log.error("error", e);
        }
    }
}

@ChannelHandler.Sharable
class NettyClientHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        Heart rsp = DataCodecKit.decode(command.getData(), Heart.class);
        System.out.println("2.收到服务器返回数据：" + command + ", 内容：" + rsp.getT() + ", 耗时: " + (System.currentTimeMillis() - rsp.getT()));
    }
}