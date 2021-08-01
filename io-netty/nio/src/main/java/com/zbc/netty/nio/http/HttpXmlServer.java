package com.zbc.netty.nio.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;

public class HttpXmlServer {

    public static void main(String[] args) throws Exception {
        int portNumber = 8080;
        new HttpXmlServer().listen(portNumber);
    }

    private void listen(int portNumber) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group, workerGroup)
                    .localAddress(new InetSocketAddress(portNumber))
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 120)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder())
                                    .addLast("http-encoder", new HttpResponseEncoder())
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    .addLast("http-xmlHandler", new HttpXmlHandler());
                        }
                    }).bind()
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();


        } finally {
            group.shutdownGracefully()
                    .sync();
            workerGroup.shutdownGracefully()
                    .sync();
        }
    }
}
