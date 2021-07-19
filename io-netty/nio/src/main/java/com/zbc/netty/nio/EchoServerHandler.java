package com.zbc.netty.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3> 未解决分包问题
 * <p>
 * create: 2020/9/24 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(EchoServerHandler.class);
    private String msg;

    /**
     * 接收数据时会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将消息记录到控制台
        ByteBuf buf = (ByteBuf) msg;
        this.msg = buf.toString(CharsetUtil.UTF_8);
        log.debug("Server received: " + this.msg);
        ctx.write(buf);
    }

    /**
     * 建立新连接时会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Server listen socket");
    }

    /**
     * 当数据流读取完时会触发
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture future = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
        if ("done".equals(msg)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 读取数据流期间发生异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
