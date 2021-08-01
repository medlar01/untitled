package com.zbc.protocol;

import com.zbc.protocol.hdl.HeartBeatReqHandler;
import com.zbc.protocol.hdl.HeartBeatRespHandler;
import com.zbc.protocol.hdl.LoginAuthReqHandler;
import com.zbc.protocol.hdl.LoginAuthRespHandler;
import com.zbc.protocol.msg.NettyMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ProtocolServer {
    private static final Logger log = LoggerFactory.getLogger(ProtocolServer.class);
    private final ExecutorService executor = new ScheduledThreadPoolExecutor(2);

    public static void main(String[] args) throws Exception {
        int portNumber0 = Integer.parseInt(args[0]);
        int portNumber1 = Integer.parseInt(args[1]);
        ProtocolServer server = new ProtocolServer();
        CountDownLatch latch = new CountDownLatch(1);
        server.start(() -> server.listen0(portNumber0));
        server.start(() -> server.listen1(portNumber1));
        latch.await();
    }

    public int listen0(int portNumber) throws Exception {
        EventLoopGroup parent = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parent, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 120)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufDecoder(NettyMsg.getDefaultInstance()))
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new LoginAuthRespHandler())
                                    .addLast(new HeartBeatRespHandler())
                            ;
                        }
                    }).bind(new InetSocketAddress(portNumber))
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            parent.shutdownGracefully()
                    .sync();
            worker.shutdownGracefully()
                    .sync();
        }
        return -1;
    }

    public int listen1(int portNumber) throws Exception {
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 120)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufDecoder(NettyMsg.getDefaultInstance()))
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new LoginAuthReqHandler())
                                    .addLast(new HeartBeatReqHandler())
                            ;
                        }
                    }).connect(new InetSocketAddress(portNumber))
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            worker.shutdownGracefully()
                    .sync();
            // 断线重连
            if (Thread.activeCount() > 1) {
                Thread.sleep(5000);
                log.error("server disconnect! try connect ...");
                listen1(portNumber);
            }
        }
        return -1;
    }

    private void start(Callable<?> callable) {
        this.executor.submit(callable);
    }
}
