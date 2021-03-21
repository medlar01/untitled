package com.zbc.netty.nio.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

import java.net.InetSocketAddress;

public class EchoServer {

    public static void main(String[] args) throws Exception {
        int portNumber = 8080;
        new EchoServer().listen(portNumber);
    }

    private void listen(int portNumber) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .localAddress(new InetSocketAddress(portNumber))
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(ObjectBeanProto.ObjectBean.getDefaultInstance()));
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    }).bind()
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            group.shutdownGracefully()
                    .sync();
        }
    }
}
