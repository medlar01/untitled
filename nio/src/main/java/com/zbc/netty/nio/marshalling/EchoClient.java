package com.zbc.netty.nio.marshalling;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * <H3>  </H3>
 * <p>
 * create: 2021/3/23 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
public class EchoClient {

    public static void main(String[] args) throws Exception {
        int portNumber = 8080;
        new EchoClient().listen(portNumber);
    }

    private void listen(int portNumber) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bp = new Bootstrap();
            bp.group(group)
                    .remoteAddress(new InetSocketAddress(portNumber))
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    }).connect()
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
