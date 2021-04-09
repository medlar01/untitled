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

import java.net.ConnectException;
import java.net.InetSocketAddress;

public class ProtocolServer {

    public static void main(String[] args) throws Exception {
        int portNumber0 = Integer.parseInt(args[0]);
        int portNumber1 = Integer.parseInt(args[1]);
        ProtocolServer server = new ProtocolServer();
        Thread td1 = new Thread(() -> {
            try {
                server.listen0(portNumber0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread td2 = new Thread(() -> {
            try {
                server.listen1(portNumber1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        td1.start();
        td2.start();
        td1.join();
        td2.join();
    }

    public void listen0(int portNumber) throws Exception {
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
                                    .addLast(new LoginAuthReqHandler())
                                    .addLast(new LoginAuthRespHandler())
                                    .addLast(new HeartBeatReqHandler())
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
    }

    private int count = 10;

    public void listen1(int portNumber) throws Exception {
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
                                    .addLast(new LoginAuthRespHandler())
                                    .addLast(new HeartBeatReqHandler())
                                    .addLast(new HeartBeatRespHandler())
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
            if (0 < count --) {
                Thread.sleep(5000);
                listen1(portNumber);
            } else {
                System.err.println(portNumber + " connect err!");
            }
        }
    }
}
