package io.game.sq.httpsrv;

import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import io.game.sq.httpsrv.filter.Interceptor;
import io.game.sq.web.domain.ApiResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ResourceLeakDetector;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class HttpServer implements BeanPostProcessor {
    private final String format = "method=%s|v=%s";
    private final String format2 = "%s|%s";

    @Setter
    private int port;
    private final Map<String, HandlerMethod> handlerMappings = new HashMap<>(128);
    // 拦截器，最大支持16个
    private final List<Interceptor> interceptors = new ArrayList<>(16);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final EventLoopGroup bizGroup;

    public HttpServer() {
        if (isLinux()) {
            this.bossGroup = new EpollEventLoopGroup(1);
            this.workerGroup = new EpollEventLoopGroup(128);
            this.bizGroup = new EpollEventLoopGroup(64);
        } else {
            this.bossGroup = new NioEventLoopGroup(1);
            this.workerGroup = new NioEventLoopGroup(8);
            this.bizGroup = new NioEventLoopGroup(64);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (!beanClass.getPackageName().startsWith("io.game.sq")) {
            log.info("bean: {}", beanClass.getName());
            return bean;
        }

        // 拦截器
        if (bean instanceof Interceptor) {
            Order index = beanClass.getAnnotation(Order.class);
            interceptors.add(index.value(), (Interceptor) bean);
        }

        // 请求映射
        if (beanClass.isAnnotationPresent(RestController.class)) {
            Method[] methods = beanClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping requestMapping = method.getAnnotation(PostMapping.class);
                    String key = this.key(requestMapping.params());
                    HandlerMethod handlerMethod = new HandlerMethod(bean, method);
                    handlerMappings.put(key, handlerMethod);
                }
            }
        }
        return bean;
    }

    public void start() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(isLinux() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new ChunkedWriteHandler());
                            p.addLast(bizGroup, new NettyHttpServerHandler());
                            // 禁用Nagle算法
                            ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
            ChannelFuture f = b.bind(port).sync();
            log.info("HttpServer 启动成功，开放端口：{}", port);
        } catch (Exception e) {
            log.error("启动异常", e);
        }
    }

    @PreDestroy
    public void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    private boolean isLinux() {
        return SystemUtil.getOsInfo().isLinux();
    }

    private String key(String[] params) {
        if (params[0].startsWith("method=")) {
            return String.format(format2, params[0], params[1]);
        }
        return String.format(format2, params[1], params[0]);
    }

    class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        ExecutorService executor = Executors.newFixedThreadPool(1024);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
            // 获取请求路径和 HTTP 方法
            Map<String, String> params = getParams(request);
            executor.submit(() -> {
                FullHttpResponse response = null;
                try {
                    for (Interceptor interceptor : interceptors) {
                        if (!interceptor.validate(params, request.headers())) {
                            ApiResponse returnValue = new ApiResponse("500").setMessage("参数校验失败");
                            response = buildResponse(returnValue);
                            return;
                        }
                    }
                    // 查找匹配的处理器
                    HandlerMethod handler = handlerMappings.get(key(params));
                    if (handler == null) {
                        // 没有匹配的处理器，返回 404 Not Found
                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                        return;
                    }

                    // 构造请求和响应对象
                    Object controller = handler.getBean();
                    Method method = handler.getMethod();
                    Object[] args = getMethodArguments(params, method);
                    // 调用处理器方法
                    Object returnValue = method.invoke(controller, args);
                    // 根据返回值构造响应
                    response = buildResponse(returnValue);
                } catch (Exception e) {
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                } finally {
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
            });
        }

        private Map<String, String> getParams(FullHttpRequest request) {
            Map<String, String> params = new HashMap<>();
            QueryStringDecoder query = new QueryStringDecoder(request.uri());
            Map<String, List<String>> parameters = query.parameters();
            if (parameters != null && !parameters.isEmpty()) {
                parameters.forEach((k, v) -> {
                    params.put(k, v.get(0));
                });
            }

            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> httpPostData = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : httpPostData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    params.put(attribute.getName(), attribute.getValue());
                }
            }
            decoder.destroy();
            return params;
        }

        private String key(Map<String, String> params) {
            String method = params.get("method");
            String v = params.get("v");
            return String.format(format, method, v);
        }

        private Object[] getMethodArguments(Map<String, String> params, Method method) {
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            int index = 0;
            for (Parameter parameter : parameters) {
                String paramName = parameter.getName();
                String paramValue = params.get(paramName);
                args[index++] = convertParameterValue(paramValue, parameter.getType());
            }
            return args;
        }

        private Object convertParameterValue(String paramValue, Class<?> parameterType) {
            if (String.class.equals(parameterType)) {
                return paramValue;
            }
            if (int.class.equals(parameterType) || Integer.class.equals(parameterType)) {
                return Integer.valueOf(paramValue);
            }
            if (long.class.equals(parameterType) || Long.class.equals(parameterType)) {
                return Long.valueOf(paramValue);
            }
            if (float.class.equals(parameterType) || Float.class.equals(parameterType)) {
                return Float.valueOf(paramValue);
            }
            if (double.class.equals(parameterType) || Double.class.equals(parameterType)) {
                return Double.valueOf(paramValue);
            }
            if (boolean.class.equals(parameterType) || Boolean.class.equals(parameterType)) {
                return Boolean.valueOf(paramValue);
            }
            return null;
        }

        private FullHttpResponse buildResponse(Object returnValue) {
            if (returnValue == null) {
                // 返回空响应
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            }
            if (returnValue instanceof String) {
                // 返回字符串响应
                String content = (String) returnValue;
                ByteBuf buf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                return response;
            }
            // 返回 JSON 响应
            String content = JSON.toJSONString(returnValue);
            ByteBuf buf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            return response;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}



