package com.zbc.netty.nio.http.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

public class WebSocketServer {

    public static void main(String[] args) throws Exception {
        int portNumber = 8080;
        new WebSocketServer().listen(portNumber);
    }

    private void listen(int portNumber) throws Exception {
        EventLoopGroup parent = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parent, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 120)
                    .localAddress(new InetSocketAddress(portNumber))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-codec", new HttpServerCodec())
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    .addLast("http-chunked", new ChunkedWriteHandler())
                                    .addLast("sock-handler", new WebSocketServerHandler());
                        }
                    }).bind()
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
}
