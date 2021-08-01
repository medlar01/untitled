package com.zbc.netty.nio.marshalling;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ServerHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ServerHandler.class);
    private Integer count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("读取数据中...");
        SubscribeReq sr = (SubscribeReq) msg;
        log.debug("接收到数据: " + sr);
        count = sr.getSubReqId();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端连接成功!");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (count >= 100) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
            ctx.channel()
                    .parent()
                    .close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常: " + cause.getMessage());
        ctx.close();
    }
}
